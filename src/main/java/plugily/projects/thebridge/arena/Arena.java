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

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.plajerlair.commonsbox.minecraft.compat.ServerVersion;
import pl.plajerlair.commonsbox.minecraft.compat.VersionUtils;
import pl.plajerlair.commonsbox.minecraft.configuration.ConfigUtils;
import pl.plajerlair.commonsbox.minecraft.dimensional.Cuboid;
import pl.plajerlair.commonsbox.minecraft.serialization.InventorySerializer;
import plugily.projects.thebridge.ConfigPreferences;
import plugily.projects.thebridge.Main;
import plugily.projects.thebridge.api.StatsStorage;
import plugily.projects.thebridge.api.events.game.TBGameStartEvent;
import plugily.projects.thebridge.api.events.game.TBGameStateChangeEvent;
import plugily.projects.thebridge.arena.base.Base;
import plugily.projects.thebridge.arena.managers.ScoreboardManager;
import plugily.projects.thebridge.arena.options.ArenaOption;
import plugily.projects.thebridge.handlers.ChatManager;
import plugily.projects.thebridge.handlers.rewards.Reward;
import plugily.projects.thebridge.user.User;
import plugily.projects.thebridge.utils.Debugger;
import plugily.projects.thebridge.utils.NMS;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * @author Tigerpanzer_02, 2Wild4You
 * <p>
 * Created at 31.10.2020
 */
public class Arena extends BukkitRunnable {

  private static final Main plugin = JavaPlugin.getPlugin(Main.class);
  private final String id;
  private ArenaState arenaState = ArenaState.WAITING_FOR_PLAYERS;
  private BossBar gameBar;
  private String mapName = "";
  private final ChatManager chatManager = plugin.getChatManager();
  private final ScoreboardManager scoreboardManager;
  private final Set<Player> players = new HashSet<>();
  private final List<Player> spectators = new ArrayList<>(), deaths = new ArrayList<>();
  //all arena values that are integers, contains constant and floating values
  private final Map<ArenaOption, Integer> arenaOptions = new EnumMap<>(ArenaOption.class);
  //instead of 3 location fields we use map with GameLocation enum
  private final Map<GameLocation, Location> gameLocations = new EnumMap<>(GameLocation.class);
  private boolean ready = true, forceStart = false;
  private List<Base> bases = new ArrayList<>();
  private Mode mode;
  private final ArrayList<Block> placedBlocks = new ArrayList<>();
  private final HashMap<Player, Player> hits = new HashMap<>();
  private int resetRound = 0;
  private int out = 0;
  private Cuboid arenaBorder;
  private Base winner;

