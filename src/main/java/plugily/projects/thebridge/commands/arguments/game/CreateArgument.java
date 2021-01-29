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

package plugily.projects.thebridge.commands.arguments.game;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import pl.plajerlair.commonsbox.minecraft.configuration.ConfigUtils;
import pl.plajerlair.commonsbox.minecraft.serialization.LocationSerializer;
import plugily.projects.thebridge.arena.Arena;
import plugily.projects.thebridge.arena.ArenaRegistry;
import plugily.projects.thebridge.commands.arguments.ArgumentsRegistry;
import plugily.projects.thebridge.commands.arguments.data.CommandArgument;
import plugily.projects.thebridge.commands.arguments.data.LabelData;
import plugily.projects.thebridge.commands.arguments.data.LabeledCommandArgument;
import plugily.projects.thebridge.handlers.ChatManager;
import plugily.projects.thebridge.handlers.setup.SetupInventory;

import java.util.ArrayList;

/**
 * @author Tigerpanzer_02, 2Wild4You
 * <p>
 * Created at 31.10.2020
 */
public class CreateArgument {

  private final ArgumentsRegistry registry;

  public CreateArgument(ArgumentsRegistry registry, ChatManager chatManager) {
    this.registry = registry;
    registry.mapArgument("thebridge", new LabeledCommandArgument("create", "thebridge.admin.create", CommandArgument.ExecutorType.PLAYER,
      new LabelData("/tb create &6<arena>", "/tb create <arena>", "&7Create new arena\n&6Permission: &7thebridge.admin.create")) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        if(args.length == 1) {
          sender.sendMessage(chatManager.colorMessage("Commands.Type-Arena-Name"));
          return;
        }
        Player player = (Player) sender;
        for(Arena arena : ArenaRegistry.getArenas()) {
          if(arena.getId().equalsIgnoreCase(args[1])) {
            player.sendMessage(ChatColor.DARK_RED + "Arena with that ID already exists!");
            player.sendMessage(ChatColor.DARK_RED + "Usage: /tb create <ID>");
            return;
          }
        }
        if(ConfigUtils.getConfig(registry.getPlugin(), "arenas").contains("instances." + args[1])) {
          player.sendMessage(ChatColor.DARK_RED + "Instance/Arena already exists! Use another ID or delete it first!");
        } else {
          createInstanceInConfig(args[1]);
          player.sendMessage(ChatColor.BOLD + "------------------------------------------");
          player.sendMessage(ChatColor.YELLOW + "      Instance " + args[1] + " created!");
          player.sendMessage("");
          player.sendMessage(ChatColor.GREEN + "Edit this arena via " + ChatColor.GOLD + "/tb " + args[1] + " edit" + ChatColor.GREEN + "!");
          player.sendMessage(ChatColor.GOLD + "Don't know where to start? Check out tutorial video:");
          player.sendMessage(ChatColor.GOLD + SetupInventory.VIDEO_LINK);
          player.sendMessage(ChatColor.BOLD + "------------------------------------------- ");
        }
      }
    });
  }

  private void createInstanceInConfig(String id) {
    String path = "instances." + id + ".";
    FileConfiguration config = ConfigUtils.getConfig(registry.getPlugin(), "arenas");
    LocationSerializer.saveLoc(registry.getPlugin(), config, "arenas", path + "lobbylocation", Bukkit.getServer().getWorlds().get(0).getSpawnLocation());
    LocationSerializer.saveLoc(registry.getPlugin(), config, "arenas", path + "endlocation", Bukkit.getServer().getWorlds().get(0).getSpawnLocation());
    LocationSerializer.saveLoc(registry.getPlugin(), config, "arenas", path + "spectatorlocation", Bukkit.getServer().getWorlds().get(0).getSpawnLocation());
    config.set(path + "minimumplayers", 2);
    config.set(path + "mapname", id);
    config.set(path + "signs", new ArrayList<>());
    config.set(path + "isdone", false);
    ConfigUtils.saveConfig(registry.getPlugin(), config, "arenas");

    Arena arena = new Arena(id);

    arena.setMinimumPlayers(config.getInt(path + "minimumplayers"));
    arena.setMaximumPlayers(config.getInt(path + "maximumplayers"));
    arena.setMapName(config.getString(path + "mapname"));
    arena.setLobbyLocation(LocationSerializer.getLocation(config.getString(path + "lobbylocation")));
    arena.setEndLocation(LocationSerializer.getLocation(config.getString(path + "endlocation")));
    arena.setReady(false);

    ArenaRegistry.registerArena(arena);
  }

}
