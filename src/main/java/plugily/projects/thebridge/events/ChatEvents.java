package plugily.projects.thebridge.events;

import me.clip.placeholderapi.PlaceholderAPI;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import plugily.projects.thebridge.ConfigPreferences;
import plugily.projects.thebridge.Main;
import plugily.projects.thebridge.arena.Arena;
import plugily.projects.thebridge.arena.ArenaRegistry;
import plugily.projects.thebridge.handlers.language.LanguageManager;
import plugily.projects.thebridge.user.User;

import java.util.regex.Pattern;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 23.11.2020
 */
public class ChatEvents implements Listener {

  private final Main plugin;
  private final String[] regexChars = {"$", "\\"};

  public ChatEvents(Main plugin) {
    this.plugin = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler(ignoreCancelled = true)
  public void onChatIngame(AsyncPlayerChatEvent event) {
    Arena arena = ArenaRegistry.getArena(event.getPlayer());
    if (arena == null) {
      if (!plugin.getConfigPreferences().getOption(ConfigPreferences.Option.DISABLE_SEPARATE_CHAT)) {
        for (Arena loopArena : ArenaRegistry.getArenas()) {
          for (Player player : loopArena.getPlayers()) {
            event.getRecipients().remove(player);
          }
        }
      }
      return;
    }
    if (plugin.getConfigPreferences().getOption(ConfigPreferences.Option.CHAT_FORMAT_ENABLED)) {
      String eventMessage = event.getMessage();
      for (String regexChar : regexChars) {
        if (eventMessage.contains(regexChar)) {
          eventMessage = eventMessage.replaceAll(Pattern.quote(regexChar), "");
        }
      }
      String message = formatChatPlaceholders(LanguageManager.getLanguageMessage("In-Game.Game-Chat-Format"), plugin.getUserManager().getUser(event.getPlayer()), eventMessage);
      if (!plugin.getConfigPreferences().getOption(ConfigPreferences.Option.DISABLE_SEPARATE_CHAT)) {
        event.setCancelled(true);
        boolean dead = !arena.getPlayersLeft().contains(event.getPlayer());
        for (Player player : arena.getPlayers()) {
          if (dead && arena.getPlayersLeft().contains(player)) {
            continue;
          }
          if (dead) {
            String prefix = formatChatPlaceholders(LanguageManager.getLanguageMessage("In-Game.Game-Death-Format"), plugin.getUserManager().getUser(event.getPlayer()), null);
            player.sendMessage(prefix + message);
          } else {
            player.sendMessage(message);
          }
        }
        Bukkit.getConsoleSender().sendMessage(message);
      } else {
        event.setMessage(message);
      }
    }
  }

  private String formatChatPlaceholders(String message, User user, String saidMessage) {
    String formatted = message;
    formatted = plugin.getChatManager().colorRawMessage(formatted);
    formatted = StringUtils.replace(formatted, "%player%", user.getPlayer().getName());
    formatted = StringUtils.replace(formatted, "%message%", ChatColor.stripColor(saidMessage));
    if (plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
      formatted = PlaceholderAPI.setPlaceholders(user.getPlayer(), formatted);
    }
    return formatted;
  }

}
