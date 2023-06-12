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

package plugily.projects.thebridge.kits.free;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import plugily.projects.minigamesbox.classic.arena.ArenaState;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.kits.basekits.FreeKit;
import plugily.projects.minigamesbox.classic.utils.helper.ArmorHelper;
import plugily.projects.minigamesbox.classic.utils.helper.WeaponHelper;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XMaterial;
import plugily.projects.thebridge.arena.Arena;

import java.util.List;

import static plugily.projects.thebridge.kits.basekits.KitUtil.addBuildBlocks;

/**
 * Created by Tom on 18/08/2014.
 */
public class LightTankKit extends FreeKit {

  public LightTankKit() {
    setName(new MessageBuilder("KIT_CONTENT_LIGHT_TANK_NAME").asKey().build());
    setKey("LightTank");
    List<String> description = getPlugin().getLanguageManager().getLanguageListFromKey("KIT_CONTENT_LIGHT_TANK_DESCRIPTION");
    setDescription(description);
    getPlugin().getKitRegistry().registerKit(this);
  }

  @Override
  public boolean isUnlockedByPlayer(Player player) {
    return true;
  }

  @Override
  public void giveKitItems(Player player) {
    player.getInventory().addItem(WeaponHelper.getUnBreakingSword(WeaponHelper.ResourceType.WOOD, 10));
    player.getInventory().addItem(new ItemStack(XMaterial.COOKED_PORKCHOP.parseMaterial(), 8));
    ArmorHelper.setArmor(player, ArmorHelper.ArmorType.IRON);
    player.getInventory().addItem(WeaponHelper.getEnchanted(XMaterial.DIAMOND_PICKAXE.parseItem(), new Enchantment[]{
        Enchantment.DURABILITY, Enchantment.DIG_SPEED}, new int[]{10, 2}));
    VersionUtils.setMaxHealth(player, 26.0);
    player.setHealth(26.0);
    Arena arena = (Arena) getPlugin().getArenaRegistry().getArena(player);
    if(arena == null) {
      return;
    }
    addBuildBlocks(player, arena);
  }

  @Override
  public Material getMaterial() {
    return Material.LEATHER_CHESTPLATE;
  }

  @Override
  public void reStock(Player player) {
    Arena arena = (Arena) getPlugin().getArenaRegistry().getArena(player);
    if(arena == null) {
      return;
    }
    addBuildBlocks(player, arena);
  }
}
