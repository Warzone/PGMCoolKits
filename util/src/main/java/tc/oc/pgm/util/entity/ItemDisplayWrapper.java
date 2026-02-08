package tc.oc.pgm.util.entity;

import org.bukkit.inventory.ItemStack;

public interface ItemDisplayWrapper {
    void setItem(ItemStack item);
    void setTeleportationDuration(int duration);
    void setDisplayContext(DisplayContext displayContext);
    void alignToFacing(float yaw, float pitch);

    enum DisplayContext {
        NONE, FIRST_PERSON_LEFT_HAND, FIRST_PERSON_RIGHT_HAND, FIXED
    }
}
