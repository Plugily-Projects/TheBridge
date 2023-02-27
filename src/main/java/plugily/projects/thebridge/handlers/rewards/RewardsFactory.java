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

package plugily.projects.thebridge.handlers.rewards;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import plugily.projects.commonsbox.minecraft.configuration.ConfigUtils;
import plugily.projects.commonsbox.minecraft.engine.ScriptEngine;
import plugily.projects.thebridge.Main;
import plugily.projects.thebridge.arena.Arena;
import plugily.projects.thebridge.arena.ArenaRegistry;
import plugily.projects.thebridge.utils.Debugger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

/**
 * @author Tigerpanzer_02, 2Wild4You
 * <p>
 * Created at 31.10.2020
 */
public class RewardsFactory {

  private final Set<Reward> rewards = new HashSet<>();
  private final FileConfiguration config;
  private final boolean enabled;

  public RewardsFactory(Main plugin) {
    enabled = plugin.getConfig().getBoolean("Rewards-Enabled");
    config = ConfigUtils.getConfig(plugin, "rewards");
    registerRewards();
  }

  public void performReward(Arena arena, Reward.RewardType type) {
    if(enabled) {
      arena.getPlayers().forEach(p -> performReward(p, type));
    }
  }

  public void performReward(Player player, Reward.RewardType type) {
    if(!enabled) {
      return;
    }
    Arena arena = ArenaRegistry.getArena(player);
    if(arena == null) {
      return;
    }
    for(Reward reward : rewards) {
      if(reward.getType() == type) {
        //cannot execute if chance wasn't met
        if(reward.getChance() != -1 && ThreadLocalRandom.current().nextInt(0, 100) > reward.getChance()) {
          continue;
        }
        String command = reward.getExecutableCode();
        command = StringUtils.replace(command, "%PLAYER%", player.getName());
        command = formatCommandPlaceholders(command, arena);
        switch(reward.getExecutor()) {
          case CONSOLE:
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
            break;
          case PLAYER:
            if(player.isOnline())
              player.performCommand(command);
            break;
          case SCRIPT:
            ScriptEngine engine = new ScriptEngine();
            engine.setValue("player", player);
            engine.setValue("server", Bukkit.getServer());
            engine.setValue("arena", arena);
            engine.execute(command);
            break;
          default:
            break;
        }
      }
    }
  }

  private String formatCommandPlaceholders(String command, Arena arena) {
    String formatted = command;
    formatted = StringUtils.replace(formatted, "%ARENA-ID%", arena.getId());
    formatted = StringUtils.replace(formatted, "%MAPNAME%", arena.getMapName());
    formatted = StringUtils.replace(formatted, "%PLAYERAMOUNT%", String.valueOf(arena.getPlayers().size()));
    return formatted;
  }

  private void registerRewards() {
    if(!enabled) {
      return;
    }
    Debugger.debug(Level.INFO, "[RewardsFactory] Starting rewards registration");
    long start = System.currentTimeMillis();

    Map<Reward.RewardType, Integer> registeredRewards = new HashMap<>();
    for(Reward.RewardType rewardType : Reward.RewardType.values()) {
      try {
        for(String reward : config.getStringList("rewards." + rewardType.getPath())) {
          rewards.add(new Reward(rewardType, reward));
          registeredRewards.put(rewardType, registeredRewards.getOrDefault(rewardType, 0) + 1);
        }
      } catch(Exception ignored) {/*ignored*/}
    }
    for(Reward.RewardType rewardType : registeredRewards.keySet()) {
      Debugger.debug(Level.INFO, "[RewardsFactory] Registered {0} {1} rewards!", registeredRewards.get(rewardType), rewardType.name());
    }
    Debugger.debug(Level.INFO, "[RewardsFactory] Registered all rewards took {0}ms", System.currentTimeMillis() - start);
  }


}
