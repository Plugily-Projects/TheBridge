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

package plugily.projects.thebridge.kits.basekits;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import plugily.projects.commonsbox.minecraft.compat.xseries.XMaterial;
import plugily.projects.commonsbox.minecraft.configuration.ConfigUtils;
import plugily.projects.commonsbox.minecraft.item.ItemBuilder;
import plugily.projects.thebridge.Main;
import plugily.projects.thebridge.arena.Arena;

/**
 * Created by Tom on 25/07/2014.
 */
public abstract class Kit {

  private static Main plugin;
  private final FileConfiguration kitsConfig;
  private String name;
  private boolean unlockedOnDefault = false;
  private String[] description = {""};

  protected Kit() {
    kitsConfig = ConfigUtils.getConfig(plugin, "kits");
  }

  public Kit(String name) {
    this.name = name;
    kitsConfig = ConfigUtils.getConfig(plugin, "kits");
  }

  public static void init(Main plugin) {
    Kit.plugin = plugin;
  }

  public abstract boolean isUnlockedByPlayer(Player p);

  public boolean isUnlockedOnDefault() {
    return unlockedOnDefault;
  }

  public void setUnlockedOnDefault(boolean unlockedOnDefault) {
    this.unlockedOnDefault = unlockedOnDefault;
  }


  /**
   * @return main plugin
   */
  public Main getPlugin() {
    return plugin;
  }

  /**
   * @return config file of kits
   */
  public FileConfiguration getKitsConfig() {
    return kitsConfig;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String[] getDescription() {
    return description.clone();
  }

  public void setDescription(String[] description) {
    this.description = description.clone();
  }

  public abstract void giveKitItems(Player player);

  public abstract Material getMaterial();

  public ItemStack getItemStack() {
    return new ItemBuilder(getMaterial())
        .name(getName())
        .lore(getDescription())
        .build();
  }

  public void addBuildBlocks(Player player, Arena arena) {
    ItemStack itemStack = XMaterial.matchXMaterial(arena.getBase(player).getMaterialColor().toUpperCase() + getPlugin().getConfigPreferences().getColoredBlockMaterial()).get().parseItem();
    itemStack.setAmount(64);
    if(player.getInventory().getItem(8) == null) {
      player.getInventory().setItem(8, itemStack);
      return;
    }
    player.getInventory().addItem(itemStack);
  }

  public abstract void reStock(Player player);


}
