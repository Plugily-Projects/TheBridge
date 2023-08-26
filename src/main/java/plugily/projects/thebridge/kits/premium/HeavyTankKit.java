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

package plugily.projects.thebridge.kits.premium;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.kits.basekits.PremiumKit;
import plugily.projects.minigamesbox.classic.utils.helper.WeaponHelper;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XMaterial;
import plugily.projects.thebridge.kits.basekits.KitUtil;

import java.util.List;

/**
 * Created by Tigerpanzer_02 on 27.12.2020.
 */
public class HeavyTankKit extends PremiumKit {

  public HeavyTankKit() {
    setName(new MessageBuilder("KIT_CONTENT_HEAVY_TANK_NAME").asKey().build());
    setKey("HeavyTank");
    List<String> description = getPlugin().getLanguageManager().getLanguageListFromKey("KIT_CONTENT_HEAVY_TANK_DESCRIPTION");
    setDescription(description);
    getPlugin().getKitRegistry().registerKit(this);
  }

  @Override
  public boolean isUnlockedByPlayer(Player player) {
    return getPlugin().getPermissionsManager().hasPermissionString("KIT_PREMIUM_UNLOCK", player) || player.hasPermission("thebridge.kit.heavytank");
  }

  @Override
  public void setupKitItems() {
    addKitItem(WeaponHelper.getEnchanted(new ItemStack(Material.STICK), new Enchantment[]{Enchantment.DURABILITY, Enchantment.DAMAGE_ALL}, new int[]{3, 2}), 0);
    addKitItem(new ItemStack(XMaterial.COOKED_PORKCHOP.parseMaterial(), 8), 1);
    addKitItem(WeaponHelper.getEnchanted(XMaterial.DIAMOND_PICKAXE.parseItem(), new Enchantment[]{
      Enchantment.DURABILITY, Enchantment.DIG_SPEED}, new int[]{10, 2}), 2);

    addKitItem(new ItemStack(XMaterial.WHITE_TERRACOTTA.parseMaterial(), 64), 8);

    setKitHelmet(new ItemStack(XMaterial.IRON_HELMET.parseMaterial()));
    setKitChestplate(new ItemStack(XMaterial.IRON_CHESTPLATE.parseMaterial()));
    setKitLeggings(new ItemStack(XMaterial.IRON_LEGGINGS.parseMaterial()));
    setKitBoots(new ItemStack(XMaterial.IRON_BOOTS.parseMaterial()));
  }

  @Override
  public void giveKitItems(Player player) {
    super.giveKitItems(player);
    VersionUtils.setMaxHealth(player, 40.0);
    player.setHealth(40.0);
  }

  @Override
  public ItemStack handleItem(Player player, ItemStack itemStack) {
    return KitUtil.handleItem(getPlugin(), player, itemStack);
  }

  @Override
  public Material getMaterial() {
    return Material.DIAMOND_CHESTPLATE;
  }


}
