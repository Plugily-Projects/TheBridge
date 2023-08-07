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

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import plugily.projects.minigamesbox.classic.arena.PluginArena;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XMaterial;
import plugily.projects.thebridge.arena.Arena;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 26.03.2022
 */
public class KitUtil {

  public static void addBuildBlocks(Player player, PluginArena arena) {
    Arena pluginArena = (Arena) arena.getPlugin().getArenaRegistry().getArena(arena.getId());
    if (pluginArena == null) {
      return;
    }
    ItemStack itemStack = XMaterial.matchXMaterial(pluginArena.getBase(player).getMaterialColor().toUpperCase() + arena.getPlugin().getConfig().getString("Colored-Block-Material", "_TERRACOTTA")).get().parseItem();
    itemStack.setAmount(64);
    if(player.getInventory().getItem(8) == null) {
      player.getInventory().setItem(8, itemStack);
      return;
    }
    player.getInventory().addItem(itemStack);
  }


}
