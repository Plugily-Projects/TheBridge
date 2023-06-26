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

import plugily.projects.minigamesbox.classic.handlers.setup.categories.PluginLocationCategory;
import plugily.projects.minigamesbox.classic.handlers.setup.items.category.LocationItem;
import plugily.projects.minigamesbox.classic.handlers.setup.items.category.LocationSelectorItem;
import plugily.projects.minigamesbox.classic.utils.helper.ItemBuilder;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XMaterial;
import plugily.projects.minigamesbox.inventory.normal.NormalFastInv;


/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 01.07.2022
 */
public class LocationCategory extends PluginLocationCategory {
  @Override
  public void addItems(NormalFastInv gui) {
    super.addItems(gui);

    LocationItem midLocation = new LocationItem(getSetupInventory(), new ItemBuilder(XMaterial.BEACON.parseMaterial()), "Mid", "Set the location where all \n lines will be crossed from each base", "midlocation");
    gui.setItem((getInventoryLine() * 9) + 4, midLocation);
    getItemList().add(midLocation);

    LocationSelectorItem arenaBorder = new LocationSelectorItem(getSetupInventory(), new ItemBuilder(XMaterial.BEDROCK.parseMaterial()), "Arena", "Location where all bases and lines are in \n (players will be able to build inside)", "arenalocation");
    gui.setItem((getInventoryLine() * 9) + 5, arenaBorder);
    getItemList().add(arenaBorder);

    LocationSelectorItem bridgeLocation = new LocationSelectorItem(getSetupInventory(), new ItemBuilder(XMaterial.RED_TERRACOTTA.parseMaterial()), "Bridge", "Location where the bridge is.\n Players will be able to break pre-built blocks inside this region", "bridgelocation");
    gui.setItem((getInventoryLine()*9 + 8), bridgeLocation);
    getItemList().add(bridgeLocation);
  }
}