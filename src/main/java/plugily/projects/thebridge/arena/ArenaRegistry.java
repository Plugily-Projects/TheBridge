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

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pl.plajerlair.commonsbox.minecraft.configuration.ConfigUtils;
import pl.plajerlair.commonsbox.minecraft.dimensional.Cuboid;
import pl.plajerlair.commonsbox.minecraft.serialization.LocationSerializer;
import plugily.projects.thebridge.Main;
import plugily.projects.thebridge.arena.base.Base;
import plugily.projects.thebridge.arena.options.ArenaOption;
import plugily.projects.thebridge.handlers.hologram.ArmorStandHologram;
import plugily.projects.thebridge.utils.Debugger;
import plugily.projects.thebridge.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Tigerpanzer_02, 2Wild4You
 * <p>
 * Created at 31.10.2020
 */
public class ArenaRegistry {

  private static final Main plugin = JavaPlugin.getPlugin(Main.class);
  private static final List<Arena> arenas = new ArrayList<>();
  private static int bungeeArena = -999;

  /**
   * Checks if player is in any arena
   *
   * @param player player to check
   * @return [b]true[/b] when player is in arena, [b]false[/b] if otherwise
   */
  public static boolean isInArena(Player player) {
    for (Arena arena : arenas) {
      if (arena.getPlayers().contains(player)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns arena where the player is
   *
   * @param p target player
   * @return Arena or null if not playing
   * @see #isInArena(Player) to check if player is playing
   */
  public static Arena getArena(Player p) {
    if (p == null || !p.isOnline()) {
      return null;
    }
    for (Arena arena : arenas) {
      for (Player player : arena.getPlayers()) {
        if (player.getUniqueId().equals(p.getUniqueId())) {
          return arena;
        }
      }
    }
    return null;
  }

  /**
   * Returns arena based by ID
   *
   * @param id name of arena
   * @return Arena or null if not found
   */
  public static Arena getArena(String id) {
    for (Arena loopArena : arenas) {
      if (loopArena.getId().equalsIgnoreCase(id)) {
        return loopArena;
      }
    }

    return null;
  }

  public static void registerArena(Arena arena) {
    Debugger.debug("Registering new game instance {0}", arena.getId());
    arenas.add(arena);
  }

  public static void unregisterArena(Arena arena) {
    Debugger.debug("Unegistering game instance {0}", arena.getId());
    arenas.remove(arena);
  }

  public static void registerArenas() {
    Debugger.debug("Initial arenas registration");
    long start = System.currentTimeMillis();
    if (ArenaRegistry.getArenas().size() > 0) {
      ArenaRegistry.getArenas().forEach(Arena::cleanUpArena);

      new ArrayList<>(ArenaRegistry.getArenas()).forEach(ArenaRegistry::unregisterArena);
    }
    FileConfiguration config = ConfigUtils.getConfig(plugin, "arenas");

    if (!config.isConfigurationSection("instances")) {
      Debugger.sendConsoleMsg(plugin.getChatManager().colorMessage("Validator.No-Instances-Created"));
      return;
    }

    for (String id : config.getConfigurationSection("instances").getKeys(false)) {
      Arena arena;
      String s = "instances." + id + ".";
      if (s.contains("default")) {
        continue;
      }
      arena = new Arena(id);
      arena.setMinimumPlayers(config.getInt(s + "minimumplayers", 2));
      arena.setMapName(config.getString(s + "mapname", "none"));


      arena.setLobbyLocation(LocationSerializer.getLocation(config.getString(s + "lobbylocation", "world,364.0,63.0,-72.0,0.0,0.0")));
      arena.setEndLocation(LocationSerializer.getLocation(config.getString(s + "endlocation", "world,364.0,63.0,-72.0,0.0,0.0")));
      arena.setMidLocation(LocationSerializer.getLocation(config.getString(s + "midlocation", "world,364.0,63.0,-72.0,0.0,0.0")));
      arena.setSpectatorLocation(LocationSerializer.getLocation(config.getString(s + "spectatorlocation", "world,364.0,63.0,-72.0,0.0,0.0")));
      arena.setArenaBorder(new Cuboid(LocationSerializer.getLocation(config.getString(s + "arenalocation1", "world,364.0,63.0,-72.0,0.0,0.0")), LocationSerializer.getLocation(config.getString(s + "arenalocation2", "world,364.0,63.0,-72.0,0.0,0.0"))));
      int bases = 0;
      if (config.contains(s + "bases")) {
        if (config.isConfigurationSection(s + "bases")) {
          for (String baseID : config.getConfigurationSection(s + "bases").getKeys(false)) {
            if (config.isSet(s + "bases." + baseID + ".isdone")) {
              Base base = new Base(
                config.getString("instances." + arena.getId() + ".bases." + baseID + ".color"),
                LocationSerializer.getLocation(config.getString("instances." + arena.getId() + ".bases." + baseID + ".baselocation1")),
                LocationSerializer.getLocation(config.getString("instances." + arena.getId() + ".bases." + baseID + ".baselocation2")),
                LocationSerializer.getLocation(config.getString("instances." + arena.getId() + ".bases." + baseID + ".spawnpoint")),
                LocationSerializer.getLocation(config.getString("instances." + arena.getId() + ".bases." + baseID + ".respawnpoint")),
                LocationSerializer.getLocation(config.getString("instances." + arena.getId() + ".bases." + baseID + ".portallocation1")),
                LocationSerializer.getLocation(config.getString("instances." + arena.getId() + ".bases." + baseID + ".portallocation2")),
                config.getInt("instances." + arena.getId() + ".maximumsize")
              );
              arena.addBase(base);
              if (config.getString("instances." + arena.getId() + ".bases." + baseID + ".cagelocation1") != null)
                base.setCageCuboid(new Cuboid(LocationSerializer.getLocation(config.getString("instances." + arena.getId() + ".bases." + baseID + ".cagelocation1")), LocationSerializer.getLocation(config.getString("instances." + arena.getId() + ".bases." + baseID + ".cagelocation2"))));
              ArmorStandHologram portal = new ArmorStandHologram(Utils.getBlockCenter(LocationSerializer.getLocation(config.getString("instances." + arena.getId() + ".bases." + baseID + ".portalhologram"))));
              for (String str : plugin.getChatManager().colorMessage("In-Game.Messages.Portal.Hologram").split(";")) {
                portal.appendLine(str.replace("%base%", base.getFormattedColor()));
              }
              base.setArmorStandHologram(portal);
              bases++;
            } else {
              System.out.println("Non configured bases instances found for arena " + id);
              arena.setReady(false);
            }
          }
        } else {
          System.out.println("Non configured bases in arena " + id);
          arena.setReady(false);
        }
      } else {
        System.out.print("Instance " + id + " doesn't contains bases!");
        arena.setReady(false);
      }
      if (bases < 2) {
        System.out.print("Instance " + id + " doesn't contains 2 bases that are done!");
        arena.setReady(false);
      }
      arena.setOptionValue(ArenaOption.SIZE, config.getInt("instances." + arena.getId() + ".maximumsize", 3));
      arena.setMaximumPlayers(bases * arena.getOption(ArenaOption.SIZE));
      if (config.contains(s + "mode")) {
        arena.setMode(Arena.Mode.valueOf(config.getString(s + "mode").toUpperCase()));
      } else {
        arena.setMode(Arena.Mode.POINTS);
      }
      arena.setOptionValue(ArenaOption.MODE_VALUE, config.getInt(s + "modevalue", 5));
      arena.setOptionValue(ArenaOption.RESET_BLOCKS, config.getInt(s + "resetblocks", 0));
      arena.setOptionValue(ArenaOption.RESET_TIME, config.getInt(s + "resettime", 5));
      for (Base base : arena.getBases()) {
        if (arena.getArenaBorder().isIn(base.getBaseCuboid().getCenter())) {
          Debugger.sendConsoleMsg(plugin.getChatManager().colorMessage("Validator.Invalid-Arena-Configuration").replace("%arena%", id).replace("%error%", "YOUR BASE CUBOIDS ARE NOT INSIDE ARENA CUBOID"));
          arena.setReady(false);
          ArenaRegistry.registerArena(arena);
          break;
        }
      }
      if (!config.getBoolean(s + "isdone", false)) {
        Debugger.sendConsoleMsg(plugin.getChatManager().colorMessage("Validator.Invalid-Arena-Configuration").replace("%arena%", id).replace("%error%", "NOT VALIDATED"));
        arena.setReady(false);
        ArenaRegistry.registerArena(arena);
        continue;
      }
      ArenaRegistry.registerArena(arena);
      arena.start();
      Debugger.sendConsoleMsg(plugin.getChatManager().colorMessage("Validator.Instance-Started").replace("%arena%", id));
    }
    Debugger.debug("Arenas registration completed, took {0}ms", System.currentTimeMillis() - start);
  }

  public static List<Arena> getArenas() {
    return arenas;
  }

  public static void shuffleBungeeArena() {
    bungeeArena = new Random().nextInt(arenas.size());
  }

  public static int getBungeeArena() {
    if (bungeeArena == -999) {
      bungeeArena = new Random().nextInt(arenas.size());
    }
    return bungeeArena;
  }
}