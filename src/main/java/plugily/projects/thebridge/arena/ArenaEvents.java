/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (c) 2022  Plugily Projects - maintained by Tigerpanzer_02 and contributors
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
 */

package plugily.projects.thebridge.arena;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerMoveEvent;
import plugily.projects.minigamesbox.classic.arena.ArenaState;
import plugily.projects.minigamesbox.classic.arena.PluginArena;
import plugily.projects.minigamesbox.classic.arena.PluginArenaEvents;
import plugily.projects.minigamesbox.classic.arena.PluginArenaUtils;
import plugily.projects.minigamesbox.classic.handlers.items.SpecialItem;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.handlers.language.TitleBuilder;
import plugily.projects.minigamesbox.classic.user.User;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;
import plugily.projects.minigamesbox.classic.utils.version.events.api.PlugilyEntityPickupItemEvent;
import plugily.projects.minigamesbox.classic.utils.version.events.api.PlugilyPlayerPickupArrow;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XMaterial;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XSound;
import plugily.projects.thebridge.Main;
import plugily.projects.thebridge.arena.base.Base;
import plugily.projects.thebridge.arena.managers.ScoreboardManager;
import plugily.projects.thebridge.kits.level.ArcherKit;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

/**
 * @author Plajer
 * <p>Created at 13.03.2018
 */
public class ArenaEvents extends PluginArenaEvents {

  private final Main plugin;

