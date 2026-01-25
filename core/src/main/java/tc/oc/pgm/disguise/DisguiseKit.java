package tc.oc.pgm.disguise;

import java.util.List;

import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import tc.oc.pgm.api.player.MatchPlayer;
import tc.oc.pgm.kits.AbstractKit;

public class DisguiseKit extends AbstractKit{
  private final LivingEntity disguiseType;
  protected final boolean enabled;

  public  DisguiseKit(LivingEntity mobDisguiseType, boolean enabled) {
    this.disguiseType = mobDisguiseType;
    this.enabled = enabled;
  }

  @Override
  public void applyPostEvent(MatchPlayer player, boolean force, List<ItemStack> displacedItems) {
    applyKit(player, this);
  }

  @Override
  public boolean isRemovable() {
    return true;
  }

  @Override
  public void remove(MatchPlayer player) {
    applyKit(player, null);
  }

  private void applyKit(MatchPlayer player, DisguiseKit kit) {
    DisguiseMatchModule dmm = player.getMatch().getModule(DisguiseMatchModule.class);
    if (dmm != null) dmm.setKit(player.getBukkit(), kit);
  }
}
