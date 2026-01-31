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
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Team;

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
    private final boolean showNametag;
    private final ScheduledExecutorService scheduledExecutorService;
    private final Plugin plugin;
    private final Match match;

    private Future<?> tickFuture;
    private Entity disguise;

    public PlayerDisguise(
        Player player, EntitySpecification entitySpec, boolean showNametag,
        Plugin plugin, ScheduledExecutorService scheduledExecutorService
    ) {
        this.player = player;
        this.entitySpec = entitySpec;
        this.plugin = plugin;
        this.scheduledExecutorService = scheduledExecutorService;
        this.showNametag = showNametag;
        this.match = PGM.get().getMatchManager().getMatch(player);
    }

    public void enable() {
        if (disguise != null) return;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        Entity entity = entitySpec.spawn(player.getWorld(), player.getLocation());
        NMS_HACKS.setEntityAi(entity, false);
        NMS_HACKS.hideEntityForPlayer(PGM.get(), player, entity);
        if (showNametag) {
            MatchPlayer matchPlayer = match.getPlayer(player);
            String teamColor = matchPlayer.getParty().getColor().toString();
            entity.setCustomName(teamColor + player.getName());
            entity.setCustomNameVisible(true);
        }

        double targetHeight =  NMS_HACKS.getEntityHeight(entity);
        double playerBaseHeight = 1.8;
        double scaleValue = targetHeight / playerBaseHeight;
        NMS_HACKS.setPlayerScale(player, scaleValue);
        disguise = entity;
        Team team = player.getScoreboard().getEntryTeam(player.getName());
        team.addEntry(disguise.getUniqueId().toString());

        tickFuture = scheduledExecutorService.scheduleAtFixedRate(this::tick, 0L, 50L, TimeUnit.MILLISECONDS);
    }

    public void disable() {
        if (disguise == null) return;
        HandlerList.unregisterAll(this);
        disguise.remove();
        NMS_HACKS.setPlayerScale(player, 1);
        Team team = player.getScoreboard().getEntryTeam(player.getName());
        team.removeEntry(disguise.getUniqueId().toString());
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
        if (isTeammate(event.getDamager())) {
            event.setCancelled(true);
            return;
        }
        propagateDamage(event.getDamager(), event.getFinalDamage(), event);
    }

    @EventHandler
    public void onOtherDamage(final EntityDamageEvent event) {
        if (!event.getEntity().equals(disguise) || event instanceof EntityDamageByEntityEvent) return;
        if (NMS_HACKS.isPlayerInWall(player)) {
            event.setDamage(0);
        } else {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityByBlockDamage(final EntityDamageByBlockEvent event) {
        if (!event.getEntity().equals(disguise)) return;
        propagateDamage(null, event.getFinalDamage(), event);
    }

    @EventHandler
    public void onProjectileHit(final ProjectileHitEvent event) {
        Entity hitEntity = NMS_HACKS.getHitEntity(event);
        if (!hitEntity.equals(disguise)) return;
        if (isTeammate(event.getEntity())) {
            NMS_HACKS.cancelProjectileHitEvent(event);
        }
    }

    private void propagateDamage(Entity damager, double damage, EntityDamageEvent event) {
        if (disguise instanceof LivingEntity livingDisguise) {
            livingDisguise.resetMaxHealth();
        }
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

    private boolean isTeammate(Entity entity) {
        if (entity instanceof Projectile projectile && projectile.getShooter() instanceof Player) {
            entity = (Player) projectile.getShooter();
        }

        if (!(entity instanceof Player damager)) {
            return false;
        }

        MatchPlayer mpDamager = match.getPlayer(damager);
        MatchPlayer mpVictim = match.getPlayer(player);

        return (mpVictim.getParty() == mpDamager.getParty());
    }
}
