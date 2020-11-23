package plugily.projects.thebridge.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import plugily.projects.thebridge.Main;
import plugily.projects.thebridge.arena.ArenaManager;
import plugily.projects.thebridge.arena.ArenaRegistry;


/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 23.11.2020
 */
public class QuitEvent implements Listener {

  private final Main plugin;

  public QuitEvent(Main plugin) {
    this.plugin = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler
  public void onQuit(PlayerQuitEvent event) {
    onQuit(event.getPlayer());
  }

  @EventHandler
  public void onKick(PlayerKickEvent event) {
    onQuit(event.getPlayer());
  }

  private void onQuit(Player player) {
  if (ArenaRegistry.isInArena(player)) {
      ArenaManager.leaveAttempt(player, ArenaRegistry.getArena(player));
    }
    plugin.getUserManager().removeUser(plugin.getUserManager().getUser(player));
  }

}
