package tc.oc.pgm.projectile.definition;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Explosive;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Projectile;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;
import tc.oc.pgm.api.PGM;
import tc.oc.pgm.api.player.MatchPlayer;
import tc.oc.pgm.projectile.EntityLaunchEvent;
import tc.oc.pgm.util.material.BlockMaterialData;
import tc.oc.pgm.util.nms.NMSHacks;

public record RealEntityProjectileDefinition(
    Class<? extends Entity> entityType,
    BaseProjectileDefinition base,
    boolean applyFireballAcceleration,
    BlockMaterialData blockMaterial,
    Float power
) implements ProjectileDefinition {
    @Override
    public void launch(MatchPlayer source, Location location) {
        Vector velocity = source.getBukkit().getEyeLocation().getDirection().multiply(base.velocity());
        Entity projectile;
        if (Projectile.class.isAssignableFrom(entityType)) {
            projectile = source.getBukkit().launchProjectile(entityType.asSubclass(Projectile.class), velocity);
            if (projectile instanceof Fireball fireball && applyFireballAcceleration) {
                NMSHacks.NMS_HACKS.setFireballDirection(fireball, velocity);
            }
        } else {
            if (FallingBlock.class.isAssignableFrom(entityType)) {
                projectile = blockMaterial.spawnFallingBlock(source.getBukkit().getEyeLocation());
            } else {
                projectile = source.getBukkit().getWorld().spawn(source.getBukkit().getEyeLocation(), entityType);
            }
            projectile.setVelocity(velocity);
        }
        if (power != null && projectile instanceof Explosive) {
            ((Explosive) projectile).setYield(power);
        }
        if (projectile != null) {
            projectile.setMetadata(
                "projectileDefinition", new FixedMetadataValue(PGM.get(), this));
        }

        // If the entity implements Projectile, it will have already generated a
        // ProjectileLaunchEvent.
        // Otherwise, we fire our custom event.
        if (Projectile.class.isAssignableFrom(entityType) && projectile != null) {
            EntityLaunchEvent launchEvent = new EntityLaunchEvent(projectile, source.getBukkit());
            source.getMatch().callEvent(launchEvent);
            if (launchEvent.isCancelled()) {
                projectile.remove();
            }
        }
    }

    @Override
    public BaseProjectileDefinition getBaseDefinition() {
        return base;
    }
}