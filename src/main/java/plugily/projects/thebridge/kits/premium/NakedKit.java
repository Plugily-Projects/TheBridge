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
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionType;
import pl.plajerlair.commonsbox.minecraft.compat.XMaterial;
import pl.plajerlair.commonsbox.minecraft.helper.WeaponHelper;
import plugily.projects.thebridge.arena.Arena;
import plugily.projects.thebridge.arena.ArenaRegistry;
import plugily.projects.thebridge.arena.ArenaState;
import plugily.projects.thebridge.handlers.PermissionsManager;
import plugily.projects.thebridge.kits.KitRegistry;
import plugily.projects.thebridge.kits.basekits.PremiumKit;
import plugily.projects.thebridge.user.User;
import plugily.projects.thebridge.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by Tom on 8/02/2015.
 */
public class NakedKit extends PremiumKit implements Listener {

  private final List<Material> armorTypes = new ArrayList<>();

  public NakedKit() {
    setName(getPlugin().getChatManager().colorMessage("Kits.Wild-Naked.Name"));
    List<String> description = Utils.splitString(getPlugin().getChatManager().colorMessage("Kits.Wild-Naked.Description"), 40);
    this.setDescription(description.toArray(new String[0]));
    getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
    KitRegistry.registerKit(this);
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
    return player.hasPermission("thebridge.kit.naked") || PermissionsManager.gotKitsPerm(player);
  }

  @Override
  public void giveKitItems(Player player) {
    ItemStack itemStack = new ItemStack(getMaterial());
    itemStack.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 6);
    itemStack.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 2);
    itemStack.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
    player.getInventory().addItem(itemStack);
    player.getInventory().addItem(WeaponHelper.getEnchanted(XMaterial.DIAMOND_PICKAXE.parseItem(), new Enchantment[]{
      Enchantment.DURABILITY, Enchantment.DIG_SPEED}, new int[]{10, 4}));
    Arena arena = ArenaRegistry.getArena(player);
    if(arena == null || arena.getArenaState() != ArenaState.IN_GAME) {
      return;
    }
    player.getInventory().addItem(new ItemStack(XMaterial.matchXMaterial(arena.getBase(player).getColor().toUpperCase() + getPlugin().getConfigPreferences().getColoredBlockMaterial()).get().parseMaterial(), 64));
  }

  @Override
  public Material getMaterial() {
    return Material.IRON_SWORD;
  }

  @Override
  public void reStock(Player player) {
    player.getInventory().addItem(Utils.getPotion(PotionType.INSTANT_HEAL, 1, true));
    Arena arena = ArenaRegistry.getArena(player);
    if(arena == null || arena.getArenaState() != ArenaState.IN_GAME) {
      return;
    }
    player.getInventory().addItem(new ItemStack(XMaterial.matchXMaterial(arena.getBase(player).getColor().toUpperCase() + getPlugin().getConfigPreferences().getColoredBlockMaterial()).get().parseMaterial(), 64));
  }

  @EventHandler
  public void onArmor(InventoryClickEvent event) {
    if(!(event.getWhoClicked() instanceof Player)) {
      return;
    }
    User user = getPlugin().getUserManager().getUser((Player) event.getWhoClicked());
    if(!ArenaRegistry.isInArena((Player) event.getWhoClicked())) {
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
        event.getWhoClicked().sendMessage(getPlugin().getChatManager().colorMessage("Kits.Wild-Naked.Cannot-Wear-Armor"));
        event.getWhoClicked().getInventory().setHelmet(new ItemStack(Material.AIR, 1));
        event.getWhoClicked().getInventory().setChestplate(new ItemStack(Material.AIR, 1));
        event.getWhoClicked().getInventory().setLeggings(new ItemStack(Material.AIR, 1));
        event.getWhoClicked().getInventory().setBoots(new ItemStack(Material.AIR, 1));
        return;
      }
    }, 1);
  }

  @EventHandler
  public void onArmorClick(PlayerInteractEvent event) {
    if(!ArenaRegistry.isInArena(event.getPlayer())) {
      return;
    }
    User user = getPlugin().getUserManager().getUser(event.getPlayer());
    if(!(user.getKit() instanceof NakedKit) || !event.hasItem()) {
      return;
    }
    if(armorTypes.contains(event.getItem().getType())) {
      event.setCancelled(true);
      event.getPlayer().sendMessage(getPlugin().getChatManager().colorMessage("Kits.Wild-Naked.Cannot-Wear-Armor"));
    }
  }
}
