package tc.oc.pgm.platform.modern.entity;

import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import tc.oc.pgm.util.entity.BlockDisplayWrapper;
import tc.oc.pgm.util.entity.EntityTypes;
import tc.oc.pgm.util.entity.ItemDisplayWrapper;

public class ModernEntityTypes implements EntityTypes {
    public static final ModernEntityTypes INSTANCE = new ModernEntityTypes();

    private ModernEntityTypes() {}

    @Override
    public Class<? extends Entity> getBlockDisplayEntityType() {
        return BlockDisplay.class;
    }

    @Override
    public Class<? extends Entity> getItemDisplayEntityType() {
        return ItemDisplay.class;
    }

    public BlockDisplayWrapper asBlockDisplay(Entity entity) {
        return new ModernBlockDisplay((BlockDisplay) entity);
    }

    @Override
    public ItemDisplayWrapper asItemDisplay(Entity entity) {
        return new ModernItemDisplay((ItemDisplay) entity);
    }
}