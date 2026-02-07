package tc.oc.pgm.disguise;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.plugin.Plugin;
import tc.oc.pgm.api.PGM;
import tc.oc.pgm.api.match.Match;
import tc.oc.pgm.api.player.MatchPlayer;
import tc.oc.pgm.util.entity.EntitySpecification;

import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static tc.oc.pgm.util.nms.NMSHacks.NMS_HACKS;

public class PlayerDisguise implements Listener {
    private final Player player;
    private final EntitySpecification entitySpec;
    private final ScheduledExecutorService scheduledExecutorService;
    private final Plugin plugin;
    private final Match match;

    private Future<?> tickFuture;
    private DisguiseEntity disguise;

    public PlayerDisguise(
        Player player, EntitySpecification entitySpec,
        Plugin plugin, ScheduledExecutorService scheduledExecutorService
    ) {
        this.player = player;
        this.entitySpec = entitySpec;
        this.plugin = plugin;
        this.scheduledExecutorService = scheduledExecutorService;
        this.match = PGM.get().getMatchManager().getMatch(player);
    }

    public void enable() {
        if (disguise != null) return;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        var disguiseEntity = DisguiseEntity.Factory.spawnFromSpecification(
            entitySpec, player.getWorld(), player.getLocation()
        );
        disguiseEntity.init(match, player);
        disguise = disguiseEntity;

        tickFuture = scheduledExecutorService.scheduleAtFixedRate(this::tick, 0L, 50L, TimeUnit.MILLISECONDS);
    }

    public void disable() {
        if (disguise == null) return;
        HandlerList.unregisterAll(this);
        disguise.unload(player);
        disguise = null;
        tickFuture.cancel(true);
        tickFuture = null;
    }

    private void tick() {
        if (disguise == null) return;
        disguise.syncPos(player);
    }

    @EventHandler
    public void onDamage(final EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity victim = event.getEntity();
        if (disguise == null || (damager != disguise.getPhysicsEntity() && victim != disguise.getPhysicsEntity())) return;

        Entity disguiseEntity = disguise.getPhysicsEntity();

        if (damager.equals(disguiseEntity) || isTeammateOrSpectator(damager)) {
            event.setCancelled(true);
            return;
        }

        if (!victim.equals(disguiseEntity)) return;

        propagateDamage(damager, event.getFinalDamage(), event);
    }

    @EventHandler
    public void onOtherDamage(final EntityDamageEvent event) {
        if (disguise == null) return;
        if (!event.getEntity().equals(disguise.getPhysicsEntity()) || event instanceof EntityDamageByEntityEvent) return;
        if (NMS_HACKS.isPlayerInWall(player)) {
            event.setDamage(0);
        } else {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityByBlockDamage(final EntityDamageByBlockEvent event) {
        if (disguise == null) return;
        if (!event.getEntity().equals(disguise.getPhysicsEntity())) return;
        propagateDamage(null, event.getFinalDamage(), event);
    }

    @EventHandler
    public void onProjectileHit(final ProjectileHitEvent event) {
        Entity hitEntity = NMS_HACKS.getHitEntity(event);
        if (hitEntity == null) {
            return;
        }
        if (!hitEntity.equals(disguise.getPhysicsEntity())) return;
        if (isTeammateOrSpectator(event.getEntity())) {
            NMS_HACKS.cancelProjectileHitEvent(event);
        }
    }

    private void propagateDamage(Entity damager, double damage, EntityDamageEvent event) {
        disguise.postDamageHandler();
        if (damager != null) {
            if (damager instanceof Projectile projectile) {
                NMS_HACKS.simulateProjectileHit(projectile, player);
            } else {
                player.damage(damage, damager);
            }
        } else {
            player.damage(damage);
        }
        event.setDamage(0);
    }

    private boolean isTeammateOrSpectator(Entity entity) {
        if (entity instanceof Projectile projectile && projectile.getShooter() instanceof Player) {
            entity = (Player) projectile.getShooter();
        }

        if (!(entity instanceof Player damager)) {
            return false;
        }

        MatchPlayer mpDamager = match.getPlayer(damager);
        MatchPlayer mpVictim = match.getPlayer(player);
        if (!mpDamager.isParticipating()) return true;
        return (mpVictim.getParty() == mpDamager.getParty());
    }
}
