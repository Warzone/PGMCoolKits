package tc.oc.pgm.disguise;

import java.util.Iterator;
import java.util.Map;

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
    private final DisguiseKit kit;

    private DisguisedPlayer(Player player, DisguiseKit kit) {
      this.player = player;
      this.kit = kit;
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
    for (Iterator<Player> iterator = this.disguisedPlayers.keySet().iterator(); iterator.hasNext(); ) {
      iterator.remove();
    }
    this.disguisedPlayers.disable();
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerReset(PlayerResetEvent event) {
    this.setKit(event.getPlayer().getBukkit(), null);
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerDeath(PlayerDeathEvent event) {
    this.setKit(event.getEntity(), null);
  }

  public void setKit(Player player, @Nullable DisguiseKit kit) {
    if (kit != null && kit.enabled) {
      DisguisedPlayer disguisedPlayer = new DisguisedPlayer(player, kit);
      this.disguisedPlayers.put(player, disguisedPlayer);
    } else {
      this.disguisedPlayers.remove(player);
    }
  }
    
}
