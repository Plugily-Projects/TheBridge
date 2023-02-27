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

/**
 * @author Tigerpanzer_02, 2Wild4You
 * <p>
 * Created at 31.10.2020
 */
public class Reward {

  private final RewardType type;
  private final RewardExecutor executor;
  private String executableCode;
  private final double chance;

  public Reward(RewardType type, String rawCode) {
    this.type = type;
    String processedCode = rawCode;

    //set reward executor based on provided code
    if(rawCode.contains("p:")) {
      this.executor = RewardExecutor.PLAYER;
      processedCode = StringUtils.replace(processedCode, "p:", "");
    } else if(rawCode.contains("script:")) {
      this.executor = RewardExecutor.SCRIPT;
      processedCode = StringUtils.replace(processedCode, "script:", "");
    } else {
      this.executor = RewardExecutor.CONSOLE;
    }

    //search for chance modifier
    if(processedCode.contains("chance(")) {
      int loc = processedCode.indexOf(")");
      //modifier is invalid
      if(loc == -1) {
        Bukkit.getLogger().warning("rewards.yml configuration is broken! Make sure you don't forget using ')' character in chance condition! Command: " + rawCode);
        //invalid code, 0% chance to execute
        this.chance = 0.0;
        return;
      }
      String chanceStr = processedCode;
      chanceStr = chanceStr.substring(0, loc).replaceAll("[^0-9]+", "");
      double chance = Double.parseDouble(chanceStr);
      processedCode = StringUtils.replace(processedCode, "chance(" + chanceStr + "):", "");
      this.chance = chance;
    } else {
      this.chance = 100.0;
    }
    this.executableCode = processedCode;
  }

  public RewardExecutor getExecutor() {
    return executor;
  }

  public String getExecutableCode() {
    return executableCode;
  }

  public double getChance() {
    return chance;
  }

  public RewardType getType() {
    return type;
  }

  public enum RewardType {
    WON("won"), LOSE("lose"), KILL("kill"), POINT("point"), RESET_ROUND("resetround"),
    END_GAME("endgame"), DEATH("death");

    private final String path;

    RewardType(String path) {
      this.path = path;
    }

    public String getPath() {
      return path;
    }

  }

  public enum RewardExecutor {
    CONSOLE, PLAYER, SCRIPT
  }

}
