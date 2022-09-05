/*
 *
 * BuildBattle - Ultimate building competition minigame
 * Copyright (C) 2021 Plugily Projects - maintained by Tigerpanzer_02, 2Wild4You and contributors
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

package plugily.projects.thebridge.handlers.setup;

import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import plugily.projects.minigamesbox.classic.handlers.setup.categories.PluginSpecificCategory;
import plugily.projects.minigamesbox.classic.utils.helper.ItemBuilder;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XMaterial;
import plugily.projects.minigamesbox.inventory.common.item.ClickableItem;
import plugily.projects.minigamesbox.inventory.normal.NormalFastInv;
import plugily.projects.thebridge.handlers.setup.components.BasePage;


/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 01.07.2022
 */
public class SpecificCategory extends PluginSpecificCategory {
  @Override
  public void addItems(NormalFastInv gui) {
    super.addItems(gui);

    gui.setItem((getInventoryLine() * 9) + 1, ClickableItem.of(new ItemBuilder(XMaterial.ORANGE_STAINED_GLASS_PANE.parseMaterial()).name("&e&lEdit Base").lore(ChatColor.GRAY + "Here you can add/edit a base")
        .lore(ChatColor.GRAY + "Make sure to register the base before continuing!").build(), event -> openBaseMenu(event.getWhoClicked())));

  }
  public void openBaseMenu(HumanEntity player) {
    NormalFastInv pagedGui = new BasePage(54, getSetupInventory().getPlugin().getPluginMessagePrefix() + "Base Editor Menu", getSetupInventory());
    pagedGui.open(player);
  }
}