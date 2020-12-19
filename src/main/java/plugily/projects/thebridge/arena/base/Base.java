package plugily.projects.thebridge.arena.base;


import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import pl.plajerlair.commonsbox.minecraft.dimensional.Cuboid;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tigerpanzer_02 & 2Wild4You
 * <p>
 * Created at 07.11.2020
 */
public class Base {

  private Color color;
  private Location baseLocation1;
  private Location baseLocation2;
  private Location playerSpawnPoint;
  private Location playerRespawnPoint;
  private Location portalLocation1;
  private Location portalLocation2;
  private Integer maximumSize;
  private Integer points = 0;

  private ArrayList<Player> players = new ArrayList<>();

  private Cuboid baseCuboid;
  private Cuboid portalCuboid;

  public Base(Color color, Location baseLocation1, Location baseLocation2, Location playerSpawnPoint, Location playerRespawnPoint, Location portalLocation1, Location portalLocation2, Integer maximumSize) {
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

  public Color getColor() {
    return color;
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

  public Integer getMaximumSize() {
    return maximumSize;
  }

  public Integer getPoints() {
    return points;
  }

  public void setPoints(Integer points) {
    this.points = points;
  }

  public void addPoint(){
    this.points++;
  }

  public void reset() {
    this.points = 0;
    resetPlayers();
  }
}
