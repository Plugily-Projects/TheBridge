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

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.utils.misc.ColorUtil;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XMaterial;
import plugily.projects.thebridge.arena.Arena;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 26.03.2022
 */
public class KitUtil {

  public static ItemStack handleItem(PluginMain plugin, Player player, ItemStack itemStack) {
    // Gets the current arena the player is in
    Arena arena = (Arena) plugin.getArenaRegistry().getArena(player);

    if (arena == null) {
      return itemStack;
    }

    // Replaces white terracotta with coloured terracotta if the player is in a team
    if (itemStack.getType().equals(Material.WHITE_TERRACOTTA)) {
      Arena pluginArena = arena.getPlugin().getArenaRegistry().getArena(arena.getId());
      if (pluginArena == null) {
        return itemStack;
      }
      itemStack.setType(XMaterial.matchXMaterial(pluginArena.getBase(player).getMaterialColor().toUpperCase() + "_TERRACOTTA").get().parseMaterial());
      return itemStack;
    }

    // Replaces leather armour with the coloured leather armour if the player is in a team
    if (itemStack.getType().equals(Material.LEATHER_HELMET) || itemStack.getType().equals(Material.LEATHER_CHESTPLATE) || itemStack.getType().equals(Material.LEATHER_LEGGINGS) || itemStack.getType().equals(Material.LEATHER_BOOTS)) {
      LeatherArmorMeta itemMeta = (LeatherArmorMeta) itemStack.getItemMeta();
      assert itemMeta != null;
      itemMeta.setColor(ColorUtil.fromChatColor(ChatColor.valueOf(arena.getBase(player).getColor().toUpperCase())));
      itemStack.setItemMeta(itemMeta);
      return itemStack;
    }

    return itemStack;
  }
}
