package tc.oc.pgm.disguise;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import tc.oc.pgm.api.match.Match;
import tc.oc.pgm.util.entity.EntitySpecification;
import tc.oc.pgm.util.entity.ProxiedPhysicsEntitySpecification;
import tc.oc.pgm.util.entity.SimpleEntitySpecification;

public interface DisguiseEntity {
    Entity getModelEntity();
    Entity getPhysicsEntity();
    void syncPos(Player player);
    default void init(Match match, Player player) {}
    default void postDamageHandler() {
        if (getPhysicsEntity() instanceof LivingEntity livingDisguise) {
            livingDisguise.resetMaxHealth();
        }
    }
    default void unload(Player player) {}

    class Factory {
        public static DisguiseEntity spawnFromSpecification(
            final EntitySpecification specification,
            final World world, final Location location
        ) {
            switch (specification) {
                case ProxiedPhysicsEntitySpecification proxiedPhysicsEntitySpecification -> {
                    var model = proxiedPhysicsEntitySpecification.modelEntitySpec().spawn(world, location);
                    var physics = world.spawn(location, Slime.class);
                    physics.setSize(proxiedPhysicsEntitySpecification.slimeSize());
                    return new ProxiedPhysicsDisguiseEntity(
                        model, physics, proxiedPhysicsEntitySpecification.showNametag()
                    );
                }
                case SimpleEntitySpecification simpleEntitySpecification -> {
                    return new StandardDisguiseEntity(
                        simpleEntitySpecification.spawn(world, location),
                        simpleEntitySpecification.showNametag()
                    );
                }
            }
        }
    }
}