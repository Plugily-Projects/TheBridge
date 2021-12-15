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

package plugily.projects.thebridge.events;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import plugily.projects.commonsbox.minecraft.compat.ServerVersion;
import plugily.projects.commonsbox.minecraft.compat.VersionUtils;
import plugily.projects.commonsbox.minecraft.compat.events.api.CBPlayerInteractEvent;
import plugily.projects.commonsbox.minecraft.compat.events.api.CBPlayerSwapHandItemsEvent;
import plugily.projects.commonsbox.minecraft.compat.xseries.XMaterial;
import plugily.projects.thebridge.ConfigPreferences;
import plugily.projects.thebridge.Main;
import plugily.projects.thebridge.arena.Arena;
import plugily.projects.thebridge.arena.ArenaManager;
import plugily.projects.thebridge.arena.ArenaRegistry;
import plugily.projects.thebridge.arena.ArenaState;
import plugily.projects.thebridge.arena.ArenaUtils;
import plugily.projects.thebridge.handlers.items.SpecialItemManager;
import plugily.projects.thebridge.utils.Utils;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 23.11.2020
 */
public class Events implements Listener {

  private final Main plugin;

  public Events(Main plugin) {
    this.plugin = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler
  public void onItemSwap(CBPlayerSwapHandItemsEvent e) {
    if(ArenaRegistry.isInArena(e.getPlayer())) {
      e.setCancelled(true);
    }
  }

  @EventHandler
  public void onDrop(PlayerDropItemEvent event) {
    if(ArenaRegistry.isInArena(event.getPlayer())) {
      event.setCancelled(true);
    }
  }


  @EventHandler(priority = EventPriority.HIGHEST)
  public void onCommandExecute(PlayerCommandPreprocessEvent event) {
    Arena arena = ArenaRegistry.getArena(event.getPlayer());
    if(arena == null) {
      return;
    }
    if(!plugin.getConfig().getBoolean("Block-Commands-In-Game", true)) {
      return;
    }
    String command = event.getMessage().substring(1);
    command = (command.indexOf(' ') >= 0 ? command.substring(0, command.indexOf(' ')) : command);
    for(String msg : plugin.getConfig().getStringList("Whitelisted-Commands")) {
      if(command.equalsIgnoreCase(msg)) {
        return;
      }
    }
    if(event.getPlayer().isOp() || event.getPlayer().hasPermission("thebridge.admin") || event.getPlayer().hasPermission("thebridge.command.bypass")) {
      return;
    }
    if(command.equalsIgnoreCase("tb") || command.equalsIgnoreCase("thebridge")
        || event.getMessage().contains("thebridgeadmin") || event.getMessage().contains("leave")
        || command.equalsIgnoreCase("stats") || command.equalsIgnoreCase("tba")) {
      return;
    }
    event.setCancelled(true);
    event.getPlayer().sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage("In-Game.Only-Command-Ingame-Is-Leave"));
  }

