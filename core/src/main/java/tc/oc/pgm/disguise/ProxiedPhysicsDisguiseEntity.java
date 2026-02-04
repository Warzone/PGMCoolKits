package tc.oc.pgm.disguise;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import tc.oc.pgm.api.PGM;
import tc.oc.pgm.api.match.Match;
import tc.oc.pgm.api.player.MatchPlayer;

import static tc.oc.pgm.util.nms.NMSHacks.NMS_HACKS;

public class ProxiedPhysicsDisguiseEntity implements DisguiseEntity {
    private static final Class<? extends Entity> BLOCK_DISPLAY_CLASS =
        NMS_HACKS.getEntityTypes().getBlockDisplayEntityType();
    private final Entity model;
    private final Slime physics;
    private final boolean showNametag;
    private final boolean isBlockDisplay;

    public ProxiedPhysicsDisguiseEntity(Entity model, Slime physics, boolean showNametag) {
        this.model = model;
        this.physics = physics;
        this.showNametag = showNametag;
        this.isBlockDisplay = BLOCK_DISPLAY_CLASS != null && BLOCK_DISPLAY_CLASS.isAssignableFrom(model.getClass());
    }

    @Override
    public void init(Match match, Player player) {
        this.physics.addPotionEffect(
            new PotionEffect(
                PotionEffectType.INVISIBILITY, 1000000, 2, true, false
            )
        );
        NMS_HACKS.getEntityWrapper(physics).setSilent(true);
        NMS_HACKS.setEntityAi(physics, false);
        NMS_HACKS.hideEntityForPlayer(PGM.get(), player, physics);
        if (showNametag) {
            MatchPlayer matchPlayer = match.getPlayer(player);
            String teamColor = matchPlayer.getParty().getColor().toString();
            physics.setCustomName(teamColor + player.getName());
            physics.setCustomNameVisible(true);
        }
        if (isBlockDisplay) {
            NMS_HACKS.getEntityTypes().asBlockDisplay(model).setTranslation(
                -0.5f, 0.0f, -0.5f
            );
        }
    }

    @Override
    public Entity getModelEntity() {
        return model;
    }

    @Override
    public Entity getPhysicsEntity() {
        return physics;
    }

    @Override
    public void syncPos(Player player) {
        Location location = player.getLocation();
        if (isBlockDisplay) {
            location.setYaw(0);
            location.setPitch(0);
        }
        this.model.teleport(location);
        this.physics.teleport(location);
    }

    @Override
    public void postDamageHandler() {
    }

    @Override
    public void unload(Player player) {
        this.model.remove();
        this.physics.remove();
    }
}
