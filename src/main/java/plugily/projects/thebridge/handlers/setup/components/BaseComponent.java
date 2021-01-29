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
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import pl.plajerlair.commonsbox.minecraft.compat.XMaterial;
import pl.plajerlair.commonsbox.minecraft.configuration.ConfigUtils;
import pl.plajerlair.commonsbox.minecraft.dimensional.Cuboid;
import pl.plajerlair.commonsbox.minecraft.item.ItemBuilder;
import pl.plajerlair.commonsbox.minecraft.serialization.LocationSerializer;
import plugily.projects.thebridge.Main;
import plugily.projects.thebridge.arena.Arena;
import plugily.projects.thebridge.arena.base.Base;
import plugily.projects.thebridge.handlers.hologram.ArmorStandHologram;
import plugily.projects.thebridge.handlers.setup.BaseUtilities;
import plugily.projects.thebridge.handlers.setup.SetupInventory;
import plugily.projects.thebridge.utils.CuboidSelector;
import plugily.projects.thebridge.utils.Utils;
import plugily.projects.thebridge.utils.conversation.SimpleConversationBuilder;

import java.util.HashMap;

public class BaseComponent implements SetupComponent {

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

    pane.addItem(new GuiItem(new ItemBuilder(Material.NAME_TAG)
      .name(plugin.getChatManager().colorRawMessage("&e&lSet Color"))
      .lore(ChatColor.GRAY + "Click to set base color name")
      .lore("", setupInventory.getSetupUtilities().isOptionDone("instances." + arena.getId() + ".bases." + getId(player) + ".color"))
      .build(), e -> {
      e.getWhoClicked().closeInventory();
      new SimpleConversationBuilder().withPrompt(new StringPrompt() {
        @Override
        public String getPromptText(ConversationContext context) {
          return plugin.getChatManager().colorRawMessage(plugin.getChatManager().getPrefix() + "&ePlease type in chat color name (USE UPPERCASE)!");
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
          if(!ChatColor.valueOf(input).isColor()) {
            player.sendRawMessage(plugin.getChatManager().colorRawMessage("&cTry again. This is not an color!"));
          }
          String color = ChatColor.valueOf(input).name();
          player.sendRawMessage(plugin.getChatManager().colorRawMessage("&e✔ Completed | &aColor of base " + getId(player) + " set to " + color));
          config.set("instances." + arena.getId() + ".bases." + getId(player) + ".color", color);
          ConfigUtils.saveConfig(plugin, config, "arenas");
          BaseUtilities.addEditing(player);
          new SetupInventory(arena, player).openBases();
          return Prompt.END_OF_CONVERSATION;
        }
      }).buildFor(player);
    }), 0, 0);

    pane.addItem(new GuiItem(new ItemBuilder(XMaterial.BEDROCK.parseMaterial())
      .name(plugin.getChatManager().colorRawMessage("&e&lSet Base Location"))
      .lore(ChatColor.GRAY + "Click to set the base location")
      .lore(ChatColor.GRAY + "after you selected it with the location wand")
      .lore(ChatColor.DARK_GRAY + "(corners of one base)")
      .lore("", setupInventory.getSetupUtilities().isOptionDoneBool("instances." + arena.getId() + ".bases." + getId(player) + ".baselocation1"))
      .build(), e -> {
      e.getWhoClicked().closeInventory();
      CuboidSelector.Selection selection = plugin.getCuboidSelector().getSelection(player);
      if(selection == null || selection.getFirstPos() == null || selection.getSecondPos() == null) {
        player.sendMessage(plugin.getChatManager().colorRawMessage(plugin.getChatManager().getPrefix() + "&cPlease select both corners before adding an base location!"));
        return;
      }
      LocationSerializer.saveLoc(plugin, config, "arenas", "instances." + arena.getId() + ".bases." + getId(player) + ".baselocation1", selection.getFirstPos());
      LocationSerializer.saveLoc(plugin, config, "arenas", "instances." + arena.getId() + ".bases." + getId(player) + ".baselocation2", selection.getSecondPos());
      arena.setEndLocation(player.getLocation());
      player.sendMessage(plugin.getChatManager().colorRawMessage("&e✔ Completed | &aBase location for arena " + arena.getId() + " set with your selection!"));
      BaseUtilities.addEditing(player);
      ConfigUtils.saveConfig(plugin, config, "arenas");
    }), 1, 0);