  @EventHandler
  public void onInGameInteract(PlayerInteractEvent event) {
    Arena arena = ArenaRegistry.getArena(event.getPlayer());
    if(arena == null || event.getClickedBlock() == null) {
      return;
    }
    if(event.getClickedBlock().getType() == XMaterial.PAINTING.parseMaterial() || event.getClickedBlock().getType() == XMaterial.FLOWER_POT.parseMaterial()) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onInGameBedEnter(PlayerBedEnterEvent event) {
    if(ArenaRegistry.isInArena(event.getPlayer())) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onSpecialItem(CBPlayerInteractEvent event) {
    if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.PHYSICAL) {
      return;
    }
    Arena arena = ArenaRegistry.getArena(event.getPlayer());
    ItemStack itemStack = VersionUtils.getItemInHand(event.getPlayer());
    if(arena == null || !Utils.isNamed(itemStack)) {
      return;
    }
    String key = plugin.getSpecialItemManager().getRelatedSpecialItem(itemStack).getName();
    if(key == null) {
      return;
    }
    if(key.equalsIgnoreCase(SpecialItemManager.SpecialItems.FORCESTART.getName())) {
      event.setCancelled(true);
      ArenaUtils.arenaForceStart(event.getPlayer());
      return;
    }
    if(key.equals(SpecialItemManager.SpecialItems.LOBBY_LEAVE_ITEM.getName()) || key.equals(SpecialItemManager.SpecialItems.SPECTATOR_LEAVE_ITEM.getName())) {
      event.setCancelled(true);
      if(plugin.getConfigPreferences().getOption(ConfigPreferences.Option.BUNGEE_ENABLED)) {
        plugin.getBungeeManager().connectToHub(event.getPlayer());
      } else {
        ArenaManager.leaveAttempt(event.getPlayer(), arena);
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onFoodLevelChange(FoodLevelChangeEvent event) {
    if(event.getEntity().getType() != EntityType.PLAYER) {
      return;
    }
    Player player = (Player) event.getEntity();
    Arena arena = ArenaRegistry.getArena(player);
    if(arena == null) {
      return;
    }
    if(plugin.getConfigPreferences().getOption(ConfigPreferences.Option.DISABLE_FOOD_LOSE)) {
      event.setCancelled(true);
      event.setFoodLevel(20);
    }
    if(arena.getArenaState() != ArenaState.IN_GAME) {
      event.setFoodLevel(20);
      event.setCancelled(true);
    }
  }


  @EventHandler(priority = EventPriority.HIGH)
  public void onFoodLevelChange(PlayerItemConsumeEvent event) {
    Player player = event.getPlayer();
    Arena arena = ArenaRegistry.getArena(player);
    if(arena == null) {
      return;
    }
    if(event.getItem().getType() == XMaterial.GOLDEN_APPLE.parseMaterial()) {
      player.setFoodLevel(20);
      player.setHealth(VersionUtils.getMaxHealth(player));
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  //highest priority to fully protect our game
  public void onBuild(BlockPlaceEvent event) {
    if(ArenaRegistry.isInArena(event.getPlayer()) && ArenaRegistry.getArena(event.getPlayer()).getArenaState() != ArenaState.IN_GAME) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  //highest priority to fully protect our game
  public void onHangingBreakEvent(HangingBreakByEntityEvent event) {
    if(event.getEntity() instanceof ItemFrame || event.getEntity() instanceof Painting) {
      if(event.getRemover() instanceof Player && ArenaRegistry.isInArena((Player) event.getRemover())) {
        event.setCancelled(true);
        return;
      }
      if(!(event.getRemover() instanceof Arrow)) {
        return;
      }
      Arrow arrow = (Arrow) event.getRemover();
      if(arrow.getShooter() instanceof Player && ArenaRegistry.isInArena((Player) arrow.getShooter())) {
        event.setCancelled(true);
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onArmorStandDestroy(EntityDamageByEntityEvent event) {
    if(!(event.getEntity() instanceof LivingEntity)) {
      return;
    }
    LivingEntity livingEntity = (LivingEntity) event.getEntity();
    if(livingEntity.getType() != EntityType.ARMOR_STAND) {
      return;
    }
    if(event.getDamager() instanceof Player && ArenaRegistry.isInArena((Player) event.getDamager())) {
      event.setCancelled(true);
    } else if(event.getDamager() instanceof Arrow) {
      Arrow arrow = (Arrow) event.getDamager();
      if(arrow.getShooter() instanceof Player && ArenaRegistry.isInArena((Player) arrow.getShooter())) {
        event.setCancelled(true);
        return;
      }
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onInteractWithArmorStand(PlayerArmorStandManipulateEvent event) {
    if(ArenaRegistry.isInArena(event.getPlayer())) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onCraft(PlayerInteractEvent event) {
    if(!ArenaRegistry.isInArena(event.getPlayer())) {
      return;
    }
    if(event.getPlayer().getTargetBlock(null, 7).getType() == XMaterial.CRAFTING_TABLE.parseMaterial()) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onPlayerTeleport(PlayerTeleportEvent event) {
    if(!ArenaRegistry.isInArena(event.getPlayer())) {
      return;
    }
    if(event.getCause().equals(PlayerTeleportEvent.TeleportCause.END_PORTAL)) {
      event.setCancelled(true);
    }
    if(event.getCause().equals(PlayerTeleportEvent.TeleportCause.NETHER_PORTAL)) {
      event.setCancelled(true);
    }
  }
}
