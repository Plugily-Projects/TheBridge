/*
 * TheBridge - Defend your base and try to wipe out the others
 * Copyright (C)  2021  Plugily Projects - maintained by Tigerpanzer_02, 2Wild4You and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

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
import plugily.projects.thebridge.api.StatsStorage;
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
    formatted = StringUtils.replace(formatted, "%level%", String.valueOf(user.getStat(StatsStorage.StatisticType.LEVEL)));
    formatted = StringUtils.replace(formatted, "%player%", user.getPlayer().getName());
    formatted = StringUtils.replace(formatted, "%message%", ChatColor.stripColor(saidMessage));
    if (user.getArena().getBase(user.getPlayer()) == null) {
      formatted = StringUtils.replace(formatted, "%base%", LanguageManager.getLanguageMessage("Scoreboard.Bases.Not-Inside"));
      formatted = StringUtils.replace(formatted, "%base_formatted%", LanguageManager.getLanguageMessage("Scoreboard.Bases.Not-Inside"));
    } else {
      formatted = StringUtils.replace(formatted, "%base%", user.getArena().getBase(user.getPlayer()).getColor());
      formatted = StringUtils.replace(formatted, "%base_formatted%", user.getArena().getBase(user.getPlayer()).getFormattedColor());
    }
    if (user.isSpectator()) {
      formatted = StringUtils.replace(formatted, "%kit%", plugin.getChatManager().colorMessage("Messages.DEAD_TAG_ON_DEATH"));
    } else {
      formatted = StringUtils.replace(formatted, "%kit%", user.getKit().getName());
    }
    formatted = plugin.getChatManager().colorRawMessage(formatted);
    if (plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
      formatted = PlaceholderAPI.setPlaceholders(user.getPlayer(), formatted);
    }
    return formatted;
  }

}
