package tc.oc.pgm.util.entity;

import org.bukkit.entity.Entity;

public interface EntityTypes {
    Class<? extends Entity> getBlockDisplayEntityType();
}
