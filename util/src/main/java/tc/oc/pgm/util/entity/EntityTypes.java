package tc.oc.pgm.util.entity;

import org.bukkit.entity.Entity;

/**
 * This describes entities not present in the intersected API.
 * These are largely modern entities given net-new entities are generally what would not
 * appear in the intersected API.
 */
public interface EntityTypes {
    default Class<? extends Entity> getBlockDisplayEntityType() {
        return null;
    }

    default Class<? extends Entity> getItemDisplayEntityType() {
        return null;
    }

    default BlockDisplayWrapper asBlockDisplay(Entity entity) {
        return null;
    }

    default ItemDisplayWrapper asItemDisplay(Entity entity) {
        return null;
    }

    default EntityWrapper asModernEntity(Entity entity) {
        return null;
    }
}
