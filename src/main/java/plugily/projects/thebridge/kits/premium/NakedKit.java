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

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.kits.basekits.PremiumKit;
import plugily.projects.minigamesbox.classic.user.User;
import plugily.projects.minigamesbox.classic.utils.helper.WeaponHelper;
import plugily.projects.minigamesbox.classic.utils.version.events.api.PlugilyPlayerInteractEvent;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XMaterial;
import plugily.projects.thebridge.arena.Arena;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static plugily.projects.thebridge.kits.basekits.KitUtil.addBuildBlocks;

/**
 * Created by Tom on 8/02/2015.
 */
public class NakedKit extends PremiumKit implements Listener {

  private final List<Material> armorTypes = new ArrayList<>();

  public NakedKit() {
    super("Naked", new MessageBuilder("KIT_CONTENT_WILD_NAKED_NAME").asKey().build(), XMaterial.IRON_SWORD.parseItem());
    List<String> description = getPlugin().getLanguageManager().getLanguageListFromKey("KIT_CONTENT_WILD_NAKED_DESCRIPTION");
    setDescription(description);
    getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
    getPlugin().getKitRegistry().registerKit(this);
    setupArmorTypes();
  }

  private void setupArmorTypes() {
    Stream.of(Material.LEATHER_BOOTS, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_HELMET,
      XMaterial.GOLDEN_BOOTS.parseMaterial(), XMaterial.GOLDEN_CHESTPLATE.parseMaterial(), XMaterial.GOLDEN_LEGGINGS.parseMaterial(),
      XMaterial.GOLDEN_HELMET.parseMaterial(), Material.DIAMOND_BOOTS, Material.DIAMOND_LEGGINGS, Material.DIAMOND_CHESTPLATE,
      Material.DIAMOND_HELMET, Material.IRON_CHESTPLATE, Material.IRON_BOOTS, Material.IRON_HELMET, Material.IRON_LEGGINGS,
      Material.CHAINMAIL_BOOTS, Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_HELMET)
      .forEach(armorTypes::add);
  }

  @Override
  public boolean isUnlockedByPlayer(Player player) {
    return player.hasPermission("thebridge.kit.naked") || getPlugin().getPermissionsManager().hasPermissionString("KIT_PREMIUM_UNLOCK", player);
  }

  @Override
  public void giveKitItems(Player player) {
    ItemStack itemStack = XMaterial.IRON_SWORD.parseItem();
    itemStack.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 6);
    itemStack.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 2);
    itemStack.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
    player.getInventory().addItem(itemStack);
    player.getInventory().addItem(WeaponHelper.getEnchanted(XMaterial.DIAMOND_PICKAXE.parseItem(), new Enchantment[]{
      Enchantment.DURABILITY, Enchantment.DIG_SPEED}, new int[]{10, 4}));
    Arena arena = (Arena) getPlugin().getArenaRegistry().getArena(player);
    if(arena == null) {
      return;
    }
    addBuildBlocks(player, arena);
  }

  @EventHandler
  public void onArmor(InventoryClickEvent event) {
    if(!(event.getWhoClicked() instanceof Player)) {
      return;
    }
    User user = getPlugin().getUserManager().getUser((Player) event.getWhoClicked());
    if(!getPlugin().getArenaRegistry().isInArena((Player) event.getWhoClicked())) {
      return;
    }
    if(!(user.getKit() instanceof NakedKit)) {
      return;
    }
    if(!(event.getInventory().getType().equals(InventoryType.PLAYER) || event.getInventory().getType().equals(InventoryType.CRAFTING))) {
      return;
    }
    Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
      for(ItemStack stack : event.getWhoClicked().getInventory().getArmorContents()) {
        if(stack == null || !armorTypes.contains(stack.getType())) {
          continue;
        }
        //we cannot cancel event using scheduler, we must remove all armor contents from inventory manually
        new MessageBuilder("KIT_CONTENT_WILD_NAKED_CANNOT_WEAR_ARMOR").asKey().send(user.getPlayer());
        event.getWhoClicked().getInventory().setHelmet(new ItemStack(Material.AIR, 1));
        event.getWhoClicked().getInventory().setChestplate(new ItemStack(Material.AIR, 1));
        event.getWhoClicked().getInventory().setLeggings(new ItemStack(Material.AIR, 1));
        event.getWhoClicked().getInventory().setBoots(new ItemStack(Material.AIR, 1));
        return;
      }
    }, 1);
  }
  @EventHandler
  public void onArmorClick(PlugilyPlayerInteractEvent event) {
    if(!getPlugin().getArenaRegistry().isInArena(event.getPlayer())) {
      return;
    }
    if(!(getPlugin().getUserManager().getUser(event.getPlayer()).getKit() instanceof NakedKit) || !event.hasItem()) {
      return;
    }
    if(armorTypes.contains(event.getItem().getType())) {
      event.setCancelled(true);
      new MessageBuilder("KIT_CONTENT_WILD_NAKED_CANNOT_WEAR_ARMOR").asKey().player(event.getPlayer()).sendPlayer();
    }
  }
}
