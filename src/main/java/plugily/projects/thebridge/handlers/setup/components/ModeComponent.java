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
import org.bukkit.inventory.ItemStack;
import pl.plajerlair.commonsbox.minecraft.compat.xseries.XMaterial;
import pl.plajerlair.commonsbox.minecraft.configuration.ConfigUtils;
import pl.plajerlair.commonsbox.minecraft.item.ItemBuilder;
import plugily.projects.thebridge.Main;
import plugily.projects.thebridge.arena.Arena;
import plugily.projects.thebridge.arena.options.ArenaOption;
import plugily.projects.thebridge.handlers.setup.SetupInventory;

public class ModeComponent implements SetupComponent {

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

    pane.addItem(new GuiItem(new ItemBuilder(Material.REDSTONE)
      .amount(setupInventory.getSetupUtilities().getMinimumValueHigherThanZero("modevalue"))
      .name(plugin.getChatManager().colorRawMessage("&e&lSet Mode Value Amount"))
      .lore(ChatColor.GRAY + "LEFT click to decrease")
      .lore(ChatColor.GRAY + "RIGHT click to increase")
      .lore(ChatColor.DARK_GRAY + "(how many points until the mode choose winner)")
      .lore("", setupInventory.getSetupUtilities().isOptionDone("instances." + arena.getId() + ".modevalue"))
      .build(), e -> {
      ItemStack itemStack = e.getInventory().getItem(e.getSlot());
      if(itemStack == null || e.getCurrentItem() == null) {
        return;
      }
      if(e.getClick().isRightClick()) {
        e.getCurrentItem().setAmount(e.getCurrentItem().getAmount() + 1);
      }
      if(e.getClick().isLeftClick()) {
        e.getCurrentItem().setAmount(e.getCurrentItem().getAmount() - 1);
      }
      if(itemStack.getAmount() < 1) {
        e.getWhoClicked().sendMessage(plugin.getChatManager().colorRawMessage("&c&l✖ &cWarning | Please do not set amount lower than 1!"));
        itemStack.setAmount(1);
      }
      config.set("instances." + arena.getId() + ".modevalue", e.getCurrentItem().getAmount());
      arena.setOptionValue(ArenaOption.MODE_VALUE, e.getCurrentItem().getAmount());
      ConfigUtils.saveConfig(plugin, config, "arenas");
      new SetupInventory(arena, setupInventory.getPlayer()).openModes();
    }), 0, 0);

    pane.addItem(new GuiItem(new ItemBuilder(Material.REDSTONE_BLOCK)
      .name(plugin.getChatManager().colorRawMessage("&e&lSet Mode"))
      .lore(ChatColor.GRAY + "LEFT click to set HEARTS")
      .lore(ChatColor.GRAY + "RIGHT click to set POINTS")
      .lore(ChatColor.DARK_GRAY + "(Points: Base with the most points win")
      .lore(ChatColor.DARK_GRAY + "(Hearts: Kinda Last Team Standing)")
      .lore("", setupInventory.getSetupUtilities().isOptionDone("instances." + arena.getId() + ".mode"))
      .build(), e -> {
      Arena.Mode mode = Arena.Mode.POINTS;
      if(e.isLeftClick()) {
        mode = Arena.Mode.HEARTS;
      }
      config.set("instances." + arena.getId() + ".mode", mode.toString());
      arena.setMode(mode);
      ConfigUtils.saveConfig(plugin, config, "arenas");
      new SetupInventory(arena, setupInventory.getPlayer()).openModes();
    }), 1, 0);

    pane.addItem(new GuiItem(new ItemBuilder(XMaterial.REDSTONE_TORCH.parseMaterial())
      .amount(setupInventory.getSetupUtilities().getMinimumValueHigherThanZero("resetblocks"))
      .name(plugin.getChatManager().colorRawMessage("&e&lSet reset blocks round"))
      .lore(ChatColor.GRAY + "LEFT click to decrease")
      .lore(ChatColor.GRAY + "RIGHT click to increase")
      .lore(ChatColor.DARK_GRAY + "(After how many rounds should we reset blocks?)")
      .lore(ChatColor.DARK_GRAY + "(SHIFT LEFT CLICK to disable reset)")
      .lore("", setupInventory.getSetupUtilities().isOptionDone("instances." + arena.getId() + ".resetblocks"))
      .build(), e -> {
      ItemStack itemStack = e.getInventory().getItem(e.getSlot());
      if(itemStack == null || e.getCurrentItem() == null) {
        return;
      }
      if(e.getClick().isRightClick()) {
        e.getCurrentItem().setAmount(e.getCurrentItem().getAmount() + 1);
      }
      if(e.getClick().isLeftClick() && !e.getClick().isShiftClick()) {
        e.getCurrentItem().setAmount(e.getCurrentItem().getAmount() - 1);
      }
      if(e.getClick().isShiftClick() && e.getClick().isLeftClick()) {
        itemStack.setAmount(1);
      }
      if(itemStack.getAmount() < 1) {
        e.getWhoClicked().sendMessage(plugin.getChatManager().colorRawMessage("&c&l✖ &cWarning | Please do not set amount lower than 1!"));
        itemStack.setAmount(1);
      }
      if(e.getClick().isShiftClick() && e.getClick().isLeftClick()) {
        config.set("instances." + arena.getId() + ".resetblocks", 0);
        arena.setOptionValue(ArenaOption.RESET_BLOCKS, 0);
      } else {
        config.set("instances." + arena.getId() + ".resetblocks", e.getCurrentItem().getAmount());
        arena.setOptionValue(ArenaOption.RESET_BLOCKS, e.getCurrentItem().getAmount());
      }
      ConfigUtils.saveConfig(plugin, config, "arenas");
      new SetupInventory(arena, setupInventory.getPlayer()).openModes();
    }), 2, 0);

  }
}
