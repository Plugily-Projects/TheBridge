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

package plugily.projects.thebridge.arena;

import me.clip.placeholderapi.PlaceholderAPI;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import pl.plajerlair.commonsbox.minecraft.misc.MiscUtils;
import pl.plajerlair.commonsbox.minecraft.serialization.InventorySerializer;
import plugily.projects.thebridge.ConfigPreferences;
import plugily.projects.thebridge.Main;
import plugily.projects.thebridge.api.StatsStorage;
import plugily.projects.thebridge.api.events.game.TBGameJoinAttemptEvent;
import plugily.projects.thebridge.api.events.game.TBGameLeaveAttemptEvent;
import plugily.projects.thebridge.api.events.game.TBGameStopEvent;
import plugily.projects.thebridge.handlers.ChatManager;
import plugily.projects.thebridge.handlers.PermissionsManager;
import plugily.projects.thebridge.handlers.items.SpecialItem;
import plugily.projects.thebridge.handlers.language.LanguageManager;
import plugily.projects.thebridge.handlers.party.GameParty;
import plugily.projects.thebridge.handlers.rewards.Reward;
import plugily.projects.thebridge.kits.KitRegistry;
import plugily.projects.thebridge.user.User;
import plugily.projects.thebridge.utils.Debugger;
import plugily.projects.thebridge.utils.NMS;

import java.util.List;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 23.11.2020
 */
public class ArenaManager {

  private static final Main plugin = JavaPlugin.getPlugin(Main.class);
  private static final ChatManager chatManager = plugin.getChatManager();

  private ArenaManager() {
  }

