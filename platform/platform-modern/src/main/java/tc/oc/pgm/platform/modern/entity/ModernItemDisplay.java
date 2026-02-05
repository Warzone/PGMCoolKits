package tc.oc.pgm.platform.modern.entity;

import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import tc.oc.pgm.util.entity.ItemDisplayWrapper;

public class ModernItemDisplay implements ItemDisplayWrapper {
    private final ItemDisplay itemDisplay;

    public ModernItemDisplay(ItemDisplay itemDisplay) {
        this.itemDisplay = itemDisplay;
    }

    @Override
    public void setItem(ItemStack item) {
        itemDisplay.setItemStack(item);
    }

    @Override
    public void setTeleportationDuration(int duration) {
        itemDisplay.setTeleportDuration(duration);
    }

    @Override
    public void setDisplayContext(DisplayContext displayContext) {
        var context = switch (displayContext) {
            case FIRST_PERSON_LEFT_HAND -> ItemDisplay.ItemDisplayTransform.FIRSTPERSON_LEFTHAND;
            case FIRST_PERSON_RIGHT_HAND -> ItemDisplay.ItemDisplayTransform.FIRSTPERSON_RIGHTHAND;
            default -> ItemDisplay.ItemDisplayTransform.NONE;
        };
        itemDisplay.setItemDisplayTransform(context);
    }
}
