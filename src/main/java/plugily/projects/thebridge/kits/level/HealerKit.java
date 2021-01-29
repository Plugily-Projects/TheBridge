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

package plugily.projects.thebridge.kits.level;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionType;
import pl.plajerlair.commonsbox.minecraft.compat.XMaterial;
import pl.plajerlair.commonsbox.minecraft.helper.ArmorHelper;
import pl.plajerlair.commonsbox.minecraft.helper.WeaponHelper;
import pl.plajerlair.commonsbox.minecraft.misc.ColorUtil;
import plugily.projects.thebridge.api.StatsStorage;
import plugily.projects.thebridge.arena.Arena;
import plugily.projects.thebridge.arena.ArenaRegistry;
import plugily.projects.thebridge.arena.ArenaState;
import plugily.projects.thebridge.kits.KitRegistry;
import plugily.projects.thebridge.kits.basekits.LevelKit;
import plugily.projects.thebridge.utils.Utils;

import java.util.List;

/**
 * Created by Tom on 18/08/2014.
 */
public class HealerKit extends LevelKit {

  public HealerKit() {
    setName(getPlugin().getChatManager().colorMessage("Kits.Healer.Name"));
    List<String> description = Utils.splitString(getPlugin().getChatManager().colorMessage("Kits.Healer.Description"), 40);
    this.setDescription(description.toArray(new String[0]));
    setLevel(getKitsConfig().getInt("Required-Level.Healer"));
    KitRegistry.registerKit(this);
  }

  @Override
  public boolean isUnlockedByPlayer(Player player) {
    return getPlugin().getUserManager().getUser(player).getStat(StatsStorage.StatisticType.LEVEL) >= this.getLevel() || player.hasPermission("thebridge.kit.healer");
  }

  @Override
  public void giveKitItems(Player player) {
    player.getInventory().addItem(WeaponHelper.getUnBreakingSword(WeaponHelper.ResourceType.WOOD, 10));
    player.getInventory().addItem(WeaponHelper.getEnchanted(XMaterial.DIAMOND_PICKAXE.parseItem(), new Enchantment[]{
      Enchantment.DURABILITY, Enchantment.DIG_SPEED}, new int[]{10, 2}));
    player.getInventory().addItem(new ItemStack(XMaterial.COOKED_PORKCHOP.parseMaterial(), 8));
    player.getInventory().addItem(Utils.getPotion(PotionType.INSTANT_HEAL, 2, true));
    player.getInventory().addItem(Utils.getPotion(PotionType.REGEN, 1, true));
    Arena arena = ArenaRegistry.getArena(player);
    if(arena == null || arena.getArenaState() != ArenaState.IN_GAME) {
      return;
    }
    ArmorHelper.setColouredArmor(ColorUtil.fromChatColor(ChatColor.valueOf(arena.getBase(player).getColor().toUpperCase())), player);
    player.getInventory().addItem(new ItemStack(XMaterial.matchXMaterial(arena.getBase(player).getColor().toUpperCase() + getPlugin().getConfigPreferences().getColoredBlockMaterial()).get().parseMaterial(), 64));

  }

  @Override
  public Material getMaterial() {
    return XMaterial.POPPY.parseMaterial();
  }

  @Override
  public void reStock(Player player) {
    for(int i = 0; i < 2; i++) {
      player.getInventory().addItem(Utils.getPotion(PotionType.INSTANT_HEAL, 2, true));
    }
    for(int i = 0; i < 2; i++) {
      player.getInventory().addItem(Utils.getPotion(PotionType.REGEN, 1, true));
    }
    Arena arena = ArenaRegistry.getArena(player);
    if(arena == null || arena.getArenaState() != ArenaState.IN_GAME) {
      return;
    }
    player.getInventory().addItem(new ItemStack(XMaterial.matchXMaterial(arena.getBase(player).getColor().toUpperCase() + getPlugin().getConfigPreferences().getColoredBlockMaterial()).get().parseMaterial(), 64));
  }
}