    pane.addItem(new GuiItem(new ItemBuilder(XMaterial.ENDER_EYE.parseMaterial())
      .name(plugin.getChatManager().colorRawMessage("&e&lSet Portal Location"))
      .lore(ChatColor.GRAY + "Click to set the portal location")
      .lore(ChatColor.GRAY + "after you selected it with the location wand")
      .lore(ChatColor.DARK_GRAY + "(corners of the portal on the base)")
      .lore("", setupInventory.getSetupUtilities().isOptionDoneBool("instances." + arena.getId() + ".bases." + getId(player) + ".portallocation1"))
      .build(), e -> {
      e.getWhoClicked().closeInventory();
      CuboidSelector.Selection selection = plugin.getCuboidSelector().getSelection(player);
      if(selection == null || selection.getFirstPos() == null || selection.getSecondPos() == null) {
        player.sendMessage(plugin.getChatManager().colorRawMessage(plugin.getChatManager().getPrefix() + "&cPlease select both corners before adding an base location!"));
        return;
      }
      LocationSerializer.saveLoc(plugin, config, "arenas", "instances." + arena.getId() + ".bases." + getId(player) + ".portallocation1", selection.getFirstPos());
      LocationSerializer.saveLoc(plugin, config, "arenas", "instances." + arena.getId() + ".bases." + getId(player) + ".portallocation2", selection.getSecondPos());
      LocationSerializer.saveLoc(plugin, config, "arenas", "instances." + arena.getId() + ".bases." + getId(player) + ".portalhologram", new Cuboid(selection.getFirstPos(), selection.getSecondPos()).getCenter().add(0, 2, 0));

      player.sendMessage(plugin.getChatManager().colorRawMessage("&e✔ Completed | &aPortal location for arena " + arena.getId() + " set with your selection!"));
      player.sendMessage(plugin.getChatManager().colorRawMessage("&e✔ Completed &cautomatically &e| &aPortalHologram location for base " + getId(player) + " set at the mid of your selection! Feel free to change it if you want!"));
      BaseUtilities.addEditing(player);
      ConfigUtils.saveConfig(plugin, config, "arenas");
    }), 2, 0);

    pane.addItem(new GuiItem(new ItemBuilder(XMaterial.ENDER_EYE.parseMaterial())
      .name(plugin.getChatManager().colorRawMessage("&e&lSet Cage Location (Only floor)"))
      .lore(ChatColor.GRAY + "Click to set the cage location (only floor needed)")
      .lore(ChatColor.GRAY + "after you selected it with the location wand")
      .lore(ChatColor.DARK_GRAY + "(Please just select the blocks that should be removed/set)")
      .lore("", setupInventory.getSetupUtilities().isOptionDoneBool("instances." + arena.getId() + ".bases." + getId(player) + ".cagelocation1"))
      .build(), e -> {
      e.getWhoClicked().closeInventory();
      CuboidSelector.Selection selection = plugin.getCuboidSelector().getSelection(player);
      if(selection == null || selection.getFirstPos() == null || selection.getSecondPos() == null) {
        player.sendMessage(plugin.getChatManager().colorRawMessage(plugin.getChatManager().getPrefix() + "&cPlease select both corners before adding an base location!"));
        return;
      }
      if(new Cuboid(selection.getFirstPos(), selection.getSecondPos()).contains(XMaterial.AIR.parseMaterial())) {
        player.sendMessage(plugin.getChatManager().colorRawMessage(plugin.getChatManager().getPrefix() + "&cPlease select only the floor of the cage! Make sure that it is not air!"));
        return;
      }
      LocationSerializer.saveLoc(plugin, config, "arenas", "instances." + arena.getId() + ".bases." + getId(player) + ".cagelocation1", selection.getFirstPos());
      LocationSerializer.saveLoc(plugin, config, "arenas", "instances." + arena.getId() + ".bases." + getId(player) + ".cagelocation2", selection.getSecondPos());

      player.sendMessage(plugin.getChatManager().colorRawMessage("&e✔ Completed | &aCage location for arena " + arena.getId() + " set with your selection!"));
      BaseUtilities.addEditing(player);
      ConfigUtils.saveConfig(plugin, config, "arenas");
    }), 3, 0);

