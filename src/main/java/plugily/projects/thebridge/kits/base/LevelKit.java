/*
 *  MiniGamesBox - Library box with massive content that could be seen as minigames core.
 *  Copyright (C) 2023 Plugily Projects - maintained by Tigerpanzer_02 and contributors
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package plugily.projects.thebridge.kits.base;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import plugily.projects.thebridge.arena.Arena;
import plugily.projects.thebridge.kits.KitUtils;

/**
 * @author Lagggpixel
 * <p>
 * Created at 23.09.2023
 */
public class LevelKit extends plugily.projects.minigamesbox.classic.kits.basekits.LevelKit {
  public LevelKit(String key, String name, ItemStack itemStack) {
    super(key, name, itemStack);
  }

  @Override
  public ItemStack handleItem(Player player, ItemStack itemStack) {
    Arena arena = (Arena) this.getPlugin().getArenaRegistry().getArena(player);
    return KitUtils.handleItem(arena, player, itemStack);
  }
}
