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

package plugily.projects.thebridge.utils;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import pl.plajerlair.commonsbox.minecraft.compat.ServerVersion.Version;
import plugily.projects.thebridge.Main;

@SuppressWarnings("deprecation")
public abstract class NMS {

  private static final Main PLUGIN = JavaPlugin.getPlugin(Main.class);

  public static void setDurability(ItemStack item, short durability) {
    if(Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
      ItemMeta meta = item.getItemMeta();
      if(meta != null) {
        ((Damageable) meta).setDamage(durability);
      }
    } else {
      item.setDurability(durability);
    }
  }

  public static void hidePlayer(Player to, Player p) {
    if(Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
      to.hidePlayer(PLUGIN, p);
    } else {
      to.hidePlayer(p);
    }
  }

  public static void showPlayer(Player to, Player p) {
    if(Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
      to.showPlayer(PLUGIN, p);
    } else {
      to.showPlayer(p);
    }
  }
}