  public Arena(String id) {
    this.id = id;
    for(ArenaOption option : ArenaOption.values()) {
      arenaOptions.put(option, option.getDefaultValue());
    }
    if(plugin.getConfigPreferences().getOption(ConfigPreferences.Option.BOSSBAR_ENABLED) && ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_9_R1)) {
      gameBar = Bukkit.createBossBar(chatManager.colorMessage("Bossbar.Main-Title"), BarColor.BLUE, BarStyle.SOLID);
    }
    scoreboardManager = new ScoreboardManager(this);
  }

  public boolean isReady() {
    return ready;
  }

  public void setReady(boolean ready) {
    this.ready = ready;
  }

  @Override
  public void run() {
    //idle task
    if(getPlayers().isEmpty() && getArenaState() == ArenaState.WAITING_FOR_PLAYERS) {
      return;
    }
    Debugger.performance("ArenaTask", "[PerformanceMonitor] [{0}] Running game task", getId());
    long start = System.currentTimeMillis();

    switch(getArenaState()) {
      case WAITING_FOR_PLAYERS:
        if(plugin.getConfigPreferences().getOption(ConfigPreferences.Option.BUNGEE_ENABLED)) {
          plugin.getServer().setWhitelist(false);
        }
        if(getPlayers().size() < getMinimumPlayers()) {
          if(getTimer() <= 0) {
            setTimer(45);
            chatManager.broadcast(this, chatManager.formatMessage(this, chatManager.colorMessage("In-Game.Messages.Lobby-Messages.Waiting-For-Players"), getMinimumPlayers()));
            break;
          }
        } else {
          if(plugin.getConfigPreferences().getOption(ConfigPreferences.Option.BOSSBAR_ENABLED)&& ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_9_R1)) {
            gameBar.setTitle(chatManager.colorMessage("Bossbar.Waiting-For-Players"));
          }
          chatManager.broadcast(this, chatManager.colorMessage("In-Game.Messages.Lobby-Messages.Enough-Players-To-Start"));
          setArenaState(ArenaState.STARTING);
          setTimer(plugin.getConfig().getInt("Starting-Waiting-Time", 60));
          this.showPlayers();
        }
        setTimer(getTimer() - 1);
        break;
      case STARTING:
        if(getPlayers().size() == getMaximumPlayers() && getTimer() >= plugin.getConfig().getInt("Start-Time-On-Full-Lobby", 15) && !forceStart) {
          setTimer(plugin.getConfig().getInt("Start-Time-On-Full-Lobby", 15));
          chatManager.broadcast(this, chatManager.colorMessage("In-Game.Messages.Lobby-Messages.Start-In").replace("%TIME%", String.valueOf(getTimer())));
        }
        if(plugin.getConfigPreferences().getOption(ConfigPreferences.Option.BOSSBAR_ENABLED)&& ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_9_R1)) {
          gameBar.setTitle(chatManager.colorMessage("Bossbar.Starting-In").replace("%time%", String.valueOf(getTimer())));
          gameBar.setProgress(getTimer() / plugin.getConfig().getDouble("Starting-Waiting-Time", 60));
        }
        for(Player player : getPlayers()) {
          player.setExp((float) (getTimer() / plugin.getConfig().getDouble("Starting-Waiting-Time", 60)));
          player.setLevel(getTimer());
        }
        if(getPlayers().size() < getMinimumPlayers() && !forceStart) {
          if(plugin.getConfigPreferences().getOption(ConfigPreferences.Option.BOSSBAR_ENABLED)&& ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_9_R1)) {
            gameBar.setTitle(chatManager.colorMessage("Bossbar.Waiting-For-Players"));
            gameBar.setProgress(1.0);
          }
          chatManager.broadcast(this, chatManager.formatMessage(this, chatManager.colorMessage("In-Game.Messages.Lobby-Messages.Waiting-For-Players"), getMinimumPlayers()));
          setArenaState(ArenaState.WAITING_FOR_PLAYERS);
          Bukkit.getPluginManager().callEvent(new TBGameStartEvent(this));
          setTimer(15);
          for(Player player : getPlayers()) {
            player.setExp(1);
            player.setLevel(0);
          }
          if(forceStart) {
            forceStart = false;
          }
          break;
        }
        if(getTimer() == 0 || forceStart) {
          TBGameStartEvent gameStartEvent = new TBGameStartEvent(this);
          Bukkit.getPluginManager().callEvent(gameStartEvent);
          setArenaState(ArenaState.IN_GAME);
          if(plugin.getConfigPreferences().getOption(ConfigPreferences.Option.BOSSBAR_ENABLED)&& ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_9_R1)) {
            gameBar.setProgress(1.0);
          }
          setTimer(5);
          if(players.isEmpty()) {
            break;
          }
          teleportAllToStartLocation();
          for(Player player : getPlayers()) {
            //reset local variables to be 100% sure
            plugin.getUserManager().getUser(player).setStat(StatsStorage.StatisticType.LOCAL_DEATHS, 0);
            plugin.getUserManager().getUser(player).setStat(StatsStorage.StatisticType.LOCAL_KILLS, 0);
            plugin.getUserManager().getUser(player).setStat(StatsStorage.StatisticType.LOCAL_SCORED_POINTS, 0);
            //
            player.getInventory().clear();
            player.setGameMode(GameMode.SURVIVAL);
            ArenaUtils.hidePlayersOutsideTheGame(player, this);
            player.updateInventory();
            plugin.getUserManager().addStat(player, StatsStorage.StatisticType.GAMES_PLAYED);
            setTimer(plugin.getConfig().getInt("Gameplay-Time", 500));
            player.sendMessage(chatManager.getPrefix() + chatManager.colorMessage("In-Game.Messages.Lobby-Messages.Game-Started"));
            // get base with min players
            Base minPlayers = getBases().stream().min(Comparator.comparing(Base::getPlayersSize)).get();
            // add player to min base if he got no base
            if(!inBase(player)) {
              minPlayers.addPlayer(player);
            }
            // fallback
            if(!inBase(player)) {
              getBases().get(0).addPlayer(player);
            }
            plugin.getUserManager().getUser(player).getKit().giveKitItems(player);
            player.updateInventory();
            plugin.getUserManager().addExperience(player, 10);
          }
          //check if not only one base got players
          Base maxPlayers = getBases().stream().max(Comparator.comparing(Base::getPlayersSize)).get();
          Base minPlayers = getBases().stream().min(Comparator.comparing(Base::getPlayersSize)).get();
          if(maxPlayers.getPlayersSize() == getPlayers().size()) {
            for(int i = 0; i < maxPlayers.getPlayersSize() / 2; i++) {
              Player move = maxPlayers.getPlayers().get(i);
              minPlayers.addPlayer(move);
              maxPlayers.removePlayer(move);
            }
          }
          teleportAllToBaseLocation();
          for(Base base : bases) {
            base.removeCageFloor();
          }
          if(plugin.getConfigPreferences().getOption(ConfigPreferences.Option.BOSSBAR_ENABLED)&& ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_9_R1)) {
            gameBar.setTitle(chatManager.colorMessage("Bossbar.In-Game-Info"));
          }
        }
        if(forceStart) {
          forceStart = false;
        }
        setTimer(getTimer() - 1);
        break;
      case IN_GAME:
        if(plugin.getConfigPreferences().getOption(ConfigPreferences.Option.BUNGEE_ENABLED)) {
          plugin.getServer().setWhitelist(getMaximumPlayers() <= getPlayers().size());
        }
        if(getTimer() <= 0) {
          Base highestValue = bases.get(0);
          for(Base base : bases) {
            if(highestValue.getPoints() < base.getPoints()) {
              highestValue = base;
            }
          }
          winner = highestValue;
          ArenaManager.stopGame(false, this);
        }
        if(getTimer() == 30 || getTimer() == 60 || getTimer() == 120) {
          String title = chatManager.colorMessage("In-Game.Messages.Seconds-Left-Title").replace("%time%", String.valueOf(getTimer()));
          String subtitle = chatManager.colorMessage("In-Game.Messages.Seconds-Left-Subtitle").replace("%time%", String.valueOf(getTimer()));
          for(Player p : getPlayers()) {
            VersionUtils.sendTitles(p, title, subtitle, 5, 40, 5);
          }
        }

        if(resetRound > 0) {
          String title = chatManager.colorMessage("In-Game.Messages.Blocked.Title").replace("%seconds%", String.valueOf(resetRound));
          String subtitle = chatManager.colorMessage("In-Game.Messages.Blocked.Subtitle").replace("%seconds%", String.valueOf(resetRound));
          for(Player p : getPlayers()) {
            VersionUtils.sendTitles(p, title, subtitle, 5, 40, 5);
            if(resetRound == 1) {
              p.sendMessage(chatManager.colorMessage("In-Game.Messages.Blocked.Run"));
            }
          }
          if(resetRound == 1) {
            for(Base base : bases) {
              base.removeCageFloor();
            }
          }
          resetRound--;
        }

        //no players - stop game
        if(getPlayersLeft().size() == 0) {
          ArenaManager.stopGame(false, this);
        } else {
          //winner check
          for(Base base : getBases()) {
            if(base.getPoints() >= getOption(ArenaOption.MODE_VALUE)) {
              winner = base;
              if(mode == Mode.POINTS) {
                for(Player p : getPlayers()) {
                  VersionUtils.sendTitles(p, chatManager.colorMessage("In-Game.Messages.Game-End-Messages.Titles.Lose"),
                      chatManager.colorMessage("In-Game.Messages.Game-End-Messages.Subtitles.Reached").replace("%base%", base.getFormattedColor()), 5, 40, 5);
                  if(base.getPlayers().contains(p)) {
                    VersionUtils.sendTitles(p, chatManager.colorMessage("In-Game.Messages.Game-End-Messages.Titles.Win"), "", 5, 40, 5);
                  }
                }
                ArenaManager.stopGame(false, this);
                break;
              }
            }
          }
          if(mode == Mode.HEARTS) {
            if(out >= bases.size() - 1) {
              for(Player player : getPlayers()) {
                if(!isDeathPlayer(player)) {
                  VersionUtils.sendTitles(player, chatManager.colorMessage("In-Game.Messages.Game-End-Messages.Titles.Win"), "", 5, 40, 5);
                  winner = getBase(player);
                } else {
                  VersionUtils.sendTitles(player, chatManager.colorMessage("In-Game.Messages.Game-End-Messages.Titles.Lose"), "", 5, 40, 5);
                }
              }
              ArenaManager.stopGame(false, this);
            }
          }
        }
        setTimer(getTimer() - 1);
        break;
      case ENDING:
        scoreboardManager.stopAllScoreboards();
        if(plugin.getConfigPreferences().getOption(ConfigPreferences.Option.BUNGEE_ENABLED)) {
          plugin.getServer().setWhitelist(false);
        }
        if(getTimer() <= 0) {
          if(plugin.getConfigPreferences().getOption(ConfigPreferences.Option.BOSSBAR_ENABLED)&& ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_9_R1)) {
            gameBar.setTitle(chatManager.colorMessage("Bossbar.Game-Ended"));
          }

          List<Player> playersToQuit = new ArrayList<>(getPlayers());
          for(Player player : playersToQuit) {
            plugin.getUserManager().getUser(player).removeScoreboard();
            player.setGameMode(GameMode.SURVIVAL);
            for(Player players : Bukkit.getOnlinePlayers()) {
              NMS.showPlayer(player, players);
              if(!ArenaRegistry.isInArena(players)) {
                NMS.showPlayer(players, player);
              }
            }
            player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
            player.setWalkSpeed(0.2f);
            player.setFlying(false);
            player.setAllowFlight(false);
            player.getInventory().clear();

            player.getInventory().setArmorContents(null);
            doBarAction(BarAction.REMOVE, player);
            player.setFireTicks(0);
            player.setFoodLevel(20);
          }
          teleportAllToEndLocation();

          if(plugin.getConfigPreferences().getOption(ConfigPreferences.Option.INVENTORY_MANAGER_ENABLED)) {
            for(Player player : getPlayers()) {
              InventorySerializer.loadInventory(plugin, player);
            }
          }

          for(User user : plugin.getUserManager().getUsers(this)) {
            user.setSpectator(false);
            VersionUtils.setCollidable(user.getPlayer(), true);
            user.getPlayer().sendMessage(chatManager.getPrefix() + chatManager.colorMessage("Commands.Teleported-To-The-Lobby", user.getPlayer()));
            plugin.getUserManager().saveAllStatistic(user);
          }
          plugin.getRewardsHandler().performReward(this, Reward.RewardType.END_GAME);
          players.clear();

          deaths.clear();
          spectators.clear();

          cleanUpArena();
          if(plugin.getConfigPreferences().getOption(ConfigPreferences.Option.BUNGEE_ENABLED)
            && ConfigUtils.getConfig(plugin, "bungee").getBoolean("Shutdown-When-Game-Ends")) {
            plugin.getServer().shutdown();
          }
          setArenaState(ArenaState.RESTARTING);
        }
        setTimer(getTimer() - 1);
        break;
      case RESTARTING:
        getPlayers().clear();
        for(Base base : getBases()) {
          base.reset();
        }
        setArenaState(ArenaState.WAITING_FOR_PLAYERS);
        if(plugin.getConfigPreferences().getOption(ConfigPreferences.Option.BUNGEE_ENABLED)) {
          ArenaRegistry.shuffleBungeeArena();
          for(Player player : Bukkit.getOnlinePlayers()) {
            ArenaManager.joinAttempt(player, ArenaRegistry.getArenas().get(ArenaRegistry.getBungeeArena()));
          }
        }
        if(plugin.getConfigPreferences().getOption(ConfigPreferences.Option.BOSSBAR_ENABLED)&& ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_9_R1)) {
          gameBar.setTitle(chatManager.colorMessage("Bossbar.Waiting-For-Players"));
        }
        break;
      default:
        break;
    }
  }


  /**
   * Get arena identifier used to get arenas by string.
   *
   * @return arena name
   * @see ArenaRegistry#getArena(String)
   */
  public String getId() {
    return id;
  }

  /**
   * Get minimum players needed.
   *
   * @return minimum players needed to start arena
   */
  public int getMinimumPlayers() {
    return getOption(ArenaOption.MINIMUM_PLAYERS);
  }

  /**
   * Set minimum players needed.
   *
   * @param minimumPlayers players needed to start arena
   */
  public void setMinimumPlayers(int minimumPlayers) {
    if(minimumPlayers < 2) {
      Debugger.debug(Level.WARNING, "Minimum players amount for arena cannot be less than 2! Got {0}", minimumPlayers);
      setOptionValue(ArenaOption.MINIMUM_PLAYERS, 2);
      return;
    }
    setOptionValue(ArenaOption.MINIMUM_PLAYERS, minimumPlayers);
  }

  /**
   * Get arena map name.
   *
   * @return arena map name, it's not arena id
   * @see #getId()
   */
  @NotNull
  public String getMapName() {
    return mapName;
  }

  /**
   * Set arena map name.
   *
   * @param mapname new map name, it's not arena id
   */
  public void setMapName(@NotNull String mapname) {
    this.mapName = mapname;
  }

  /**
   * Get timer of arena.
   *
   * @return timer of lobby time / time to next wave
   */
  public int getTimer() {
    return getOption(ArenaOption.TIMER);
  }

  /**
   * Modify game timer.
   *
   * @param timer timer of lobby / time to next wave
   */
  public void setTimer(int timer) {
    setOptionValue(ArenaOption.TIMER, timer);
  }

  /**
   * Return maximum players arena can handle.
   *
   * @return maximum players arena can handle
   */
  public int getMaximumPlayers() {
    return getOption(ArenaOption.MAXIMUM_PLAYERS);
  }

  /**
   * Set maximum players arena can handle.
   *
   * @param maximumPlayers how many players arena can handle
   */
  public void setMaximumPlayers(int maximumPlayers) {
    setOptionValue(ArenaOption.MAXIMUM_PLAYERS, maximumPlayers);
  }

  public int getOut() {
    return out;
  }

  public void setOut(int out) {
    this.out = out;
  }

  public void addOut() {
    this.out++;
  }

  public List<Base> getBases() {
    return bases;
  }

  public boolean inBase(Player player) {
    return getBases().stream().anyMatch(base -> base.getPlayers().contains(player));
  }

  public List<Player> getTeammates(Player player) {
    List<Player> mates = new ArrayList<>(getBase(player).getPlayers());
    mates.remove(player);
    return mates;
  }

  public boolean isTeammate(Player player, Player check) {
    return getTeammates(player).contains(check);
  }

  /**
   * Returns base where the player is
   *
   * @param p target player
   * @return Base or null if not inside an base
   * @see #inBase(Player) to check if player is playing
   */
  public Base getBase(Player p) {
    if(p == null || !p.isOnline()) {
      return null;
    }
    for(Base base : bases) {
      for(Player player : base.getPlayers()) {
        if(player == p) {
          return base;
        }
      }
    }
    return null;
  }

  public Base getWinner() {
    return winner;
  }

  public void setBases(List<Base> bases) {
    this.bases = bases;
  }

  public void addBase(Base base) {
    this.bases.add(base);
  }

  public void removeBase(Base base) {
    this.bases.remove(base);
  }

  /**
   * Return game state of arena.
   *
   * @return game state of arena
   * @see ArenaState
   */
  @NotNull
  public ArenaState getArenaState() {
    return arenaState;
  }

  /**
   * Set game state of arena.
   *
   * @param arenaState new game state of arena
   * @see ArenaState
   */
  public void setArenaState(@NotNull ArenaState arenaState) {
    this.arenaState = arenaState;

    TBGameStateChangeEvent gameStateChangeEvent = new TBGameStateChangeEvent(this, getArenaState());
    Bukkit.getPluginManager().callEvent(gameStateChangeEvent);

    plugin.getSignManager().updateSigns();
  }

  /**
   * Get all players in arena.
   *
   * @return set of players in arena
   */
  @NotNull
  public Set<Player> getPlayers() {
    return players;
  }

  public void teleportToLobby(Player player) {
    player.setFoodLevel(20);
    player.setFlying(false);
    player.setAllowFlight(false);
    player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
    player.setWalkSpeed(0.2f);
    Location location = getLobbyLocation();
    if(location == null) {
      System.out.print("LobbyLocation isn't initialized for arena " + getId());
      return;
    }
    player.teleport(location);
  }

  public ScoreboardManager getScoreboardManager() {
    return scoreboardManager;
  }

  public void addDeathPlayer(Player player) {
    deaths.add(player);
  }

  public void removeDeathPlayer(Player player) {
    deaths.remove(player);
  }

  public boolean isDeathPlayer(Player player) {
    return deaths.contains(player);
  }

  public void addSpectatorPlayer(Player player) {
    spectators.add(player);
  }

  public void removeSpectatorPlayer(Player player) {
    spectators.remove(player);
  }

  public boolean isSpectatorPlayer(Player player) {
    return spectators.contains(player);
  }

  /**
   * Executes boss bar action for arena
   *
   * @param action add or remove a player from boss bar
   * @param p      player
   */
  public void doBarAction(BarAction action, Player p) {
    if(!ServerVersion.Version.isCurrentEqualOrHigher(ServerVersion.Version.v1_9_R1)) {
      return;
    }
    if(!plugin.getConfigPreferences().getOption(ConfigPreferences.Option.BOSSBAR_ENABLED)) {
      return;
    }
    switch(action) {
      case ADD:
        gameBar.addPlayer(p);
        break;
      case REMOVE:
        gameBar.removePlayer(p);
        break;
      default:
        break;
    }
  }

  public void cleanUpArena() {
    getBases().forEach(Base::reset);
    resetPlacedBlocks();
    resetHits();
    round = 0;
    resetRound = 0;
    winner = null;
  }

  int round = 0;

  public void resetRound() {
    for(Base base : bases) {
      base.addCageFloor();
    }
    resetRound = arenaOptions.get(ArenaOption.RESET_TIME);
    round++;
    if(arenaOptions.get(ArenaOption.RESET_BLOCKS) != 0 && getOption(ArenaOption.RESET_BLOCKS) - getRound() == 0) {
      resetPlacedBlocks();
      round = 0;
    }
    resetHits();
    for(Player player : getPlayersLeft()) {
      player.teleport(getBase(player).getPlayerSpawnPoint());
      player.sendMessage(chatManager.colorMessage("In-Game.Messages.Blocked.Reset"));
      player.setHealth(VersionUtils.getHealth(player));
      player.getInventory().clear();
      plugin.getUserManager().getUser(player).getKit().giveKitItems(player);
      player.updateInventory();
      plugin.getUserManager().addExperience(player, 2);
    }
    plugin.getRewardsHandler().performReward(this, Reward.RewardType.RESET_ROUND);
  }

  public int getRound() {
    return round;
  }

  public boolean isResetRound() {
    return resetRound > 0;
  }

  public HashMap<Player, Player> getHits() {
    return hits;
  }

  public void addHits(Player victim, Player attacker) {
    this.hits.remove(victim);
    this.hits.put(victim, attacker);
  }

  public void removeHits(Player victim) {
    this.hits.remove(victim);
  }

  public void resetHits() {
    hits.clear();
  }

  public ArrayList<Block> getPlacedBlocks() {
    return placedBlocks;
  }

  public void addPlacedBlock(Block placedBlock) {
    this.placedBlocks.add(placedBlock);
  }

  public void removePlacedBlock(Block removedblock) {
    this.placedBlocks.remove(removedblock);
  }

  public void resetPlacedBlocks() {
    for(Block block : placedBlocks) {
      block.setType(Material.AIR);
    }
    placedBlocks.clear();
  }

  /**
   * Get lobby location of arena.
   *
   * @return lobby location of arena
   */
  @Nullable
  public Location getLobbyLocation() {
    return gameLocations.get(GameLocation.LOBBY);
  }

  /**
   * Set lobby location of arena.
   *
   * @param loc new lobby location of arena
   */
  public void setLobbyLocation(Location loc) {
    gameLocations.put(GameLocation.LOBBY, loc);
  }

  public void teleportToStartLocation(Player player) {
    player.teleport(getMidLocation());
  }

  public void teleportToBaseLocation(Player player) {
    player.teleport(getBase(player).getPlayerSpawnPoint());
  }

  private void teleportAllToStartLocation() {
    for(Player player : players) {
      player.teleport(getMidLocation());
    }
  }

  private void teleportAllToBaseLocation() {
    for(Player player : players) {
      player.teleport(getBase(player).getPlayerSpawnPoint());
    }
  }

  public void setForceStart(boolean forceStart) {
    this.forceStart = forceStart;
  }


  public void teleportAllToEndLocation() {
    if(plugin.getConfigPreferences().getOption(ConfigPreferences.Option.BUNGEE_ENABLED)
      && ConfigUtils.getConfig(plugin, "bungee").getBoolean("End-Location-Hub", true)) {
      getPlayers().forEach(plugin.getBungeeManager()::connectToHub);
      return;
    }

    Location location = getEndLocation();
    if(location == null) {
      location = getLobbyLocation();
      System.out.print("EndLocation for arena " + getId() + " isn't intialized!");
    }

    if(location != null) {
      for(Player player : getPlayers()) {
        player.teleport(location);
      }
    }
  }

  public void teleportToEndLocation(Player player) {
    if(plugin.getConfigPreferences().getOption(ConfigPreferences.Option.BUNGEE_ENABLED)
      && ConfigUtils.getConfig(plugin, "bungee").getBoolean("End-Location-Hub", true)) {
      plugin.getBungeeManager().connectToHub(player);
      return;
    }

    Location location = getEndLocation();
    if(location == null) {
      System.out.print("EndLocation for arena " + getId() + " isn't intialized!");
      location = getLobbyLocation();
    }

    if(location != null) {
      player.teleport(location);
    }
  }

  public void start() {
    Debugger.debug("[{0}] Game instance started", getId());
    this.runTaskTimer(plugin, 20L, 20L);
    this.setArenaState(ArenaState.RESTARTING);
  }

  void addPlayer(Player player) {
    players.add(player);
  }

  void removePlayer(Player player) {
    if(player != null) {
      players.remove(player);
    }
  }

  public List<Player> getPlayersLeft() {
    return plugin.getUserManager().getUsers(this).stream().filter(user -> !user.isSpectator()).map(User::getPlayer).collect(Collectors.toList());
  }

  void showPlayers() {
    for(Player player : getPlayers()) {
      for(Player p : getPlayers()) {
        NMS.showPlayer(player, p);
        NMS.showPlayer(p, player);
      }
    }
  }

  /**
   * Get end location of arena.
   *
   * @return end location of arena
   */
  @Nullable
  public Location getEndLocation() {
    return gameLocations.get(GameLocation.END);
  }

  /**
   * Set end location of arena.
   *
   * @param endLoc new end location of arena
   */
  public void setEndLocation(Location endLoc) {
    gameLocations.put(GameLocation.END, endLoc);
  }

  /**
   * Get mid location of arena.
   *
   * @return mid location of arena
   */
  @Nullable
  public Location getMidLocation() {
    return gameLocations.get(GameLocation.MID);
  }

  /**
   * Set mid location of arena.
   *
   * @param midLoc new end location of arena
   */
  public void setMidLocation(Location midLoc) {
    gameLocations.put(GameLocation.MID, midLoc);
  }

  /**
   * Get spectator location of arena.
   *
   * @return end location of arena
   */
  @Nullable
  public Location getSpectatorLocation() {
    return gameLocations.get(GameLocation.SPECTATOR);
  }

  /**
   * Set spectator location of arena.
   *
   * @param spectatorLoc new end location of arena
   */
  public void setSpectatorLocation(Location spectatorLoc) {
    gameLocations.put(GameLocation.SPECTATOR, spectatorLoc);
  }


  public Mode getMode() {
    return mode;
  }

  public void setMode(Mode mode) {
    this.mode = mode;
  }

  public void setArenaBorder(Cuboid arenaBorder) {
    this.arenaBorder = arenaBorder;
  }

  public Cuboid getArenaBorder() {
    return arenaBorder;
  }

  public int getOption(@NotNull ArenaOption option) {
    return arenaOptions.get(option);
  }

  public void setOptionValue(ArenaOption option, int value) {
    arenaOptions.put(option, value);
  }

  public void addOptionValue(ArenaOption option, int value) {
    arenaOptions.put(option, arenaOptions.get(option) + value);
  }

  public enum BarAction {
    ADD, REMOVE
  }

  public enum GameLocation {
    LOBBY, END, SPECTATOR, MID
  }

  public enum Mode {
    HEARTS, POINTS
  }
}
