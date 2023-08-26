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

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.kits.basekits.LevelKit;
import plugily.projects.minigamesbox.classic.utils.helper.WeaponHelper;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XMaterial;
import plugily.projects.thebridge.kits.basekits.KitUtil;

import java.util.List;

/**
 * Created by Tom on 14/08/2014.
 */
public class ArcherKit extends LevelKit {

  public ArcherKit() {
    setLevel(getKitsConfig().getInt("Required-Level.Archer"));
    setKey("Archer");
    setName(new MessageBuilder("KIT_CONTENT_ARCHER_NAME").asKey().build());
    List<String> description = getPlugin().getLanguageManager().getLanguageListFromKey("KIT_CONTENT_ARCHER_DESCRIPTION");
    setDescription(description);
    getPlugin().getKitRegistry().registerKit(this);
  }

  @Override
  public boolean isUnlockedByPlayer(Player player) {
    return getPlugin().getUserManager().getUser(player).getStatistic("LEVEL") >= this.getLevel() || player.hasPermission("thebridge.kit.archer");
  }

  @Override
  public void setupKitItems() {
    addKitItem(WeaponHelper.getUnBreakingSword(WeaponHelper.ResourceType.WOOD, 10), 0);
    addKitItem(WeaponHelper.getEnchantedBow(Enchantment.ARROW_INFINITE, 10), 1);
    addKitItem(XMaterial.ARROW.parseItem(), 9);
    addKitItem(WeaponHelper.getEnchanted(XMaterial.DIAMOND_PICKAXE.parseItem(), new Enchantment[]{
      Enchantment.DURABILITY, Enchantment.DIG_SPEED}, new int[]{10, 2}), 2);
    addKitItem(new ItemStack(Material.COOKED_BEEF, 10), 3);

    addKitItem(new ItemStack(XMaterial.WHITE_TERRACOTTA.parseMaterial(), 64), 8);

    setKitHelmet(new ItemStack(XMaterial.LEATHER_HELMET.parseMaterial()));
    setKitChestplate(new ItemStack(XMaterial.LEATHER_CHESTPLATE.parseMaterial()));
    setKitLeggings(new ItemStack(XMaterial.LEATHER_LEGGINGS.parseMaterial()));
    setKitBoots(new ItemStack(XMaterial.LEATHER_BOOTS.parseMaterial()));
  }

  @Override
  public void giveKitItems(Player player) {
    super.giveKitItems(player);
    VersionUtils.setMaxHealth(player, 20.0);
    player.setHealth(20.0);
  }

  @Override
  public ItemStack handleItem(Player player, ItemStack itemStack) {
    return KitUtil.handleItem(getPlugin(), player, itemStack);
  }

  @Override
  public Material getMaterial() {
    return Material.BOW;
  }
}