  /**
   * Attempts player to join arena.
   * Calls MMGameJoinAttemptEvent.
   * Can be cancelled only via above-mentioned event
   *
   * @param player player to join
   * @see TBGameJoinAttemptEvent
   */
  public static void joinAttempt(Player player, Arena arena) {
    Debugger.debug("[{0}] Initial join attempt for {1}", arena.getId(), player.getName());
    long start = System.currentTimeMillis();
    TBGameJoinAttemptEvent gameJoinAttemptEvent = new TBGameJoinAttemptEvent(player, arena);
    Bukkit.getPluginManager().callEvent(gameJoinAttemptEvent);

    if(!arena.isReady()) {
      player.sendMessage(chatManager.getPrefix() + chatManager.colorMessage("In-Game.Arena-Not-Configured"));
      return;
    }
    if(gameJoinAttemptEvent.isCancelled()) {
      player.sendMessage(chatManager.getPrefix() + chatManager.colorMessage("In-Game.Join-Cancelled-Via-API"));
      return;
    }
    if(ArenaRegistry.isInArena(player)) {
      player.sendMessage(chatManager.getPrefix() + chatManager.colorMessage("In-Game.Already-Playing"));
      return;
    }

    //check if player is in party and send party members to the game
    if(plugin.getPartyHandler().isPlayerInParty(player)) {
      GameParty party = plugin.getPartyHandler().getParty(player);
      if(party.getLeader().equals(player)) {
        if(arena.getMaximumPlayers() - arena.getPlayers().size() >= party.getPlayers().size()) {
          for(Player partyPlayer : party.getPlayers()) {
            if(partyPlayer == player) {
              continue;
            }
            if(ArenaRegistry.isInArena(partyPlayer)) {
              if(ArenaRegistry.getArena(partyPlayer).getArenaState() == ArenaState.IN_GAME) {
                continue;
              }
              leaveAttempt(partyPlayer, ArenaRegistry.getArena(partyPlayer));
            }
            partyPlayer.sendMessage(chatManager.getPrefix() + chatManager.formatMessage(arena, chatManager.colorMessage("In-Game.Join-As-Party-Member"), partyPlayer));
            joinAttempt(partyPlayer, arena);
          }
        } else {
          player.sendMessage(chatManager.getPrefix() + chatManager.formatMessage(arena, chatManager.colorMessage("In-Game.Messages.Lobby-Messages.Not-Enough-Space-For-Party"), player));
          return;
        }
      }
    }

    if(!plugin.getConfigPreferences().getOption(ConfigPreferences.Option.BUNGEE_ENABLED)
      && !player.hasPermission(PermissionsManager.getJoinPerm().replace("<arena>", "*"))
      || !player.hasPermission(PermissionsManager.getJoinPerm().replace("<arena>", arena.getId()))) {
      player.sendMessage(chatManager.getPrefix() + chatManager.colorMessage("In-Game.Join-No-Permission").replace("%permission%",
        PermissionsManager.getJoinPerm().replace("<arena>", arena.getId())));
      return;
    }
    if(arena.getArenaState() == ArenaState.RESTARTING) {
      return;
    }
    if(arena.getPlayers().size() >= arena.getMaximumPlayers() && arena.getArenaState() == ArenaState.STARTING) {
      if(!player.hasPermission(PermissionsManager.getJoinFullGames())) {
        player.sendMessage(chatManager.getPrefix() + chatManager.colorMessage("In-Game.Full-Game-No-Permission"));
        return;
      }
      boolean foundSlot = false;
      for(Player loopPlayer : arena.getPlayers()) {
        if(loopPlayer.hasPermission(PermissionsManager.getJoinFullGames())) {
          continue;
        }
        leaveAttempt(loopPlayer, arena);
        loopPlayer.sendMessage(chatManager.getPrefix() + chatManager.colorMessage("In-Game.Messages.Lobby-Messages.You-Were-Kicked-For-Premium-Slot"));
        chatManager.broadcast(arena, chatManager.formatMessage(arena, chatManager.colorMessage("In-Game.Messages.Lobby-Messages.Kicked-For-Premium-Slot"), loopPlayer));
        foundSlot = true;
        break;
      }
      if(!foundSlot) {
        player.sendMessage(chatManager.getPrefix() + chatManager.colorMessage("In-Game.No-Slots-For-Premium"));
        return;
      }
    }
    Debugger.debug("[{0}] Checked join attempt for {1} initialized", arena.getId(), player.getName());
    User user = plugin.getUserManager().getUser(player);
    arena.getScoreboardManager().createScoreboard(user);
    if(plugin.getConfigPreferences().getOption(ConfigPreferences.Option.INVENTORY_MANAGER_ENABLED)) {
      InventorySerializer.saveInventoryToFile(plugin, player);
    }

    arena.addPlayer(player);
    player.setLevel(0);
    player.setExp(1);
    player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
    player.setFoodLevel(20);
    if((arena.getArenaState() == ArenaState.IN_GAME || arena.getArenaState() == ArenaState.ENDING)) {
      arena.teleportToStartLocation(player);
      player.sendMessage(chatManager.colorMessage("In-Game.You-Are-Spectator"));
      player.getInventory().clear();

      for(SpecialItem item : plugin.getSpecialItemManager().getSpecialItems()) {
        if(item.getDisplayStage() != SpecialItem.DisplayStage.SPECTATOR) {
          continue;
        }
        player.getInventory().setItem(item.getSlot(), item.getItemStack());
      }

      player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
      player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0));
      ArenaUtils.hidePlayer(player, arena);

      user.setSpectator(true);
      arena.addSpectatorPlayer(player);
      player.setCollidable(false);
      player.setGameMode(GameMode.SURVIVAL);
      player.setAllowFlight(true);
      player.setFlying(true);

