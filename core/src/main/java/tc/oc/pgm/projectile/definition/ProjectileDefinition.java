package tc.oc.pgm.projectile.definition;

import org.bukkit.Location;
import tc.oc.pgm.api.player.MatchPlayer;

public sealed interface ProjectileDefinition
    permits BlockDisplayProjectileDefinition, ItemDisplayProjectileDefinition, RealEntityProjectileDefinition {
    void launch(MatchPlayer source, Location location);

    BaseProjectileDefinition getBaseDefinition();
}
