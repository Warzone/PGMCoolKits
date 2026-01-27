package tc.oc.pgm.disguise;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.Nullable;
import tc.oc.pgm.api.PGM;
import tc.oc.pgm.api.match.Match;
import tc.oc.pgm.api.match.MatchModule;
import tc.oc.pgm.api.match.MatchScope;
import tc.oc.pgm.events.ListenerScope;
import tc.oc.pgm.events.PlayerResetEvent;
import tc.oc.pgm.util.bukkit.OnlinePlayerMapAdapter;
import tc.oc.pgm.util.entity.EntitySpecification;

@ListenerScope(MatchScope.RUNNING)
public class DisguiseMatchModule implements MatchModule, Listener {
    private final Match match;
    private final OnlinePlayerMapAdapter<PlayerDisguise> playerDisguises;

    public DisguiseMatchModule(Match match) {
        this.match = match;
        this.playerDisguises = new OnlinePlayerMapAdapter<>(PGM.get());
        this.playerDisguises.enable();
    }

    @Override
    public void disable() {
        this.playerDisguises.clear();
        this.playerDisguises.disable();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerReset(PlayerResetEvent event) {
        this.setDisguise(event.getPlayer().getBukkit(), null);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        this.setDisguise(event.getEntity(), null);
    }

    public void setDisguise(Player player, @Nullable EntitySpecification entitySpec) {
        if (entitySpec != null) {
            PlayerDisguise disguisedPlayer = new PlayerDisguise(
                player, entitySpec, match.getExecutor(MatchScope.LOADED)
            );
            disguisedPlayer.enable();
            this.playerDisguises.put(player, disguisedPlayer);
        } else {
            var disguise = this.playerDisguises.remove(player);
            if (disguise != null) {
                disguise.disable();
            }
        }
    }
}
