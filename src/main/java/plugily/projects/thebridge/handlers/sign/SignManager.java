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

package plugily.projects.thebridge.handlers.sign;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import plugily.projects.commonsbox.minecraft.compat.ServerVersion;
import plugily.projects.commonsbox.minecraft.compat.xseries.XMaterial;
import plugily.projects.commonsbox.minecraft.configuration.ConfigUtils;
import plugily.projects.commonsbox.minecraft.serialization.LocationSerializer;
import plugily.projects.thebridge.Main;
import plugily.projects.thebridge.arena.Arena;
import plugily.projects.thebridge.arena.ArenaManager;
import plugily.projects.thebridge.arena.ArenaRegistry;
import plugily.projects.thebridge.arena.ArenaState;
import plugily.projects.thebridge.handlers.ChatManager;
import plugily.projects.thebridge.handlers.language.LanguageManager;
import plugily.projects.thebridge.utils.Debugger;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * @author Tigerpanzer_02, 2Wild4You
 * <p>
 * Created at 31.10.2020
 */
public class SignManager implements Listener {

  private final List<ArenaSign> arenaSigns = new ArrayList<>();
  private final Map<ArenaState, String> gameStateToString = new EnumMap<>(ArenaState.class);
  private final Main plugin;
  private final ChatManager chatManager;
  private final List<String> signLines;

