package tc.oc.pgm.projectile.definition;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import tc.oc.pgm.api.match.MatchScope;
import tc.oc.pgm.api.player.MatchPlayer;
import tc.oc.pgm.projectile.SimulatedProjectileLauncher;
import tc.oc.pgm.util.material.BlockMaterialData;

import static tc.oc.pgm.util.nms.NMSHacks.NMS_HACKS;

public record BlockDisplayProjectileDefinition(
    BlockMaterialData materialData,
    BaseProjectileDefinition base, SimulatedProjectileLauncher.Options launchOptions
) implements ProjectileDefinition {
    private static final Class<? extends Entity> BLOCK_DISPLAY_ENTITY_TYPE =
        NMS_HACKS.getEntityTypes().getBlockDisplayEntityType();

    @Override
    public void launch(MatchPlayer source, Location location) {
        if (BLOCK_DISPLAY_ENTITY_TYPE == null) return;
        var blockDisplay = location.getWorld().spawn(location, BLOCK_DISPLAY_ENTITY_TYPE);
        NMS_HACKS.getEntityTypes().asBlockDisplay(blockDisplay).setBlock(materialData);
        NMS_HACKS.getEntityTypes().asBlockDisplay(blockDisplay).setTeleportationDuration(1);
        new SimulatedProjectileLauncher(
            this, source.getMatch().getExecutor(MatchScope.RUNNING), launchOptions
        ).launch(blockDisplay, source, location);
    }

    @Override
    public BaseProjectileDefinition getBaseDefinition() {
        return base;
    }
}
