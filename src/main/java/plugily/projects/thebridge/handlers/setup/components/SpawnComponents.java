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
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import pl.plajerlair.commonsbox.minecraft.compat.XMaterial;
import pl.plajerlair.commonsbox.minecraft.configuration.ConfigUtils;
import pl.plajerlair.commonsbox.minecraft.dimensional.Cuboid;
import pl.plajerlair.commonsbox.minecraft.item.ItemBuilder;
import pl.plajerlair.commonsbox.minecraft.serialization.LocationSerializer;
import plugily.projects.thebridge.Main;
import plugily.projects.thebridge.arena.Arena;
import plugily.projects.thebridge.handlers.setup.SetupInventory;
import plugily.projects.thebridge.utils.CuboidSelector;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 08.06.2019
 */
public class SpawnComponents implements SetupComponent {

  private SetupInventory setupInventory;

  @Override
  public void prepare(SetupInventory setupInventory) {
    this.setupInventory = setupInventory;
  }

  @Override
  public void injectComponents(StaticPane pane) {
    Arena arena = setupInventory.getArena();
    if(arena == null) {
      return;
    }
    Player player = setupInventory.getPlayer();
    FileConfiguration config = setupInventory.getConfig();
    Main plugin = setupInventory.getPlugin();
    String serializedLocation = player.getLocation().getWorld().getName() + "," + player.getLocation().getX() + "," + player.getLocation().getY() + ","
      + player.getLocation().getZ() + "," + player.getLocation().getYaw() + ",0.0";
    pane.addItem(new GuiItem(new ItemBuilder(Material.REDSTONE_BLOCK)
      .name(plugin.getChatManager().colorRawMessage("&e&lSet Ending Location"))
      .lore(ChatColor.GRAY + "Click to set the ending location")
      .lore(ChatColor.GRAY + "on the place where you are standing.")
      .lore(ChatColor.DARK_GRAY + "(location where players will be")
      .lore(ChatColor.DARK_GRAY + "teleported after the game)")
      .lore("", setupInventory.getSetupUtilities().isOptionDoneBool("instances." + arena.getId() + ".endlocation"))
      .build(), e -> {
      e.getWhoClicked().closeInventory();
      config.set("instances." + arena.getId() + ".endlocation", serializedLocation);
      arena.setEndLocation(player.getLocation());
      player.sendMessage(plugin.getChatManager().colorRawMessage("&e✔ Completed | &aEnding location for arena " + arena.getId() + " set at your location!"));
      ConfigUtils.saveConfig(plugin, config, "arenas");
    }), 0, 0);

    pane.addItem(new GuiItem(new ItemBuilder(Material.LAPIS_BLOCK)
      .name(plugin.getChatManager().colorRawMessage("&e&lSet Lobby Location"))
      .lore(ChatColor.GRAY + "Click to set the lobby location")
      .lore(ChatColor.GRAY + "on the place where you are standing")
      .lore("", setupInventory.getSetupUtilities().isOptionDoneBool("instances." + arena.getId() + ".lobbylocation"))
      .build(), e -> {
      e.getWhoClicked().closeInventory();
      config.set("instances." + arena.getId() + ".lobbylocation", serializedLocation);
      arena.setLobbyLocation(player.getLocation());
      player.sendMessage(plugin.getChatManager().colorRawMessage("&e✔ Completed | &aLobby location for arena " + arena.getId() + " set at your location!"));
      ConfigUtils.saveConfig(plugin, config, "arenas");
    }), 1, 0);

    pane.addItem(new GuiItem(new ItemBuilder(XMaterial.COMPASS.parseMaterial())
      .name(plugin.getChatManager().colorRawMessage("&e&lSet Spectator Location"))
      .lore(ChatColor.GRAY + "Click to set the spectator location")
      .lore(ChatColor.GRAY + "on the place where you are standing.")
      .lore(ChatColor.DARK_GRAY + "(location where players will be")
      .lore(ChatColor.DARK_GRAY + "teleported if they are dead or join while InGame")
      .lore("", setupInventory.getSetupUtilities().isOptionDoneBool("instances." + arena.getId() + ".spectatorlocation"))
      .build(), e -> {
      e.getWhoClicked().closeInventory();
      config.set("instances." + arena.getId() + ".spectatorlocation", serializedLocation);
      arena.setSpectatorLocation(player.getLocation());
      player.sendMessage(plugin.getChatManager().colorRawMessage("&e✔ Completed | &aSpectator location for arena " + arena.getId() + " set at your location!"));
      ConfigUtils.saveConfig(plugin, config, "arenas");
    }), 2, 0);

    pane.addItem(new GuiItem(new ItemBuilder(XMaterial.BEACON.parseMaterial())
      .name(plugin.getChatManager().colorRawMessage("&e&lSet Mid Location"))
      .lore(ChatColor.GRAY + "Click to set the mid location")
      .lore(ChatColor.GRAY + "on the place where you are standing.")
      .lore(ChatColor.DARK_GRAY + "(location where all lines will be")
      .lore(ChatColor.DARK_GRAY + "crossed from each base)")
      .lore("", setupInventory.getSetupUtilities().isOptionDoneBool("instances." + arena.getId() + ".midlocation"))
      .build(), e -> {
      e.getWhoClicked().closeInventory();
      config.set("instances." + arena.getId() + ".midlocation", serializedLocation);
      arena.setMidLocation(player.getLocation());
      player.sendMessage(plugin.getChatManager().colorRawMessage("&e✔ Completed | &aMid location for arena " + arena.getId() + " set at your location!"));
      ConfigUtils.saveConfig(plugin, config, "arenas");
    }), 3, 0);

    pane.addItem(new GuiItem(new ItemBuilder(XMaterial.BEDROCK.parseMaterial())
      .name(plugin.getChatManager().colorRawMessage("&e&lSet Arena Location"))
      .lore(ChatColor.GRAY + "Click to set the arena location")
      .lore(ChatColor.GRAY + "after you selected it with the location wand")
      .lore(ChatColor.DARK_GRAY + "[location where all bases and lines")
      .lore(ChatColor.DARK_GRAY + "are in (+ players can build inside)]")
      .lore("", setupInventory.getSetupUtilities().isOptionDoneBool("instances." + arena.getId() + ".arenalocation1"))
      .build(), e -> {
      e.getWhoClicked().closeInventory();
      CuboidSelector.Selection selection = plugin.getCuboidSelector().getSelection(player);
      if(selection == null || selection.getFirstPos() == null || selection.getSecondPos() == null) {
        player.sendMessage(plugin.getChatManager().colorRawMessage(plugin.getChatManager().getPrefix() + "&cPlease select both corners before adding an arena location!"));
        return;
      }
      LocationSerializer.saveLoc(plugin, config, "arenas", "instances." + arena.getId() + ".arenalocation1", selection.getFirstPos());
      LocationSerializer.saveLoc(plugin, config, "arenas", "instances." + arena.getId() + ".arenalocation2", selection.getSecondPos());
      arena.setArenaBorder(new Cuboid(selection.getFirstPos(), selection.getSecondPos()));
      player.sendMessage(plugin.getChatManager().colorRawMessage("&e✔ Completed | &aArena location for arena " + arena.getId() + " set at your location!"));
      ConfigUtils.saveConfig(plugin, config, "arenas");
    }), 4, 0);


  }

}