  public SignManager(Main plugin) {
    this.plugin = plugin;
    chatManager = plugin.getChatManager();
    gameStateToString.put(ArenaState.WAITING_FOR_PLAYERS, chatManager.colorMessage("Signs.Game-States.Inactive"));
    gameStateToString.put(ArenaState.STARTING, chatManager.colorMessage("Signs.Game-States.Starting"));
    gameStateToString.put(ArenaState.IN_GAME, chatManager.colorMessage("Signs.Game-States.In-Game"));
    gameStateToString.put(ArenaState.ENDING, chatManager.colorMessage("Signs.Game-States.Ending"));
    gameStateToString.put(ArenaState.RESTARTING, chatManager.colorMessage("Signs.Game-States.Restarting"));
    signLines = LanguageManager.getLanguageList("Signs.Lines");
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler
  public void onSignChange(SignChangeEvent e) {
    if(!e.getPlayer().hasPermission("thebridge.admin.sign.create")
      || !e.getLine(0).equalsIgnoreCase("[thebridge]")) {
      return;
    }
    if(e.getLine(1).isEmpty()) {
      e.getPlayer().sendMessage(chatManager.getPrefix() + chatManager.colorMessage("Signs.Please-Type-Arena-Name"));
      return;
    }
    for(Arena arena : ArenaRegistry.getArenas()) {
      if(!arena.getId().equalsIgnoreCase(e.getLine(1))) {
        continue;
      }
      for(int i = 0; i < signLines.size(); i++) {
        e.setLine(i, formatSign(signLines.get(i), arena));
      }
      arenaSigns.add(new ArenaSign((Sign) e.getBlock().getState(), arena));
      e.getPlayer().sendMessage(chatManager.getPrefix() + chatManager.colorMessage("Signs.Sign-Created"));
      String location = e.getBlock().getWorld().getName() + "," + e.getBlock().getX() + "," + e.getBlock().getY() + "," + e.getBlock().getZ() + ",0.0,0.0";
      FileConfiguration config = ConfigUtils.getConfig(plugin, "arenas");
      List<String> locs = config.getStringList("instances." + arena.getId() + ".signs");
      locs.add(location);
      config.set("instances." + arena.getId() + ".signs", locs);
      ConfigUtils.saveConfig(plugin, config, "arenas");
      return;
    }
    e.getPlayer().sendMessage(chatManager.getPrefix() + chatManager.colorMessage("Signs.Arena-Doesnt-Exists"));
  }

  private String formatSign(String msg, Arena a) {
    String formatted = msg;
    formatted = StringUtils.replace(formatted, "%mapname%", a.getMapName());
    int maxPlayers = a.getMaximumPlayers();
    if(a.getPlayers().size() >= maxPlayers) {
      formatted = StringUtils.replace(formatted, "%state%", chatManager.colorMessage("Signs.Game-States.Full-Game"));
    } else {
      formatted = StringUtils.replace(formatted, "%state%", gameStateToString.get(a.getArenaState()));
    }
    formatted = StringUtils.replace(formatted, "%playersize%", String.valueOf(a.getPlayers().size()));
    formatted = StringUtils.replace(formatted, "%maxplayers%", String.valueOf(maxPlayers));
    formatted = chatManager.colorRawMessage(formatted);
    return formatted;
  }

  @EventHandler
  public void onSignDestroy(BlockBreakEvent e) {
    ArenaSign arenaSign = getArenaSignByBlock(e.getBlock());
    if(!e.getPlayer().hasPermission("thebridge.admin.sign.break") || arenaSign == null) {
      return;
    }
    arenaSigns.remove(arenaSign);
    FileConfiguration config = ConfigUtils.getConfig(plugin, "arenas");
    String location = e.getBlock().getWorld().getName() + "," + e.getBlock().getX() + "," + e.getBlock().getY() + "," + e.getBlock().getZ() + "," + "0.0,0.0";
    for(String arena : config.getConfigurationSection("instances").getKeys(false)) {
      for(String sign : config.getStringList("instances." + arena + ".signs")) {
        if(!sign.equals(location)) {
          continue;
        }
        List<String> signs = config.getStringList("instances." + arena + ".signs");
        signs.remove(location);
        config.set("instances." + arena + ".signs", signs);
        ConfigUtils.saveConfig(plugin, config, "arenas");
        e.getPlayer().sendMessage(chatManager.getPrefix() + chatManager.colorMessage("Signs.Sign-Removed"));
        return;
      }
    }
    e.getPlayer().sendMessage(chatManager.getPrefix() + ChatColor.RED + "Couldn't remove sign from configuration! Please do this manually!");
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onJoinAttempt(PlayerInteractEvent e) {
    ArenaSign arenaSign = getArenaSignByBlock(e.getClickedBlock());
    if(e.getAction() == Action.RIGHT_CLICK_BLOCK && arenaSign != null && e.getClickedBlock().getState() instanceof Sign) {
      Arena arena = arenaSign.getArena();
      if(arena != null) {
        ArenaManager.joinAttempt(e.getPlayer(), arena);
      }
    }
  }

  @Nullable
  private ArenaSign getArenaSignByBlock(Block block) {
    if(block == null) {
      return null;
    }
    for(ArenaSign sign : arenaSigns) {
      if(sign.getSign().getLocation().equals(block.getLocation())) {
        return sign;
      }
    }
    return null;
  }

  public void loadSigns() {
    Debugger.debug("Signs load event started");
    long start = System.currentTimeMillis();

    arenaSigns.clear();
    FileConfiguration config = ConfigUtils.getConfig(plugin, "arenas");
    for(String path : config.getConfigurationSection("instances").getKeys(false)) {
      for(String sign : config.getStringList("instances." + path + ".signs")) {
        Location loc = LocationSerializer.getLocation(sign);
        if(loc.getBlock().getState() instanceof Sign) {
          arenaSigns.add(new ArenaSign((Sign) loc.getBlock().getState(), ArenaRegistry.getArena(path)));
        } else {
          Debugger.debug(Level.WARNING, "Block at location {0} for arena {1} not a sign", loc, path);
        }
      }
    }
    Debugger.debug("Sign load event finished took {0}ms", System.currentTimeMillis() - start);
  }

  public void updateSigns() {
    Debugger.performance("SignUpdate", "[PerformanceMonitor] [SignUpdate] Updating signs");
    long start = System.currentTimeMillis();

    for(ArenaSign arenaSign : arenaSigns) {
      Sign sign = arenaSign.getSign();
      for(int i = 0; i < signLines.size(); i++) {
        sign.setLine(i, formatSign(signLines.get(i), arenaSign.getArena()));
      }
      if(plugin.getConfig().getBoolean("Signs-Block-States-Enabled", true) && arenaSign.getBehind() != null) {
        Block behind = arenaSign.getBehind();
        try {
          switch(arenaSign.getArena().getArenaState()) {
            case WAITING_FOR_PLAYERS:
              behind.setType(XMaterial.WHITE_STAINED_GLASS.parseMaterial());
              if(ServerVersion.Version.isCurrentLower(ServerVersion.Version.v1_13_R1)) {
                Block.class.getMethod("setData", byte.class).invoke(behind, (byte) 0);
              }
              break;
            case STARTING:
              behind.setType(XMaterial.YELLOW_STAINED_GLASS.parseMaterial());
              if(ServerVersion.Version.isCurrentLower(ServerVersion.Version.v1_13_R1)) {
                Block.class.getMethod("setData", byte.class).invoke(behind, (byte) 4);
              }
              break;
            case IN_GAME:
              behind.setType(XMaterial.ORANGE_STAINED_GLASS.parseMaterial());
              if(ServerVersion.Version.isCurrentLower(ServerVersion.Version.v1_13_R1)) {
                Block.class.getMethod("setData", byte.class).invoke(behind, (byte) 1);
              }
              break;
            case ENDING:
              behind.setType(XMaterial.GRAY_STAINED_GLASS.parseMaterial());
              if(ServerVersion.Version.isCurrentLower(ServerVersion.Version.v1_13_R1)) {
                Block.class.getMethod("setData", byte.class).invoke(behind, (byte) 7);
              }
              break;
            case RESTARTING:
              behind.setType(XMaterial.BLACK_STAINED_GLASS.parseMaterial());
              if(ServerVersion.Version.isCurrentLower(ServerVersion.Version.v1_13_R1)) {
                Block.class.getMethod("setData", byte.class).invoke(behind, (byte) 15);
              }
              break;
            default:
              break;
          }
        } catch(Exception ignored) {
        }
      }
      sign.update();
    }
    Debugger.performance("SignUpdate", "[PerformanceMonitor] [SignUpdate] Updated signs took {0}ms", System.currentTimeMillis() - start);
  }

  public List<ArenaSign> getArenaSigns() {
    return arenaSigns;
  }

  public Map<ArenaState, String> getGameStateToString() {
    return gameStateToString;
  }
}