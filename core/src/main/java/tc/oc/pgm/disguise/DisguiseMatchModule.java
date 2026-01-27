package tc.oc.pgm.disguise;

import java.util.Iterator;
import java.util.Map;

import org.bukkit.entity.Entity;
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
import tc.oc.pgm.api.match.Tickable;
import tc.oc.pgm.api.time.Tick;
import tc.oc.pgm.events.ListenerScope;
import tc.oc.pgm.events.PlayerResetEvent;
import tc.oc.pgm.util.bukkit.OnlinePlayerMapAdapter;

@ListenerScope(MatchScope.RUNNING)
public class DisguiseMatchModule implements MatchModule, Listener, Tickable {
    private static class DisguisedPlayer {
        private final Player player;
        private final Entity disguise;

        private DisguisedPlayer(Player player, Entity disguise) {
            this.player = player;
            this.disguise = disguise;
        }
    }

    private final OnlinePlayerMapAdapter<DisguisedPlayer> disguisedPlayers;

    public DisguiseMatchModule(Match match) {
        this.disguisedPlayers = new OnlinePlayerMapAdapter<>(PGM.get());
        this.disguisedPlayers.enable();
    }

    @Override
    public void tick(Match match, Tick tick) {
        for (Map.Entry<Player, DisguisedPlayer> entry : disguisedPlayers.entrySetCopy()) {
            Player player = entry.getKey();
            DisguisedPlayer disguisedPlayer = entry.getValue();
        }
    }

    @Override
    public void disable() {
        this.disguisedPlayers.clear();
        this.disguisedPlayers.disable();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerReset(PlayerResetEvent event) {
        this.setDisguise(event.getPlayer().getBukkit(), null);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        this.setDisguise(event.getEntity(), null);
    }

    public void setDisguise(Player player, @Nullable Entity entity) {
        if (entity != null) {
            DisguisedPlayer disguisedPlayer = new DisguisedPlayer(player, entity);
            this.disguisedPlayers.put(player, disguisedPlayer);
        } else {
            this.disguisedPlayers.remove(player);
        }
    }
}