  public ArenaEvents(Main plugin) {
    super(plugin);
    this.plugin = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onBlockBreakEvent(BlockBreakEvent event) {
    Player player = event.getPlayer();
    Arena arena = plugin.getArenaRegistry().getArena(player);
    if(arena == null) {
      return;
    }
    if(arena.getArenaState() != ArenaState.IN_GAME) {
      return;
    }
    if(!canBuild(arena, player, event.getBlock().getLocation())) {
      event.setCancelled(true);
      return;
    }

    if(arena.getPlacedBlocks().contains(event.getBlock())) {
      arena.removePlacedBlock(event.getBlock());
      // Does not work?
      event.getBlock().getDrops().clear();
      // Alternative
      event.getBlock().setType(XMaterial.AIR.parseMaterial());
    }
    else if(arena.getBridgeCuboid() != null && arena.getBridgeCuboid().isIn(event.getBlock().getLocation())) {
      arena.addBrokenBlock(event.getBlock().getLocation(), event.getBlock().getBlockData());
      // Does not work?
      event.getBlock().getDrops().clear();
      // Alternative
      event.getBlock().setType(XMaterial.AIR.parseMaterial());
    }
    event.setCancelled(true);
  }

  @EventHandler
  public void onBuild(BlockPlaceEvent event) {
    Player player = event.getPlayer();
    Arena arena = plugin.getArenaRegistry().getArena(player);
    if(arena == null) {
      return;
    }
    if(arena.getArenaState() != ArenaState.IN_GAME) {
      return;
    }
    if(!canBuild(arena, player, event.getBlock().getLocation())) {
      event.setCancelled(true);
      return;
    }
    if(arena.getBridgeCuboid() == null || !arena.getBridgeCuboid().isIn(event.getBlock().getLocation())) {
      // Only add blocks to the list if the block is not found to be in the broken blocks list
      // Making it so that resetting placed blocks and resetting broken blocks will not tamper with each other
      arena.addPlacedBlock(event.getBlock());
    }
  }

  public boolean canBuild(Arena arena, Player player, Location location) {
    if(!arena.getArenaBorder().isIn(location)) {
      new MessageBuilder("IN_GAME_MESSAGES_ARENA_BUILD_BREAK")
        .asKey()
        .player(player)
        .arena(arena)
        .sendPlayer();
      return false;
    }
    for(Base base : arena.getBases()) {
      if(base.getBaseCuboid().isIn(location)) {
        new MessageBuilder("IN_GAME_MESSAGES_ARENA_BUILD_BREAK")
          .asKey()
          .player(player)
          .arena(arena)
          .sendPlayer();
        return false;
      }
    }
    return true;
  }

  private void rewardLastAttacker(Arena arena, Player victim) {
    if(!arena.getHits().containsKey(victim)) {
      new MessageBuilder(MessageBuilder.ActionType.DEATH).arena(arena).player(victim).sendArena();
    } else {
      Player attacker = arena.getHits().get(victim);
      arena.removeHits(victim);
      plugin
        .getRewardsHandler()
        .performReward(attacker, plugin.getRewardsHandler().getRewardType("KILL"));
      plugin.getUserManager().addStat(attacker, plugin.getStatsStorage().getStatisticType("KILLS"));
      plugin.getUserManager().addExperience(attacker, 2);
      plugin.getUserManager().getUser(attacker).adjustStatistic("LOCAL_KILLS", 1);
      new MessageBuilder("IN_GAME_MESSAGES_ARENA_KILLED")
        .asKey()
        .player(victim)
        .arena(arena)
        .send(attacker);
      new MessageBuilder("IN_GAME_MESSAGES_ARENA_DEATH")
        .asKey()
        .player(victim)
        .value(attacker.getName())
        .arena(arena)
        .sendArena();
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onHit(EntityDamageByEntityEvent event) {
    if(event.getEntity() instanceof Player victim && event.getDamager() instanceof Player attacker) {
      if(!ArenaUtils.areInSameArena(victim, attacker)) {
        return;
      }
      Arena arena = plugin.getArenaRegistry().getArena(victim);
      if(arena == null || arena.getArenaState() != ArenaState.IN_GAME) {
        return;
      }
      if(plugin.getUserManager().getUser(attacker).isSpectator()) {
        event.setCancelled(true);
        return;
      }
      if(arena.isTeammate(attacker, victim)) {
        event.setCancelled(true);
        return;
      }
      arena.addHits(victim, attacker);
      if(victim.getHealth() - event.getDamage() <= 0) {
        event.setCancelled(true);
        playerDeath(victim, arena);
      }
      DecimalFormat df = new DecimalFormat("##.##");
      VersionUtils.sendActionBar(attacker, new MessageBuilder("IN_GAME_MESSAGES_ARENA_DAMAGE")
        .asKey()
        .player(victim)
        .value(df.format(victim.getHealth() - event.getDamage()))
        .build());
    }
  }

  private final HashMap<Player, Long> cooldownPortal = new HashMap<>();
  private final HashMap<Player, Long> cooldownOutside = new HashMap<>();

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerMove(PlayerMoveEvent event) {
    Player player = event.getPlayer();
    Arena arena = plugin.getArenaRegistry().getArena(player);
    if(arena == null) {
      return;
    }
    if(arena.isResetRound() && !plugin.getUserManager().getUser(player).isSpectator()) {
      roundReset(event, arena);
      return;
    }
    if(!arena.inBase(player)) {
      return;
    }
    if(!arena.getArenaBorder().isInWithMarge(player.getLocation(), 5)) {
      outsideArenaBorder(player, arena);
      return;
    }
    if(cooldownPortal.containsKey(player)) {
      if(cooldownPortal.get(player) <= System.currentTimeMillis() - 5000)
        cooldownPortal.remove(player);
      return;
    }
    if(arena.getBase(player).getPortalCuboid().isIn(player)) {
      insideOwnPortal(player, arena);
      return;
    }
    for(Base base : arena.getBases()) {
      if(base.getPortalCuboid().isIn(player)) {
        cooldownPortal.put(player, System.currentTimeMillis());
        if(base.getPoints() >= arena.getArenaOption("MODE_VALUE")) {
          portalOut(player, arena);
          return;
        }
        if(base.getAlivePlayersSize() == 0) {
          continue;
        }
        portalScored(player, arena, base);
        return;
      }
    }
  }

  private void roundReset(PlayerMoveEvent event, Arena arena) {
    if(arena.getBase(event.getPlayer()).getCageCuboid() != null) {
      return;
    }
    if(event.getFrom().getZ() != event.getTo().getZ()
      && event.getFrom().getX() != event.getTo().getX()) {
      event.setCancelled(true);
    }
  }

  private void portalScored(Player player, Arena arena, Base base) {
    arena.resetRound();
    if(arena.getMode() == Arena.Mode.HEARTS) {
      base.addPoint();
    } else if(arena.getMode() == Arena.Mode.POINTS) {
      arena.getBase(player).addPoint();
    }
    new TitleBuilder("IN_GAME_MESSAGES_ARENA_PORTAL_SCORED_TITLE")
      .asKey()
      .arena(arena)
      .player(player)
      .value(base.getFormattedColor())
      .sendArena();
    new MessageBuilder("IN_GAME_MESSAGES_ARENA_PORTAL_OPPONENT")
      .asKey()
      .player(player)
      .arena(arena)
      .value(base.getFormattedColor())
      .sendArena();
    ((ScoreboardManager) arena.getScoreboardManager()).resetBaseCache();
    plugin
      .getUserManager()
      .addStat(player, plugin.getStatsStorage().getStatisticType("SCORED_POINTS"));
    plugin.getUserManager().addExperience(player, 10);
    plugin
      .getUserManager()
      .addStat(player, plugin.getStatsStorage().getStatisticType("LOCAL_SCORED_POINTS"));
  }

  private void portalOut(Player player, Arena arena) {
    new MessageBuilder("IN_GAME_MESSAGES_ARENA_PORTAL_OUT")
      .asKey()
      .player(player)
      .sendPlayer();
    playerDeath(player, arena);
  }

  private void insideOwnPortal(Player player, Arena arena) {
    cooldownPortal.put(player, System.currentTimeMillis());
    new MessageBuilder("IN_GAME_MESSAGES_ARENA_PORTAL_OWN").asKey().player(player).sendPlayer();
    // prevent players being stuck on portal location
    Bukkit.getScheduler()
      .runTaskLater(
        plugin,
        () -> {
          if(player != null && arena
            .getBase(player)
            .getPortalCuboid()
            .isInWithMarge(player.getLocation(), 1)) {
            playerDeath(player, arena);
            plugin
              .getDebugger()
              .debug(
                Level.INFO,
                "Killed "
                  + player.getName()
                  + " because he is more than 3 seconds on own portal (seems to stuck)");
          }
        },
        20 * 3 /* 3 seconds as cooldown to prevent instant respawning */);
  }

  private void outsideArenaBorder(Player player, Arena arena) {
    if(cooldownOutside.containsKey(player)
      && cooldownOutside.get(player) <= System.currentTimeMillis() - 1500) {
      cooldownOutside.remove(player);
      return;
    }
    playerDeath(player, arena);
    plugin
      .getDebugger()
      .debug(
        Level.INFO,
        "Killed "
          + player.getName()
          + " because he is more than 5 blocks outside arena location, Location: "
          + player.getLocation()
          + "; ArenaBorder: "
          + arena.getArenaBorder().getMinPoint()
          + ";"
          + arena.getArenaBorder().getMaxPoint()
          + ";"
          + arena.getArenaBorder().getCenter());
  }

  @EventHandler //fallback
  public void onDeath(PlayerDeathEvent event) {
    Player player = event.getEntity();
    Arena arena = plugin.getArenaRegistry().getArena(player);
    if(arena == null) {
      return;
    }
    event.setDroppedExp(0);
    event.getDrops().clear();
    playerDeath(player, arena);
  }


  private void playerDeath(Player player, Arena arena) {
    User user = plugin.getUserManager().getUser(player);
    arena.resetPlayer(player);
    switch(arena.getArenaState()) {
      case STARTING:
      case WAITING_FOR_PLAYERS:
      case FULL_GAME:
        Location lobbyLocation = arena.getLobbyLocation();
        VersionUtils.teleport(player, lobbyLocation);
        break;
      case IN_GAME:
        if(!user.isSpectator()) {
          user.adjustStatistic("DEATHS", 1);
          user.adjustStatistic("LOCAL_DEATHS", 1);
          if(arena.inBase(player)) {
            Location respawnPoint = arena.getBase(player).getPlayerRespawnPoint();
            VersionUtils.teleport(player, respawnPoint);
            modeDeathHandle(player, arena, user);
            cooldownOutside.put(player, System.currentTimeMillis());
            plugin
              .getRewardsHandler()
              .performReward(player, plugin.getRewardsHandler().getRewardType("DEATH"));

            rewardLastAttacker(arena, player);
            plugin.getUserManager().getUser(player).getKit().giveKitItems(player);
            player.updateInventory();
          } else {
            Location spectatorLocation = arena.getSpectatorLocation();
            VersionUtils.teleport(player, spectatorLocation);
            player.setAllowFlight(true);
            player.setFlying(true);
            user.setSpectator(true);
            PluginArenaUtils.hidePlayer(player, arena);
            VersionUtils.setCollidable(player, false);
            plugin.getSpecialItemManager().addSpecialItemsOfStage(player, SpecialItem.DisplayStage.SPECTATOR);
          }
        } else {
          Location spectatorLocation = arena.getSpectatorLocation();
          VersionUtils.teleport(player, spectatorLocation);
        }
        break;
      case ENDING:
      case RESTARTING:
        Location location = arena.getSpectatorLocation();
        VersionUtils.teleport(player, location);
        break;
      default:
    }
  }

  private void modeDeathHandle(Player player, Arena arena, User user) {
    if (arena.getMode() == Arena.Mode.HEARTS) {
      // if mode hearts and they are out it should set spec mode for them
      if (arena.getBase(player).getPoints() >= arena.getArenaOption("MODE_VALUE")) {
        user.setSpectator(true);
        ArenaUtils.hidePlayer(player, arena);
        player.getInventory().clear();
        if (arena.getArenaState() != ArenaState.ENDING
          && arena.getArenaState() != ArenaState.RESTARTING) {
          arena.addDeathPlayer(player);
        }
        List<Player> players = arena.getBase(player).getPlayers();
        if (players.stream().allMatch(arena::isDeathPlayer)) {
          arena.addOut();
        }
      }
    }
    ArenaUtils.hidePlayersOutsideTheGame(player, arena);
  }


  @Override
  public boolean additionalFallDamageRules(
    Player victim, PluginArena arena, EntityDamageEvent event) {
    Arena pluginArena = plugin.getArenaRegistry().getArena(arena.getId());
    if(pluginArena == null) {
      return false;
    }
    if(pluginArena.getBase(victim) != null && pluginArena.getBase(victim).isDamageCooldown()) {
      event.setCancelled(true);
      return true;
    }
    return false;
  }

  @Override
  public void handleIngameVoidDeath(Player victim, PluginArena arena) {
    Arena pluginArena = plugin.getArenaRegistry().getArena(arena.getId());
    if(pluginArena == null) {
      return;
    }
    victim.damage(0);
    //victim.teleport(pluginArena.getBase(victim).getPlayerRespawnPoint());
  }

  @EventHandler
  public void onBowShot(EntityShootBowEvent event) {
    if(!(event.getEntity() instanceof Player)) {
      return;
    }
    User user = plugin.getUserManager().getUser((Player) event.getEntity());
    Arena pluginArena = plugin.getArenaRegistry().getArena(user.getPlayer());
    if(pluginArena == null) {
      return;
    }
    if(pluginArena.isResetRound()) {
      event.setCancelled(true);
      return;
    }
    if(user.getCooldown("bow_shot") == 0) {
      int cooldown = plugin.getConfig().getInt("Bow-Cooldown", 5);
      if((user.getKit() instanceof ArcherKit)) {
        cooldown = Math.max(0, Math.min(cooldown, cooldown - 2));
      }
      user.setCooldown("bow_shot", cooldown);
      Player player = (Player) event.getEntity();
      plugin
        .getBukkitHelper()
        .applyActionBarCooldown(player, cooldown);
      if (event.getBow() != null) {
        VersionUtils.setDurability(event.getBow(), (short) 0);
      }
      return;
    }
    event.setCancelled(true);
  }

  @EventHandler
  public void onArrowPickup(PlugilyPlayerPickupArrow event) {
    if(plugin.getArenaRegistry().isInArena(event.getPlayer())) {
      event.getItem().remove();
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onItemPickup(PlugilyEntityPickupItemEvent event) {
    if(!(event.getEntity() instanceof Player player)) {
      return;
    }
    Arena pluginArena = plugin.getArenaRegistry().getArena(player);
    if(pluginArena == null) {
      return;
    }
    event.setCancelled(true);

    // User user = plugin.getUserManager().getUser(player);
    // if(user.isSpectator() || pluginArena.getArenaState() != ArenaState.IN_GAME) {
    //  return;
    // }
  }


  @EventHandler
  public void onArrowDamage(EntityDamageByEntityEvent event) {
    if(!(event.getDamager() instanceof Arrow)) {
      return;
    }
    if(!(((Arrow) event.getDamager()).getShooter() instanceof Player attacker)) {
      return;
    }
    if(!(event.getEntity() instanceof Player victim)) {
      return;
    }
    if(!ArenaUtils.areInSameArena(attacker, victim)) {
      return;
    }
    // we won't allow to suicide
    if(attacker == victim) {
      event.setCancelled(true);
      return;
    }
    Arena arena = plugin.getArenaRegistry().getArena(attacker);
    if(plugin.getUserManager().getUser(attacker).isSpectator()) {
      event.setCancelled(true);
      return;
    }
    assert arena != null;
    if(arena.isTeammate(attacker, victim)) {
      event.setCancelled(true);
      return;
    }
    arena.addHits(victim, attacker);

    XSound.ENTITY_PLAYER_DEATH.play(victim.getLocation(), 50, 1);

    if(victim.getHealth() - event.getDamage() <= 0) {
      return;
    }
    DecimalFormat df = new DecimalFormat("##.##");
    new MessageBuilder("IN_GAME_MESSAGES_ARENA_DAMAGE")
      .asKey()
      .player(victim)
      .value(df.format(victim.getHealth() - event.getDamage()))
      .send(attacker);
  }

  @EventHandler
  public void onItemMove(InventoryClickEvent event) {
    if(event.getWhoClicked() instanceof Player
      && plugin.getArenaRegistry().isInArena((Player) event.getWhoClicked())) {
      if(plugin.getArenaRegistry().getArena(((Player) event.getWhoClicked())).getArenaState()
        != ArenaState.IN_GAME) {
        if(event.getClickedInventory() == event.getWhoClicked().getInventory()) {
          if(event.getView().getType() == InventoryType.CRAFTING
            || event.getView().getType() == InventoryType.PLAYER) {
            event.setResult(Event.Result.DENY);
          }
        }
      }
    }
  }
}
