package tc.oc.pgm.disguise;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import tc.oc.pgm.api.PGM;
import tc.oc.pgm.api.match.Match;
import tc.oc.pgm.api.player.MatchPlayer;

import static tc.oc.pgm.util.nms.NMSHacks.NMS_HACKS;

/**
 * A server-side entity that acts as the disguise
 */
public class StandardDisguiseEntity implements DisguiseEntity {
    private final Entity disguise;
    private final boolean showNametag;

    public StandardDisguiseEntity(Entity disguise, boolean showNametag) {
        this.disguise = disguise;
        this.showNametag = showNametag;
    }

    @Override
    public void init(Match match, Player player) {
        NMS_HACKS.setEntityAi(disguise, false);
        NMS_HACKS.hideEntityForPlayer(PGM.get(), player, disguise);
        if (showNametag) {
            MatchPlayer matchPlayer = match.getPlayer(player);
            String teamColor = matchPlayer.getParty().getColor().toString();
            disguise.setCustomName(teamColor + player.getName());
            disguise.setCustomNameVisible(true);
        }

        double targetHeight =  NMS_HACKS.getEntityHeight(disguise);
        double playerBaseHeight = 1.8;
        double scaleValue = targetHeight / playerBaseHeight;
        NMS_HACKS.setPlayerScale(player, scaleValue);
    }

    @Override
    public void syncPos(Player player) {
        disguise.teleport(player);
    }

    @Override
    public Entity getModelEntity() {
        return disguise;
    }

    @Override
    public Entity getPhysicsEntity() {
        return disguise;
    }

    @Override
    public void postDamageHandler() {
    }

    @Override
    public void unload(Player player) {
        NMS_HACKS.setPlayerScale(player, 1);
        this.disguise.remove();
    }
}
