package plugily.projects.thebridge.arena.base;


import org.bukkit.Location;
import pl.plajerlair.commonsbox.minecraft.dimensional.Cuboid;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 10.12.2018
 */
public class Base {

  private String color;
  private Location baseLocation1;
  private Location baseLocation2;
  private Location playerSpawnPoint;
  private Location playerRespawnPoint;
  private Location portalLocation1;
  private Location portalLocation2;

  private Cuboid baseCuboid;
  private Cuboid portalCuboid;

  public Base(String color, Location baseLocation1, Location baseLocation2, Location playerSpawnPoint, Location playerRespawnPoint, Location portalLocation1, Location portalLocation2) {
    this.color = color;
    this.baseLocation1 = baseLocation1;
    this.baseLocation2 = baseLocation2;
    this.playerSpawnPoint = playerSpawnPoint;
    this.playerRespawnPoint = playerRespawnPoint;
    this.portalLocation1 = portalLocation1;
    this.portalLocation2 = portalLocation2;
    this.baseCuboid = new Cuboid(baseLocation1, baseLocation2);
    this.portalCuboid = new Cuboid(portalLocation1, portalLocation2);
  }

  public String getColor() {
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
}
