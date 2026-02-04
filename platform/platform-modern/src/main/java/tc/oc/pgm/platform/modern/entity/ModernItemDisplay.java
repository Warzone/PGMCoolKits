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
}
