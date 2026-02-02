package tc.oc.pgm.platform.modern.entity;

import org.bukkit.entity.Entity;
import tc.oc.pgm.util.entity.EntityWrapper;

public class ModernEntityWrapper implements EntityWrapper {
    private final Entity entity;

    public ModernEntityWrapper(Entity entity) {
        this.entity = entity;
    }

    @Override
    public void setSilent(boolean silent) {
        this.entity.setSilent(silent);
    }
}
