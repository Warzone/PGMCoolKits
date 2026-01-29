package tc.oc.pgm.disguise;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.Plugin;
import tc.oc.pgm.api.PGM;
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

    private Future<?> tickFuture;
    private LivingEntity disguise;

    public PlayerDisguise(
        Player player, EntitySpecification entitySpec,
        Plugin plugin, ScheduledExecutorService scheduledExecutorService
    ) {
        this.player = player;
        this.entitySpec = entitySpec;
        this.plugin = plugin;
        this.scheduledExecutorService = scheduledExecutorService;
    }

    public void enable() {
        if (disguise != null) return;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        LivingEntity entity = entitySpec.spawn(player.getWorld(), player.getLocation());
        NMS_HACKS.setEntityAi(entity, false);
        NMS_HACKS.hideEntityForPlayer(PGM.get(), player, entity);
        disguise = entity;
        tickFuture = scheduledExecutorService.scheduleAtFixedRate(this::tick, 0L, 50L, TimeUnit.MILLISECONDS);
    }

    public void disable() {
        if (disguise == null) return;
        HandlerList.unregisterAll(this);
        disguise.remove();
        disguise = null;
        tickFuture.cancel(true);
        tickFuture = null;
    }

    private void tick() {
        if (disguise == null) return;
        disguise.teleport(player);
    }

    @EventHandler
    public void onDamage(final EntityDamageByEntityEvent event) {
        if (!event.getEntity().equals(disguise)) return;
        propagateDamage(event.getDamager(), event.getFinalDamage(), event);
    }

    @EventHandler
    public void onOtherDamage(final EntityDamageEvent event) {
        if (!event.getEntity().equals(disguise) || event instanceof EntityDamageByEntityEvent) return;
        propagateDamage(null, event.getFinalDamage(), event);
    }

    @EventHandler
    public void onEntityByBlockDamage(final EntityDamageByBlockEvent event) {
        if (!event.getEntity().equals(disguise)) return;
        propagateDamage(null, event.getFinalDamage(), event);
    }

    private void propagateDamage(Entity damager, double damage, EntityDamageEvent event) {
        disguise.resetMaxHealth();
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
}
