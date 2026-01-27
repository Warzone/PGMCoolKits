package tc.oc.pgm.disguise;

import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import tc.oc.pgm.api.player.MatchPlayer;
import tc.oc.pgm.kits.AbstractKit;
import tc.oc.pgm.util.entity.EntitySpecification;

import static tc.oc.pgm.util.nms.NMSHacks.NMS_HACKS;

public class DisguiseKit extends AbstractKit{
  public final EntitySpecification entitySpec;

  public DisguiseKit(EntitySpecification entitySpec) {
    this.entitySpec = entitySpec;
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
    if (dmm != null) dmm.setDisguise(player.getBukkit(), null);
  }

  private void applyKit(MatchPlayer player) {
    LivingEntity entity = entitySpec.spawn(player.getBukkit().getWorld(), player.getBukkit().getLocation());
    NMS_HACKS.setEntityAi(entity, false);

    DisguiseMatchModule dmm = player.getMatch().getModule(DisguiseMatchModule.class);
    if (dmm != null) dmm.setDisguise(player.getBukkit(), entity);
  }
}
