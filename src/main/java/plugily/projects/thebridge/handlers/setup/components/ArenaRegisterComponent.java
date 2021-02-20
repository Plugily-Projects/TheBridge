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

package plugily.projects.thebridge.handlers.setup.components;

import com.github.stefvanschie.inventoryframework.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import pl.plajerlair.commonsbox.minecraft.compat.xseries.XMaterial;
import pl.plajerlair.commonsbox.minecraft.configuration.ConfigUtils;
import pl.plajerlair.commonsbox.minecraft.item.ItemBuilder;
import pl.plajerlair.commonsbox.minecraft.serialization.LocationSerializer;
import plugily.projects.thebridge.Main;
import plugily.projects.thebridge.arena.Arena;
import plugily.projects.thebridge.arena.ArenaRegistry;
import plugily.projects.thebridge.arena.options.ArenaOption;
import plugily.projects.thebridge.handlers.setup.SetupInventory;
import plugily.projects.thebridge.handlers.sign.ArenaSign;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 08.06.2019
 */
public class ArenaRegisterComponent implements SetupComponent {

  private SetupInventory setupInventory;

  @Override
  public void prepare(SetupInventory setupInventory) {
    this.setupInventory = setupInventory;
  }

  @Override
  public void injectComponents(StaticPane pane) {
    FileConfiguration config = setupInventory.getConfig();
    Main plugin = setupInventory.getPlugin();
    ItemStack registeredItem;
    if(!setupInventory.getArena().isReady()) {
      registeredItem = new ItemBuilder(XMaterial.FIREWORK_ROCKET.parseItem())
        .name(plugin.getChatManager().colorRawMessage("&e&lRegister Arena - Finish Setup"))
        .lore(ChatColor.GRAY + "Click this when you're done with configuration.")
        .lore(ChatColor.GRAY + "It will validate and register arena.")
        .build();
    } else {
      registeredItem = new ItemBuilder(Material.BARRIER)
        .name(plugin.getChatManager().colorRawMessage("&a&lArena Registered - Congratulations"))
        .lore(ChatColor.GRAY + "This arena is already registered!")
        .lore(ChatColor.GRAY + "Good job, you went through whole setup!")
        .lore(ChatColor.GRAY + "You can play on this arena now!")
        .build();
    }
    pane.addItem(new GuiItem(registeredItem, e -> {
      Arena arena = setupInventory.getArena();
      if(arena == null) {
        return;
      }
      if(arena.isReady()) {
        e.getWhoClicked().sendMessage(ChatColor.GREEN + "This arena was already validated and is ready to use!");
        return;
      }
      for(String s : new String[]{"lobbylocation", "midlocation", "endlocation", "spectatorlocation"}) {
        if(!config.isSet("instances." + arena.getId() + "." + s) || config.getString("instances." + arena.getId() + "." + s)
          .equals(LocationSerializer.locationToString(Bukkit.getWorlds().get(0).getSpawnLocation()))) {
          e.getWhoClicked().sendMessage(plugin.getChatManager().colorRawMessage("&c&l✘ &cArena validation failed! Please configure following spawns properly: " + s + " (cannot be world spawn location)"));
          return;
        }
      }
      if(config.getConfigurationSection("instances." + arena.getId() + ".bases") == null || config.getConfigurationSection("instances." + arena.getId() + ".bases").getKeys(false).size() < 1) {
        e.getWhoClicked().sendMessage(plugin.getChatManager().colorRawMessage("&c&l✘ &cArena validation failed! Please configure bases properly!"));
        return;
      }
      int basesDone = 0;
      if(config.isConfigurationSection("instances." + arena.getId() + ".bases")) {
        for(String baseID : config.getConfigurationSection("instances." + arena.getId() + ".bases").getKeys(false)) {
          if(config.isSet("instances." + arena.getId() + ".bases." + baseID + ".isdone")) {
            basesDone++;
          }
        }
      }
      if(basesDone < 2) {
        e.getWhoClicked().sendMessage(plugin.getChatManager().colorRawMessage("&c&l✘ &cArena validation failed! Please configure bases properly!"));
        return;
      }
      if(!config.isSet("instances." + arena.getId() + ".minimumplayers")) {
        e.getWhoClicked().sendMessage(plugin.getChatManager().colorRawMessage("&c&l✘ &cArena validation failed! Please configure minimumplayers properly!"));
        return;
      }
      if(!config.isSet("instances." + arena.getId() + ".maximumsize")) {
        e.getWhoClicked().sendMessage(plugin.getChatManager().colorRawMessage("&c&l✘ &cArena validation failed! Please configure maximumsize properly!"));
        return;
      }
      if(!config.isSet("instances." + arena.getId() + ".mode")) {
        e.getWhoClicked().sendMessage(plugin.getChatManager().colorRawMessage("&c&l✘ &cArena validation failed! Please configure mode properly!"));
        return;
      }
      if(!config.isSet("instances." + arena.getId() + ".resetblocks")) {
        e.getWhoClicked().sendMessage(plugin.getChatManager().colorRawMessage("&c&l✘ &cArena validation failed! Please configure resetblocks properly!"));
        return;
      }
      e.getWhoClicked().sendMessage(plugin.getChatManager().colorRawMessage("&a&l✔ &aValidation succeeded! Registering new arena instance: " + arena.getId()));
      config.set("instances." + arena.getId() + ".isdone", true);
      config.set("instances." + arena.getId() + ".resettime", 5);
      ConfigUtils.saveConfig(plugin, config, "arenas");
      List<Sign> signsToUpdate = new ArrayList<>();
      ArenaRegistry.unregisterArena(setupInventory.getArena());
      plugin.getSignManager().getArenaSigns().stream().filter(arenaSign -> arenaSign.getArena().equals(setupInventory.getArena()))
        .forEach(arenaSign -> signsToUpdate.add(arenaSign.getSign()));
      arena.setReady(true);
      arena.setOptionValue(ArenaOption.SIZE, config.getInt("instances." + arena.getId() + ".maximumsize", 3));
      arena.setMaximumPlayers(basesDone * arena.getOption(ArenaOption.SIZE));
      ArenaRegistry.registerArena(arena);
      arena.start();
      plugin.getSignManager().getArenaSigns().clear();
      for(Sign s : signsToUpdate) {
        plugin.getSignManager().getArenaSigns().add(new ArenaSign(s, arena));
        plugin.getSignManager().updateSigns();
      }
    }), 4, 1);
  }

}