      for(Player spectator : arena.getPlayers()) {
        if(plugin.getUserManager().getUser(spectator).isSpectator()) {
          NMS.hidePlayer(player, spectator);
        } else {
          NMS.showPlayer(player, spectator);
        }
      }
      ArenaUtils.hidePlayersOutsideTheGame(player, arena);
      Debugger.debug("[{0}] Join attempt as spectator finished for {1} took {2}ms", arena.getId(), player.getName(), System.currentTimeMillis() - start);
      return;
    }
    arena.teleportToLobby(player);
    player.getInventory().setArmorContents(new ItemStack[]{new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR)});
    player.setFlying(false);
    player.setAllowFlight(false);
    player.getInventory().clear();
    arena.doBarAction(Arena.BarAction.ADD, player);
    if(!plugin.getUserManager().getUser(player).isSpectator()) {
      chatManager.broadcastAction(arena, player, ChatManager.ActionType.JOIN);
    }
    if(arena.getArenaState() == ArenaState.STARTING || arena.getArenaState() == ArenaState.WAITING_FOR_PLAYERS) {
      user.setKit(KitRegistry.getDefaultKit());
      for(SpecialItem item : plugin.getSpecialItemManager().getSpecialItems()) {
        if(item.getDisplayStage() != SpecialItem.DisplayStage.LOBBY) {
          continue;
        }
        player.getInventory().setItem(item.getSlot(), item.getItemStack());
      }
    }
    player.updateInventory();
    for(Player arenaPlayer : arena.getPlayers()) {
      ArenaUtils.showPlayer(arenaPlayer, arena);
    }
    arena.showPlayers();
    plugin.getSignManager().updateSigns();
    Debugger.debug("[{0}] Join attempt as player for {1} took {2}ms", arena.getId(), player.getName(), System.currentTimeMillis() - start);
  }

  /**
   * Attempts player to leave arena.
   * Calls MMGameLeaveAttemptEvent event.
   *
   * @param player player to join
   * @see TBGameLeaveAttemptEvent
   */
  public static void leaveAttempt(Player player, Arena arena) {
    Debugger.debug("[{0}] Initial leave attempt for {1}", arena.getId(), player.getName());
    long start = System.currentTimeMillis();

    TBGameLeaveAttemptEvent event = new TBGameLeaveAttemptEvent(player, arena);
    Bukkit.getPluginManager().callEvent(event);
    User user = plugin.getUserManager().getUser(player);

    arena.getScoreboardManager().removeScoreboard(user);
    //-1 cause we didn't remove player yet
    if(arena.getArenaState() == ArenaState.IN_GAME && !user.isSpectator()) {
      if(arena.getPlayersLeft().size() - 1 > 1) {
        //plugin.getCorpseHandler().spawnCorpse(player, arena);
      } else {
        stopGame(true, arena);
      }
    }
    //the default fly speed
    player.setFlySpeed(0.1f);
    player.getInventory().clear();
    player.getInventory().setArmorContents(null);
    arena.removePlayer(player);
    arena.teleportToEndLocation(player);
    if(!user.isSpectator()) {
      chatManager.broadcastAction(arena, player, ChatManager.ActionType.LEAVE);
    }
    player.setGlowing(false);
    user.setSpectator(false);
    if(arena.isDeathPlayer(player)) {
      arena.removeDeathPlayer(player);
    }
    if(arena.isSpectatorPlayer(player)) {
      arena.removeSpectatorPlayer(player);
    }
    player.setCollidable(true);
    user.removeScoreboard();
    arena.doBarAction(Arena.BarAction.REMOVE, player);
    player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
    player.setFoodLevel(20);
    player.setLevel(0);
    player.setExp(0);
    player.setFlying(false);
    player.setAllowFlight(false);
    player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
    player.setWalkSpeed(0.2f);
    player.setFireTicks(0);
    if(arena.getArenaState() != ArenaState.WAITING_FOR_PLAYERS && arena.getArenaState() != ArenaState.STARTING && arena.getPlayers().size() == 0) {
      arena.setArenaState(ArenaState.ENDING);
      arena.setTimer(0);
    }

    player.setGameMode(GameMode.SURVIVAL);
    for(Player players : plugin.getServer().getOnlinePlayers()) {
      if(!ArenaRegistry.isInArena(players)) {
        NMS.showPlayer(players, player);
      }
      NMS.showPlayer(player, players);
    }
    arena.teleportToEndLocation(player);
    if(!plugin.getConfigPreferences().getOption(ConfigPreferences.Option.BUNGEE_ENABLED)
      && plugin.getConfigPreferences().getOption(ConfigPreferences.Option.INVENTORY_MANAGER_ENABLED)) {
      InventorySerializer.loadInventory(plugin, player);
    }
    plugin.getUserManager().saveAllStatistic(user);
    plugin.getSignManager().updateSigns();
    Debugger.debug("[{0}] Game leave finished for {1} took{2}ms ", arena.getId(), player.getName(), System.currentTimeMillis() - start);
  }

  /**
   * Stops current arena. Calls MMGameStopEvent event
   *
   * @param quickStop should arena be stopped immediately? (use only in important cases)
   * @see TBGameStopEvent
   */
  public static void stopGame(boolean quickStop, Arena arena) {
    Debugger.debug("[{0}] Stop game event initialized with quickStop {1}", arena.getId(), quickStop);
    long start = System.currentTimeMillis();

    TBGameStopEvent gameStopEvent = new TBGameStopEvent(arena);
    Bukkit.getPluginManager().callEvent(gameStopEvent);
    arena.setArenaState(ArenaState.ENDING);
    if(quickStop) {
      arena.setTimer(2);
      chatManager.broadcast(arena, chatManager.colorRawMessage("&cThe game has been force stopped by command"));
    } else {
      arena.setTimer(10);
    }

    List<String> summaryMessages = LanguageManager.getLanguageList("In-Game.Messages.Game-End-Messages.Summary-Message");
    arena.getScoreboardManager().stopAllScoreboards();

    for(final Player player : arena.getPlayers()) {
      User user = plugin.getUserManager().getUser(player);
      if(!quickStop) {
        switch(arena.getMode()) {
          case HEARTS:
            if(arena.isDeathPlayer(player)) {
              plugin.getUserManager().addStat(player, StatsStorage.StatisticType.LOSES);
              plugin.getRewardsHandler().performReward(player, Reward.RewardType.LOSE);
            } else {
              plugin.getUserManager().addStat(player, StatsStorage.StatisticType.WINS);
              plugin.getRewardsHandler().performReward(player, Reward.RewardType.WON);
              plugin.getUserManager().addExperience(player, 5);
            }
            break;
          case POINTS:
            if(arena.getWinner().getPlayers().contains(player)) {
              plugin.getUserManager().addStat(player, StatsStorage.StatisticType.LOSES);
              plugin.getRewardsHandler().performReward(player, Reward.RewardType.LOSE);
            } else {
              plugin.getUserManager().addStat(player, StatsStorage.StatisticType.WINS);
              plugin.getUserManager().addExperience(player, 5);
              plugin.getRewardsHandler().performReward(player, Reward.RewardType.WON);
            }
            break;
          default:
            break;
        }
      }
      //the default walk & fly speed
      player.setFlySpeed(0.1f);
      player.setWalkSpeed(0.2f);

      player.getInventory().clear();
      user.setKit(KitRegistry.getDefaultKit());
      for(SpecialItem item : plugin.getSpecialItemManager().getSpecialItems()) {
        if(item.getDisplayStage() != SpecialItem.DisplayStage.SPECTATOR) {
          continue;
        }
        player.getInventory().setItem(item.getSlot(), item.getItemStack());
      }
      if(!quickStop) {
        for(String msg : summaryMessages) {
          MiscUtils.sendCenteredMessage(player, formatSummaryPlaceholders(msg, arena, player));
        }
      }
      user.removeScoreboard();
      if(!quickStop && plugin.getConfig().getBoolean("Firework-When-Game-Ends", true)) {
        new BukkitRunnable() {
          int i = 0;

          @Override
          public void run() {
            if(i == 4 || !arena.getPlayers().contains(player)) {
              this.cancel();
            }
            MiscUtils.spawnRandomFirework(player.getLocation());
            i++;
          }
        }.runTaskTimer(plugin, 30, 30);
      }
    }
    Debugger.debug("[{0}] Stop game event finished took{1}ms ", arena.getId(), System.currentTimeMillis() - start);
  }

  private static String formatSummaryPlaceholders(String msg, Arena arena, Player player) {
    String formatted = msg;

    switch(arena.getMode()) {
      case POINTS:
        formatted = StringUtils.replace(formatted, "%summary%", LanguageManager.getLanguageMessage("In-Game.Messages.Game-End-Messages.Summary-Base-Points-Win"));
        break;
      case HEARTS:
        formatted = StringUtils.replace(formatted, "%summary%", LanguageManager.getLanguageMessage("In-Game.Messages.Game-End-Messages.Summary-Base-Hearts-Win"));
        break;
      default:
        break;
    }
    formatted = StringUtils.replace(formatted, "%base%", arena.getWinner().getFormattedColor());

    if(plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
      formatted = PlaceholderAPI.setPlaceholders(player, formatted);
    }

    return formatted;
  }

}
