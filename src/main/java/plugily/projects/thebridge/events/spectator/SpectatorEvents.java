
package plugily.projects.thebridge.events.spectator;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import plugily.projects.thebridge.Main;
import plugily.projects.thebridge.arena.ArenaRegistry;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 23.11.2020
 */
public class SpectatorEvents implements Listener {

  private final Main plugin;

  public SpectatorEvents(Main plugin) {
    this.plugin = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onSpectatorTarget(EntityTargetEvent e) {
    if (!(e.getTarget() instanceof Player)) {
      return;
    }
    if (plugin.getUserManager().getUser((Player) e.getTarget()).isSpectator()) {
      e.setCancelled(true);
      e.setTarget(null);
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onSpectatorTarget(EntityTargetLivingEntityEvent e) {
    if (!(e.getTarget() instanceof Player)) {
      return;
    }
    if (plugin.getUserManager().getUser((Player) e.getTarget()).isSpectator()) {
      e.setCancelled(true);
      e.setTarget(null);
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onBlockPlace(BlockPlaceEvent event) {
    if (plugin.getUserManager().getUser(event.getPlayer()).isSpectator()) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onBlockBreak(BlockBreakEvent event) {
    if (plugin.getUserManager().getUser(event.getPlayer()).isSpectator()) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onDropItem(PlayerDropItemEvent event) {
    if (plugin.getUserManager().getUser(event.getPlayer()).isSpectator()) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onBucketEmpty(PlayerBucketEmptyEvent event) {
    if (plugin.getUserManager().getUser(event.getPlayer()).isSpectator()) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onInteract(PlayerInteractEntityEvent event) {
    if (plugin.getUserManager().getUser(event.getPlayer()).isSpectator()) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onShear(PlayerShearEntityEvent event) {
    if (plugin.getUserManager().getUser(event.getPlayer()).isSpectator()) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onConsume(PlayerItemConsumeEvent event) {
    if (plugin.getUserManager().getUser(event.getPlayer()).isSpectator()) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onFoodLevelChange(FoodLevelChangeEvent event) {
    if (!(event.getEntity() instanceof Player)) {
      return;
    }
    Player player = (Player) event.getEntity();
    if (plugin.getUserManager().getUser(player).isSpectator()) {
      event.setFoodLevel(20);
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onDamage(EntityDamageEvent event) {
    if (!(event.getEntity() instanceof Player)) {
      return;
    }
    Player player = (Player) event.getEntity();
    if (!plugin.getUserManager().getUser(player).isSpectator() || !ArenaRegistry.isInArena(player)) {
      return;
    }
    if (player.getLocation().getY() < 1) {
      //todo teleport player to team spot
      //player.teleport(ArenaRegistry.getArena(player).getPlayerSpawnPoints().get(0));
    }
    event.setCancelled(true);
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onDamageByBlock(EntityDamageByBlockEvent event) {
    if (!(event.getEntity() instanceof Player)) {
      return;
    }
    Player player = (Player) event.getEntity();
    if (plugin.getUserManager().getUser(player).isSpectator()) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onDamageByEntity(EntityDamageByEntityEvent event) {
    if (!(event.getDamager() instanceof Player)) {
      return;
    }
    Player player = (Player) event.getDamager();
    if (plugin.getUserManager().getUser(player).isSpectator()) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onPickup(EntityPickupItemEvent event) {
    if (event.getEntity() instanceof Player && plugin.getUserManager().getUser((Player) event.getEntity()).isSpectator()) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onRightClick(PlayerInteractEvent event) {
    if (ArenaRegistry.isInArena(event.getPlayer()) && plugin.getUserManager().getUser(event.getPlayer()).isSpectator()) {
      event.setCancelled(true);
    }
  }

}