    String serializedLocation = player.getLocation().getWorld().getName() + "," + player.getLocation().getX() + "," + player.getLocation().getY() + ","
      + player.getLocation().getZ() + "," + player.getLocation().getYaw() + ",0.0";
    pane.addItem(new GuiItem(new ItemBuilder(XMaterial.EMERALD_BLOCK.parseMaterial())
      .name(plugin.getChatManager().colorRawMessage("&e&lSet SpawnPoint Location"))
      .lore(ChatColor.GRAY + "Click to set the spawn point location")
      .lore(ChatColor.GRAY + "on the place where you are standing.")
      .lore(ChatColor.DARK_GRAY + "(location where players spawns first time")
      .lore(ChatColor.DARK_GRAY + "and on every round reset)")
      .lore("", setupInventory.getSetupUtilities().isOptionDoneBool("instances." + arena.getId() + ".bases." + getId(player) + ".spawnpoint"))
      .build(), e -> {
      e.getWhoClicked().closeInventory();
      config.set("instances." + arena.getId() + ".bases." + getId(player) + ".spawnpoint", serializedLocation);
      player.sendMessage(plugin.getChatManager().colorRawMessage("&e✔ Completed | &aSpawnPoint location for base " + getId(player) + " set at your location!"));
      BaseUtilities.addEditing(player);
      ConfigUtils.saveConfig(plugin, config, "arenas");
    }), 4, 0);

    pane.addItem(new GuiItem(new ItemBuilder(XMaterial.LAPIS_BLOCK.parseMaterial())
      .name(plugin.getChatManager().colorRawMessage("&e&lSet ReSpawnPoint Location"))
      .lore(ChatColor.GRAY + "Click to set the respawn point location")
      .lore(ChatColor.GRAY + "on the place where you are standing.")
      .lore(ChatColor.DARK_GRAY + "(location where players respawns every")
      .lore(ChatColor.DARK_GRAY + "time after death)")
      .lore("", setupInventory.getSetupUtilities().isOptionDoneBool("instances." + arena.getId() + ".bases." + getId(player) + ".respawnpoint"))
      .build(), e -> {
      e.getWhoClicked().closeInventory();
      config.set("instances." + arena.getId() + ".bases." + getId(player) + ".respawnpoint", serializedLocation);
      player.sendMessage(plugin.getChatManager().colorRawMessage("&e✔ Completed | &aReSpawnPoint location for base " + getId(player) + " set at your location!"));
      BaseUtilities.addEditing(player);
      ConfigUtils.saveConfig(plugin, config, "arenas");
    }), 5, 0);

    pane.addItem(new GuiItem(new ItemBuilder(XMaterial.ARMOR_STAND.parseMaterial())
      .name(plugin.getChatManager().colorRawMessage("&e&lSet Portal Hologram Location"))
      .lore(ChatColor.GRAY + "Click to set the portal hologram location")
      .lore(ChatColor.GRAY + "on the place where you are standing.")
      .lore("", setupInventory.getSetupUtilities().isOptionDoneBool("instances." + arena.getId() + ".bases." + getId(player) + ".portalhologram"))
      .build(), e -> {
      e.getWhoClicked().closeInventory();
      config.set("instances." + arena.getId() + ".bases." + getId(player) + ".portalhologram", serializedLocation);
      if(config.getBoolean("instances." + arena.getId() + ".bases." + getId(player) + ".isdone", false)) {
        player.sendMessage(plugin.getChatManager().colorRawMessage("&cLocation changes take affect after restart!"));
      }
      player.sendMessage(plugin.getChatManager().colorRawMessage("&e✔ Completed | &aPortalHologram location for base " + getId(player) + " set at your location!"));
      BaseUtilities.addEditing(player);
      ConfigUtils.saveConfig(plugin, config, "arenas");
    }), 6, 0);

