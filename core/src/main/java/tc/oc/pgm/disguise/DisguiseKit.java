package tc.oc.pgm.disguise;

import org.bukkit.inventory.ItemStack;

import tc.oc.pgm.api.player.MatchPlayer;
import tc.oc.pgm.kits.AbstractKit;
import tc.oc.pgm.util.entity.EntitySpecification;

import java.util.List;

public class DisguiseKit extends AbstractKit {
    public final EntitySpecification entitySpec;
    public final boolean showNametag;

    public DisguiseKit(EntitySpecification entitySpec, boolean showNametag) {
        this.entitySpec = entitySpec;
        this.showNametag = showNametag;
    }

    @Override
    public void applyPostEvent(MatchPlayer player, boolean force, List<ItemStack> displacedItems) {
        applyKit(player);
    }

    @Override
    public boolean isRemovable() {
        return true;
    }

    @Override
    public void remove(MatchPlayer player) {
        DisguiseMatchModule dmm = player.getMatch().getModule(DisguiseMatchModule.class);
        if (dmm != null) dmm.setDisguise(player.getBukkit(), null, showNametag);
    }

    private void applyKit(MatchPlayer player) {
        DisguiseMatchModule dmm = player.getMatch().getModule(DisguiseMatchModule.class);
        if (dmm != null) {
            dmm.setDisguise(player.getBukkit(), entitySpec, showNametag);
        }
    }
}
