package tc.oc.pgm.projectile;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import tc.oc.pgm.api.location.MatchLocation;
import tc.oc.pgm.api.player.MatchPlayer;
import tc.oc.pgm.api.player.PlayerRelation;
import tc.oc.pgm.projectile.definition.ProjectileDefinition;
import tc.oc.pgm.util.MatchPlayers;
import tc.oc.pgm.util.TimeUtils;
import tc.oc.pgm.util.nms.NMSHacks;

import java.time.Duration;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SimulatedProjectileLauncher {
    private final ProjectileDefinition projectileDefinition;
    private final ScheduledExecutorService scheduledExecutorService;
    private int remainingTime;
    private final double velocity;
    private final int substeps;
    private final double halfSize;
    private final Options options;

    private MatchPlayer source;
    private Future<?> task;
    private Entity entity;
    private Location currentLocation;
    private Vector increment;
    private Vector substep;

    public SimulatedProjectileLauncher(
        ProjectileDefinition projectileDefinition,
        ScheduledExecutorService scheduledExecutorService,
        Options options
    ) {
        this.projectileDefinition = projectileDefinition;
        this.scheduledExecutorService = scheduledExecutorService;
        this.options = options;
        this.remainingTime = (int) TimeUtils.toTicks(options.maxTravelTime);
        this.velocity = projectileDefinition.getBaseDefinition().velocity();
        this.substeps = Math.min(
            10, Math.max(1, (int) (velocity / Math.max(0.1, options.size)))
        );
        this.halfSize = 0.5 * options.size;
    }

    public record Options(boolean solidBlockCollision, Duration maxTravelTime, double size) { }

    public void launch(Entity entity, MatchPlayer source, Location location) {
        this.entity = entity;
        this.source = source;
        this.currentLocation = location.clone();
        var normalizedDirection = currentLocation.getDirection().normalize();
        this.increment = normalizedDirection.clone().multiply(velocity);
        this.substep = increment.clone().divide(new Vector(substeps, substeps, substeps));
        if (this.substep.length() < 0.1) this.substep = normalizedDirection.clone().multiply(0.1);
        task = scheduledExecutorService.scheduleAtFixedRate(this::tick, 0L, 50L, TimeUnit.MILLISECONDS);
    }

    private void tick() {
        if (remainingTime-- <= 0) {
            cancel();
            return;
        }
        if (blockCollision() || entityCollision()) {
            cancel();
            return;
        }

        currentLocation.add(increment);
        entity.teleport(currentLocation);
    }

    private boolean blockCollision() {
        if (!options.solidBlockCollision) return false;
        var blockCollisionLocation = NMSHacks.NMS_HACKS.raycastBlock(
            currentLocation, halfSize, increment, substeps, substep
        );
        if (blockCollisionLocation != null) {
            if (projectileDefinition.getBaseDefinition().onHitBlockAction() != null) {
                projectileDefinition.getBaseDefinition().onHitBlockAction().trigger(
                    new MatchLocation(source.getMatch(), blockCollisionLocation)
                );
            }
            return true;
        }
        return false;
    }

    private boolean entityCollision() {
        if (projectileDefinition.getBaseDefinition().damage() == null) return false;
        Entity hitEntity = NMSHacks.NMS_HACKS.raycastEntity(currentLocation, halfSize, increment, this::isEnemyPlayer);
        if (hitEntity != null) {
            if (projectileDefinition.getBaseDefinition().onHitPlayerAction() != null) {
                var hitPlayer = source.getMatch().getPlayer(hitEntity);
                if (hitPlayer != null) {
                    projectileDefinition.getBaseDefinition().onHitPlayerAction().trigger(hitPlayer);
                }
            }
            ((Player) hitEntity).damage(projectileDefinition.getBaseDefinition().damage(), source.getBukkit());
            return true;
        }
        return false;
    }

    private boolean isEnemyPlayer(Entity entity) {
        if (!(entity instanceof Player victim)) {
            return false;
        }
        var mpVictim = source.getMatch().getPlayer(victim);
        return MatchPlayers.canInteract(mpVictim) &&
            PlayerRelation.get(
                mpVictim.getParticipantState(), source.getParticipantState()
            ) == PlayerRelation.ENEMY;
    }

    private void cancel() {
        this.task.cancel(true);
        if (this.entity != null) {
            this.entity.remove();
            this.entity = null;
        }
    }
}
