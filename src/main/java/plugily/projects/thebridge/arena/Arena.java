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
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;
import plugily.projects.minigamesbox.classic.arena.ArenaState;
import plugily.projects.minigamesbox.classic.arena.PluginArena;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.utils.dimensional.Cuboid;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;
import plugily.projects.thebridge.Main;
import plugily.projects.thebridge.api.events.game.TBRoundResetEvent;
import plugily.projects.thebridge.arena.base.Base;
import plugily.projects.thebridge.arena.managers.MapRestorerManager;
import plugily.projects.thebridge.arena.managers.ScoreboardManager;
import plugily.projects.thebridge.arena.states.InGameState;
import plugily.projects.thebridge.arena.states.RestartingState;
import plugily.projects.thebridge.arena.states.StartingState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 17.12.2021
 */
public class Arena extends PluginArena {

  private static Main plugin;

  private final Map<SpawnPoint, List<Location>> spawnPoints = new EnumMap<>(SpawnPoint.class);
  private final List<Player> spectators = new ArrayList<>();
  private final List<Player> deaths = new ArrayList<>();
  private List<Base> bases = new ArrayList<>();
  private Mode mode;
  private final ArrayList<Block> placedBlocks = new ArrayList<>();
  private final HashMap<Player, Player> hits = new HashMap<>();
  private int resetRound = 0;
  private int out = 0;
  int round = 0;
  private Cuboid arenaBorder;
  private Base winner;
  private MapRestorerManager mapRestorerManager;

  public Arena(String id) {
    super(id);
    setPluginValues();
    setScoreboardManager(new ScoreboardManager(this));
    mapRestorerManager = new MapRestorerManager(this);
    setMapRestorerManager(mapRestorerManager);
    addGameStateHandler(ArenaState.IN_GAME, new InGameState());
    addGameStateHandler(ArenaState.RESTARTING, new RestartingState());
    addGameStateHandler(ArenaState.STARTING, new StartingState());
  }

  public static void init(Main plugin) {
    Arena.plugin = plugin;
  }

  @Override
  public Main getPlugin() {
    return plugin;
  }


  @Override
  public MapRestorerManager getMapRestorerManager() {
    return mapRestorerManager;
  }


  private void setPluginValues() {
    for(SpawnPoint point : SpawnPoint.values()) {
      spawnPoints.put(point, new ArrayList<>());
    }
  }

  /**
   * Get mid location of arena.
   *
   * @return mid location of arena
   */
  @Nullable
  public Location getMidLocation() {
    return spawnPoints.get(SpawnPoint.MID).get(0);
  }

  /**
   * Set mid location of arena.
   *
   * @param midLoc new end location of arena
   */
  public void setMidLocation(Location midLoc) {
    spawnPoints.put(SpawnPoint.MID, Collections.singletonList(midLoc));
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
    Base base = getBase(player);
    if(base != null) {
      List<Player> mates = new ArrayList<>(base.getPlayers());
      mates.remove(player);
      return mates;
    }
    return Collections.emptyList();
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

  public void setWinner(Base winner) {
    this.winner = winner;
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
  public void cleanUpArena() {
    getBases().forEach(Base::reset);
    resetPlacedBlocks();
    resetHits();
    deaths.clear();
    spectators.clear();
    round = 0;
    resetRound = 0;
    winner = null;
  }

  public List<Player> getDeaths() {
    return deaths;
  }

  public List<Player> getSpectators() {
    return spectators;
  }

  public int getResetRound() {
    return resetRound;
  }

  public void setResetRound(int resetRound) {
    this.resetRound = resetRound;
  }

  public void resetRound() {
    for(Base base : bases) {
      base.addCageFloor();
    }
    resetRound = getArenaOption("RESET_TIME");
    round++;
    int resetBlocksOption = getArenaOption("RESET_BLOCKS");
    if(resetBlocksOption != 0 && resetBlocksOption - getRound() == 0) {
      resetPlacedBlocks();
      round = 0;
    }
    resetHits();
    for(Player player : getPlayersLeft()) {
      VersionUtils.teleport(player, getBase(player).getPlayerSpawnPoint());
      new MessageBuilder("IN_GAME_MESSAGES_ARENA_BLOCKED_RESET").asKey().arena(this).player(player).sendPlayer();
      plugin.getUserManager().addExperience(player, 2);
      resetPlayer(player);
      plugin.getUserManager().getUser(player).getKit().giveKitItems(player);
      player.updateInventory();
    }
    plugin.getRewardsHandler().performReward(this, plugin.getRewardsHandler().getRewardType("RESET_ROUND"));
    plugin.getPowerupRegistry().spawnPowerup(getMidLocation(), this);
    Bukkit.getPluginManager().callEvent(new TBRoundResetEvent(this, round));
  }

  public void resetPlayer(Player player) {
    player.getInventory().clear();
    player.getInventory().setArmorContents(null);
    for(PotionEffect pe : player.getActivePotionEffects()) {
      player.removePotionEffect(pe.getType());
    }
    player.setExp(0);
    player.setFireTicks(0);
    player.setGameMode(GameMode.SURVIVAL);
    player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 30, 10));
    VersionUtils.setMaxHealth(player, VersionUtils.getMaxHealth(player));
    player.setHealth(VersionUtils.getMaxHealth(player));
    player.setAllowFlight(false);
    player.setFlying(false);
    player.updateInventory();
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
  public void teleportAllToBaseLocation() {
    for(Player player : getPlayers()) {
      VersionUtils.teleport(player, getBase(player).getPlayerSpawnPoint());
    }
  }


  void removePlayer(Player player) {
    if(player != null) {
      getPlayers().remove(player);
      if(getBase(player) != null) {
        getBase(player).removePlayer(player);
      }
    }
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

  public enum Mode {
    HEARTS, POINTS
  }

  public enum SpawnPoint {
    ARENA_BORDER_1, ARENA_BORDER_2, MID
  }
}