package tc.oc.pgm.disguise;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import tc.oc.pgm.util.entity.EntitySpecification;

import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static tc.oc.pgm.util.nms.NMSHacks.NMS_HACKS;

public class PlayerDisguise {
    private final Player player;
    private final EntitySpecification entitySpec;
    private final ScheduledExecutorService scheduledExecutorService;

    private Future<?> tickFuture;
    private Entity disguise;

    public PlayerDisguise(
        Player player, EntitySpecification entitySpec, ScheduledExecutorService scheduledExecutorService
    ) {
        this.player = player;
        this.entitySpec = entitySpec;
        this.scheduledExecutorService = scheduledExecutorService;
    }

    public void enable() {
        if (disguise != null) return;

        LivingEntity entity = entitySpec.spawn(player.getWorld(), player.getLocation());
        NMS_HACKS.setEntityAi(entity, false);
        disguise = entity;
        tickFuture = scheduledExecutorService.scheduleAtFixedRate(this::tick, 0L, 50L, TimeUnit.MILLISECONDS);
    }

    public void disable() {
        if (disguise == null) return;
        disguise.remove();
        disguise = null;
        tickFuture.cancel(true);
        tickFuture = null;
    }

    private void tick() {
        if (disguise == null) return;
        disguise.teleport(player);
    }
}