    pane.addItem(new GuiItem(new ItemBuilder(XMaterial.FIREWORK_ROCKET.parseMaterial())
      .name(plugin.getChatManager().colorRawMessage("&e&lFinish Base"))
      .lore(ChatColor.GREEN + "Click to finish & save the setup of this base")
      .build(), e -> {
      e.getWhoClicked().closeInventory();
      if(config.get("instances." + arena.getId() + ".bases." + getId(player) + ".baselocation1") == null) {
        e.getWhoClicked().sendMessage(plugin.getChatManager().colorRawMessage("&c&l✘ &cBase validation failed! Please configure base location properly!"));
        return;
      }
      if(config.get("instances." + arena.getId() + ".bases." + getId(player) + ".portallocation1") == null) {
        e.getWhoClicked().sendMessage(plugin.getChatManager().colorRawMessage("&c&l✘ &cBase validation failed! Please configure portal location properly!"));
        return;
      }
      if(config.get("instances." + arena.getId() + ".bases." + getId(player) + ".spawnpoint") == null) {
        e.getWhoClicked().sendMessage(plugin.getChatManager().colorRawMessage("&c&l✘ &cBase validation failed! Please configure spawnpoint properly!"));
        return;
      }
      if(config.get("instances." + arena.getId() + ".bases." + getId(player) + ".respawnpoint") == null) {
        e.getWhoClicked().sendMessage(plugin.getChatManager().colorRawMessage("&c&l✘ &cBase validation failed! Please configure respawnpoint properly!"));
        return;
      }
      if(config.get("instances." + arena.getId() + ".bases." + getId(player) + ".color") == null) {
        e.getWhoClicked().sendMessage(plugin.getChatManager().colorRawMessage("&c&l✘ &cBase validation failed! Please configure color properly!"));
        return;
      }
      if(config.get("instances." + arena.getId() + ".bases." + getId(player) + ".portalhologram") == null) {
        e.getWhoClicked().sendMessage(plugin.getChatManager().colorRawMessage("&c&l✘ &cBase validation failed! Please configure portalhologram properly!"));
        return;
      }
      player.sendMessage(plugin.getChatManager().colorRawMessage("&a&l✔ &aValidation succeeded! Registering new base: " + getId(player)));
      config.set("instances." + arena.getId() + ".bases." + getId(player) + ".isdone", true);
      Base base = new Base(
        config.getString("instances." + arena.getId() + ".bases." + getId(player) + ".color"),
        LocationSerializer.getLocation(config.getString("instances." + arena.getId() + ".bases." + getId(player) + ".baselocation1")),
        LocationSerializer.getLocation(config.getString("instances." + arena.getId() + ".bases." + getId(player) + ".baselocation2")),
        LocationSerializer.getLocation(config.getString("instances." + arena.getId() + ".bases." + getId(player) + ".spawnpoint")),
        LocationSerializer.getLocation(config.getString("instances." + arena.getId() + ".bases." + getId(player) + ".respawnpoint")),
        LocationSerializer.getLocation(config.getString("instances." + arena.getId() + ".bases." + getId(player) + ".portallocation1")),
        LocationSerializer.getLocation(config.getString("instances." + arena.getId() + ".bases." + getId(player) + ".portallocation2")),
        config.getInt("instances." + arena.getId() + ".maximumsize")
      );
      if(config.getString("instances." + arena.getId() + ".bases." + getId(player) + ".cagelocation1") != null)
        base.setCageCuboid(new Cuboid(LocationSerializer.getLocation(config.getString("instances." + arena.getId() + ".bases." + getId(player) + ".cagelocation1")), LocationSerializer.getLocation(config.getString("instances." + arena.getId() + ".bases." + getId(player) + ".cagelocation2"))));
      arena.addBase(base);
      ArmorStandHologram portal = new ArmorStandHologram(Utils.getBlockCenter(LocationSerializer.getLocation(config.getString("instances." + arena.getId() + ".bases." + getId(player) + ".portalhologram"))));
      for(String str : plugin.getChatManager().colorMessage("In-Game.Messages.Portal.Hologram").split(";")) {
        portal.appendLine(str.replace("%base%", base.getFormattedColor()));
      }
      base.setArmorStandHologram(portal);
      ConfigUtils.saveConfig(plugin, config, "arenas");
      BaseUtilities.getBaseId().remove(player);
      BaseUtilities.removeEditing(player);
    }), 7, 0);
  }

  public int getId(Player player) {
    if(!BaseUtilities.check(setupInventory.getArena(), player)) {
      int id = 0;
      if(setupInventory.getConfig().getConfigurationSection("instances." + setupInventory.getArena().getId() + ".bases") != null) {
        id = setupInventory.getConfig().getConfigurationSection("instances." + setupInventory.getArena().getId() + ".bases").getKeys(false).size();
      }
      HashMap<String, Integer> secondMap = new HashMap<>();
      secondMap.put(setupInventory.getArena().getId(), id);
      BaseUtilities.getBaseId().put(player, secondMap);
    }
    return BaseUtilities.getBaseId().get(player).get(setupInventory.getArena().getId());
  }
}
