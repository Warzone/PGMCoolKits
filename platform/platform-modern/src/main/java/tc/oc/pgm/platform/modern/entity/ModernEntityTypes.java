package tc.oc.pgm.platform.modern.entity;

import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Entity;
import tc.oc.pgm.util.entity.EntityTypes;

public class ModernEntityTypes implements EntityTypes {
    public static final ModernEntityTypes INSTANCE = new ModernEntityTypes();

    private ModernEntityTypes() {}

    @Override
    public Class<? extends Entity> getBlockDisplayEntityType() {
        return BlockDisplay.class;
    }
}