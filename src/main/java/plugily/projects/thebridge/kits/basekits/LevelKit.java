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

import org.bukkit.inventory.ItemStack;
import plugily.projects.commonsbox.minecraft.item.ItemBuilder;

/**
 * Created by Tom on 14/08/2014.
 */
public abstract class LevelKit extends Kit {

  private int level;

  public int getLevel() {
    return level;
  }

  public void setLevel(int level) {
    this.level = level;
  }

  @Override
  public ItemStack getItemStack() {
    return new ItemBuilder(getMaterial())
      .name(getName())
      .lore(getDescription())
      .lore(getPlugin().getChatManager().colorMessage("Kits.Kit-Menu.Lores.Unlock-At-Level")
        .replace("%NUMBER%", Integer.toString(getLevel())))
      .build();
  }
}
