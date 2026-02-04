package tc.oc.pgm.projectile.definition;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import tc.oc.pgm.api.match.MatchScope;
import tc.oc.pgm.api.player.MatchPlayer;
import tc.oc.pgm.projectile.SimulatedProjectileLauncher;

import static tc.oc.pgm.util.nms.NMSHacks.NMS_HACKS;

public record ItemDisplayProjectileDefinition(
    ItemStack item, BaseProjectileDefinition base,
    SimulatedProjectileLauncher.Options launchOptions
) implements ProjectileDefinition {
    private static final Class<? extends Entity> ITEM_DISPLAY_ENTITY_TYPE =
        NMS_HACKS.getEntityTypes().getItemDisplayEntityType();

    @Override
    public void launch(MatchPlayer source, Location location) {
        if (ITEM_DISPLAY_ENTITY_TYPE == null) return;
        var itemDisplay = location.getWorld().spawn(location, ITEM_DISPLAY_ENTITY_TYPE);
        NMS_HACKS.getEntityTypes().asItemDisplay(itemDisplay).setItem(item);
        NMS_HACKS.getEntityTypes().asItemDisplay(itemDisplay).setTeleportationDuration(1);
        new SimulatedProjectileLauncher(
            this, source.getMatch().getExecutor(MatchScope.RUNNING), launchOptions
        ).launch(itemDisplay, source, location);
    }

    @Override
    public BaseProjectileDefinition getBaseDefinition() {
        return base;
    }
}