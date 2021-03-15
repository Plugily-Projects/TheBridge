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

package plugily.projects.thebridge.arena.base;


import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pl.plajerlair.commonsbox.minecraft.dimensional.Cuboid;
import plugily.projects.thebridge.Main;
import plugily.projects.thebridge.handlers.hologram.ArmorStandHologram;
import plugily.projects.thebridge.handlers.language.LanguageManager;
import plugily.projects.thebridge.utils.Debugger;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tigerpanzer_02, 2Wild4You
 * <p>
 * Created at 07.11.2020
 */
public class Base {

  private final String color;
  private final Location baseLocation1;
  private final Location baseLocation2;
  private final Location playerSpawnPoint;
  private final Location playerRespawnPoint;
  private final Location portalLocation1;
  private final Location portalLocation2;
  private final Integer maximumSize;
  private Integer points = 0;

  private ArrayList<Player> players = new ArrayList<>();

  private final Cuboid baseCuboid;
  private final Cuboid portalCuboid;
  private Cuboid cageCuboid;
  private Material cageBlock;
  private boolean damageCooldown = false;

  private ArmorStandHologram armorStandHologram;

  private static final Main plugin = JavaPlugin.getPlugin(Main.class);

  public Base(String color, Location baseLocation1, Location baseLocation2, Location playerSpawnPoint, Location playerRespawnPoint, Location portalLocation1, Location portalLocation2, Integer maximumSize) {
    this.color = color;
    this.baseLocation1 = baseLocation1;
    this.baseLocation2 = baseLocation2;
    this.playerSpawnPoint = playerSpawnPoint;
    this.playerRespawnPoint = playerRespawnPoint;
    this.portalLocation1 = portalLocation1;
    this.portalLocation2 = portalLocation2;
    this.baseCuboid = new Cuboid(baseLocation1, baseLocation2);
    this.portalCuboid = new Cuboid(portalLocation1, portalLocation2);
    this.maximumSize = maximumSize;
  }

  public String getColor() {
    return color;
  }

  public String getMaterialColor() {
    switch(color.toLowerCase()) {
      case "dark_blue":
      case "dark_aqua":
      case "aqua":
        return "light_blue";
      case  "dark_green":
        return "green";
      case "green":
        return "lime";
      case "dark_red":
        return "red";
      case "dark_purple":
        return "purple";
      case "dark_gray":
        return "gray";
      case "light_purple":
        return "magenta";
        //not used? BROWN, PINK, ORANGE
    }
    return color;
  }

  public String getFormattedColor() {
    return ChatColor.translateAlternateColorCodes('&', LanguageManager.getLanguageMessage("Bases.Colors." + getColor()) + "&r");
  }

  public Location getBaseLocation1() {
    return baseLocation1;
  }

  public Location getBaseLocation2() {
    return baseLocation2;
  }

  public Location getPlayerSpawnPoint() {
    return playerSpawnPoint;
  }

  public Location getPlayerRespawnPoint() {
    return playerRespawnPoint;
  }

  public Location getPortalLocation1() {
    return portalLocation1;
  }

  public Location getPortalLocation2() {
    return portalLocation2;
  }

  public Cuboid getBaseCuboid() {
    return baseCuboid;
  }

  public Cuboid getPortalCuboid() {
    return portalCuboid;
  }

  public void addPlayer(Player player) {
    this.players.add(player);
  }

  public void removePlayer(Player player) {
    this.players.remove(player);
  }

  public void setPlayers(ArrayList<Player> players) {
    this.players = players;
  }

  public void resetPlayers() {
    this.players.clear();
  }

  public List<Player> getPlayers() {
    return players;
  }

  public List<Player> getAlivePlayers() {
    List<Player> alivePlayers = new ArrayList<>(getPlayers());
    alivePlayers.removeIf(player -> plugin.getUserManager().getUser(player).isSpectator());
    return alivePlayers;
  }

  public Integer getAlivePlayersSize() {
    return getAlivePlayers().size();
  }


  public Integer getPlayersSize() {
    return players.size();
  }

  public Integer getMaximumSize() {
    return maximumSize;
  }

  public Integer getPoints() {
    return points;
  }

  public void setPoints(Integer points) {
    this.points = points;
  }

  public void addPoint() {
    this.points++;
  }

  public ArmorStandHologram getArmorStandHologram() {
    return armorStandHologram;
  }

  public void setArmorStandHologram(ArmorStandHologram armorStandHologram) {
    this.armorStandHologram = armorStandHologram;
  }

  public Cuboid getCageCuboid() {
    return cageCuboid;
  }

  public Material getCageFloorMaterial() {
    return cageBlock;
  }

  public void setCageFloorMaterial(Material cageBlock) {
    this.cageBlock = cageBlock;
  }

  public void setCageCuboid(Cuboid cageCuboid) {
    this.cageCuboid = cageCuboid;
    setCageFloorMaterial(cageCuboid.getCenter().getBlock().getType());
  }

  public void removeCageFloor() {
    if(!checkCageFloor(cageBlock)) {
      return;
    }
    cageCuboid.fill(Material.AIR);
    damageCooldown = true;
    Bukkit.getScheduler().runTaskLater(plugin, () -> {
      damageCooldown = false;
    }, 20 * 4);
  }

  public void addCageFloor() {
    if(!checkCageFloor(cageBlock)) {
      return;
    }
    cageCuboid.fill(cageBlock);
  }

  private boolean checkCageFloor(Material cageBlock) {
    if(cageCuboid == null) {
      return false;
    }
    if(cageBlock == null) {
      return false;
    }
    if(cageBlock == Material.AIR) {
      Debugger.sendConsoleMsg("[TheBridge] &cARENA SETUP PROBLEM | Please only select your floor of the cage to setup it proper! We found Material Air on the selected area!");
      return false;
    }
    return true;
  }

  public boolean isDamageCooldown() {
    return damageCooldown;
  }

  public void reset() {
    this.points = 0;
    resetPlayers();
    addCageFloor();
  }
}
