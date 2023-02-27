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

package plugily.projects.thebridge.handlers;

import me.clip.placeholderapi.PlaceholderAPI;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import plugily.projects.commonsbox.minecraft.compat.ServerVersion;
import plugily.projects.commonsbox.string.StringFormatUtils;
import plugily.projects.thebridge.Main;
import plugily.projects.thebridge.arena.Arena;
import plugily.projects.thebridge.handlers.language.LanguageManager;
import plugily.projects.thebridge.utils.Utils;

/**
 * @author Tigerpanzer_02, 2Wild4You
 * <p>
 * Created at 31.10.2020
 */
public class ChatManager {

  private final String pluginPrefix;
  private final Main plugin;

  public ChatManager(Main plugin) {
    this.plugin = plugin;
    pluginPrefix = colorMessage("In-Game.Plugin-Prefix");
  }

  /**
   * @return game prefix
   */
  public String getPrefix() {
    return pluginPrefix;
  }

  public String colorMessage(String message) {
    return colorRawMessage(LanguageManager.getLanguageMessage(message));
  }

  public String colorRawMessage(String message) {
    if(message == null) {
      return "";
    }

    if(message.contains("#") && ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_16_R1)) {
      message = Utils.matchColorRegex(message);
    }

    return ChatColor.translateAlternateColorCodes('&', message);
  }

  public String colorMessage(String message, Player player) {
    String returnString = LanguageManager.getLanguageMessage(message);
    if(plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
      returnString = PlaceholderAPI.setPlaceholders(player, returnString);
    }
    return colorRawMessage(returnString);
  }

  public void broadcast(Arena arena, String message) {
    if(message != null && !message.isEmpty()) {
      for(Player player : arena.getPlayers()) {
        player.sendMessage(pluginPrefix + message);
      }
    }
  }

  public String formatMessage(Arena arena, String message, int integer) {
    String returnString = message;
    returnString = StringUtils.replace(returnString, "%NUMBER%", Integer.toString(integer));
    returnString = colorRawMessage(formatPlaceholders(returnString, arena));
    return returnString;
  }

  public String formatMessage(Arena arena, String message, Player player) {
    String returnString = message;
    returnString = StringUtils.replace(returnString, "%PLAYER%", player.getName());
    returnString = colorRawMessage(formatPlaceholders(returnString, arena));
    if(plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
      returnString = PlaceholderAPI.setPlaceholders(player, returnString);
    }
    return returnString;
  }

  private String formatPlaceholders(String message, Arena arena) {
    String returnString = message;
    returnString = StringUtils.replace(returnString, "%ARENANAME%", arena.getMapName());
    returnString = StringUtils.replace(returnString, "%TIME%", Integer.toString(arena.getTimer()));
    returnString = StringUtils.replace(returnString, "%FORMATTEDTIME%", StringFormatUtils.formatIntoMMSS((arena.getTimer())));
    returnString = StringUtils.replace(returnString, "%PLAYERSIZE%", Integer.toString(arena.getPlayers().size()));
    returnString = StringUtils.replace(returnString, "%MAXPLAYERS%", Integer.toString(arena.getMaximumPlayers()));
    returnString = StringUtils.replace(returnString, "%MINPLAYERS%", Integer.toString(arena.getMinimumPlayers()));
    return returnString;
  }

  public void broadcastAction(Arena arena, Player player, ActionType action) {
    String path;
    switch(action) {
      case JOIN:
        path = "In-Game.Messages.Join";
        break;
      case LEAVE:
        path = "In-Game.Messages.Leave";
        break;
      case DEATH:
        path = "In-Game.Messages.Death-Own";
        break;
      default:
        return; //likely won't ever happen
    }
    broadcast(arena, formatMessage(arena, colorMessage(path), player));
  }

  public enum ActionType {
    JOIN, LEAVE, DEATH
  }

}
