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

import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.handlers.setup.categories.PluginLocationCategory;
import plugily.projects.minigamesbox.classic.handlers.setup.items.category.LocationItem;
import plugily.projects.minigamesbox.classic.handlers.setup.items.category.LocationSelectorItem;
import plugily.projects.minigamesbox.classic.utils.dimensional.Cuboid;
import plugily.projects.minigamesbox.classic.utils.helper.ItemBuilder;
import plugily.projects.minigamesbox.classic.utils.serialization.LocationSerializer;
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

    LocationSelectorItem arenaBorder = new LocationSelectorItem(getSetupInventory(), new ItemBuilder(XMaterial.BEDROCK.parseMaterial()), "Arena Cuboid/Border", "Location where all bases and lines are in \n (players will be able to build inside) \n MAKE SURE TO SET IT OUTSIDE \n OF THE BASES (Baselocations) WHICH YOU SET BEFORE!", "arenalocation", inventoryClickEvent -> {
      String path = "instances." + getSetupInventory().getArenaKey();
      if(getSetupInventory().getConfig().getString(path + ".arenalocation.1") != null && getSetupInventory().getConfig().getString(path + ".arenalocation.2") != null) {
        Cuboid arenaCuboid = new Cuboid(LocationSerializer.getLocation(getSetupInventory().getConfig().getString(path + ".arenalocation.1", "world,364.0,63.0,-72.0,0.0,0.0")), LocationSerializer.getLocation(getSetupInventory().getConfig().getString(path + ".arenalocation.2", "world,364.0,63.0,-72.0,0.0,0.0")));
        for(String bases : getSetupInventory().getConfig().getConfigurationSection("instances." + getSetupInventory().getArenaKey() + ".bases").getKeys(false)) {
          if(getSetupInventory().getConfig().getString(path + "." + bases + ".baselocation.1") != null && getSetupInventory().getConfig().getString(path + "." + bases + ".baselocation.2") != null) {
            if(!(arenaCuboid.isIn(LocationSerializer.getLocation(getSetupInventory().getConfig().getString(path + "." + bases + ".baselocation.1"))) && arenaCuboid.isIn(LocationSerializer.getLocation(getSetupInventory().getConfig().getString(path + "." + bases + ".baselocation.2"))))) {
              new MessageBuilder("&c&l✘ &cArena validation failed! Please set your arena cuboids outside your base cuboids! The arena needs to be bigger than your bases locations! The following base is not inside the arena cuboid: " + bases).prefix().player(getSetupInventory().getPlayer()).sendPlayer();
            }
          }
        }
      }
    });
    gui.setItem((getInventoryLine() * 9) + 5, arenaBorder);
    getItemList().add(arenaBorder);
  }
}