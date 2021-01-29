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

package plugily.projects.thebridge.arena.managers;

import me.clip.placeholderapi.PlaceholderAPI;
import me.tigerhix.lib.scoreboard.ScoreboardLib;
import me.tigerhix.lib.scoreboard.common.EntryBuilder;
import me.tigerhix.lib.scoreboard.type.Entry;
import me.tigerhix.lib.scoreboard.type.Scoreboard;
import me.tigerhix.lib.scoreboard.type.ScoreboardHandler;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pl.plajerlair.commonsbox.string.StringFormatUtils;
import plugily.projects.thebridge.Main;
import plugily.projects.thebridge.api.StatsStorage;
import plugily.projects.thebridge.arena.Arena;
import plugily.projects.thebridge.arena.ArenaState;
import plugily.projects.thebridge.arena.base.Base;
import plugily.projects.thebridge.arena.options.ArenaOption;
import plugily.projects.thebridge.handlers.ChatManager;
import plugily.projects.thebridge.handlers.language.LanguageManager;
import plugily.projects.thebridge.user.User;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 07.11.2020
 */
public class ScoreboardManager {

  private static final Main plugin = JavaPlugin.getPlugin(Main.class);
  private static final ChatManager chatManager = plugin.getChatManager();
  private static final String boardTitle = chatManager.colorMessage("Scoreboard.Title");
  private final List<Scoreboard> scoreboards = new ArrayList<>();
  private final Arena arena;

  public ScoreboardManager(Arena arena) {
    this.arena = arena;
  }

  /**
   * Creates arena scoreboard for target user
   *
   * @param user user that represents game player
   * @see User
   */
  public void createScoreboard(User user) {
    Scoreboard scoreboard = ScoreboardLib.createScoreboard(user.getPlayer()).setHandler(new ScoreboardHandler() {
      @Override
      public String getTitle(Player player) {
        return boardTitle;
      }

      @Override
      public List<Entry> getEntries(Player player) {
        return formatScoreboard(user);
      }
    });
    scoreboard.activate();
    scoreboards.add(scoreboard);
  }

  /**
   * Removes scoreboard of user
   *
   * @param user user that represents game player
   * @see User
   */
  public void removeScoreboard(User user) {
    for(Scoreboard board : scoreboards) {
      if(board.getHolder().equals(user.getPlayer())) {
        scoreboards.remove(board);
        board.deactivate();
        return;
      }
    }
  }

  /**
   * Forces all scoreboards to deactivate.
   */
  public void stopAllScoreboards() {
    scoreboards.forEach(Scoreboard::deactivate);
    scoreboards.clear();
  }

  private List<Entry> formatScoreboard(User user) {
    EntryBuilder builder = new EntryBuilder();
    List<String> lines;
    if(arena.getArenaState() == ArenaState.IN_GAME) {
      lines = LanguageManager.getLanguageList("Scoreboard.Content.Playing");
    } else {
      //apply fix
      if(arena.getArenaState() == ArenaState.ENDING) {
        lines = LanguageManager.getLanguageList("Scoreboard.Content.Playing");
      } else {
        lines = LanguageManager.getLanguageList("Scoreboard.Content." + arena.getArenaState().getFormattedName());
      }
    }
    for(String line : lines) {
      if(line.contains("%ONLYIFRESETBLOCKS%") && arena.getOption(ArenaOption.RESET_BLOCKS) == 0) {
        continue;
      }
      if(line.contains("%BASES%")) {
        if(cachedBaseFormat.isEmpty()) {
          for(Base base : arena.getBases()) {
            builder.next(formatBase(LanguageManager.getLanguageMessage("Scoreboard.Bases.Format"), user, base));
          }
        } else {
          for(String cached : cachedBaseFormat) {
            builder.next(cached);
          }
        }
      } else {
        builder.next(formatScoreboardLine(line, user));
      }
    }
    return builder.build();
  }

  private String formatScoreboardLine(String line, User user) {
    String formattedLine = line;
    formattedLine = StringUtils.replace(formattedLine, "%TIME%", String.valueOf(arena.getTimer() + 1));
    formattedLine = StringUtils.replace(formattedLine, "%FORMATTED_TIME%", StringFormatUtils.formatIntoMMSS(arena.getTimer() + 1));
    formattedLine = StringUtils.replace(formattedLine, "%MAPNAME%", arena.getMapName());
    formattedLine = StringUtils.replace(formattedLine, "%PLAYERS%", String.valueOf(arena.getPlayers().size()));
    formattedLine = StringUtils.replace(formattedLine, "%MAX_PLAYERS%", String.valueOf(arena.getMaximumPlayers()));
    formattedLine = StringUtils.replace(formattedLine, "%MIN_PLAYERS%", String.valueOf(arena.getMinimumPlayers()));
    formattedLine = StringUtils.replace(formattedLine, "%PLAYERS_LEFT%", String.valueOf(arena.getPlayersLeft()));
    formattedLine = StringUtils.replace(formattedLine, "%MODE%", String.valueOf(arena.getMode()));
    formattedLine = StringUtils.replace(formattedLine, "%MODE_VALUE%", String.valueOf(arena.getOption(ArenaOption.MODE_VALUE)));
    formattedLine = StringUtils.replace(formattedLine, "%ONLYIFRESETBLOCKS%", "");
    formattedLine = StringUtils.replace(formattedLine, "%RESET_BLOCKS%", String.valueOf(arena.getOption(ArenaOption.RESET_BLOCKS) - arena.getRound()));
    formattedLine = StringUtils.replace(formattedLine, "%LOCAL_KILLS%", String.valueOf(user.getStat(StatsStorage.StatisticType.LOCAL_KILLS)));
    formattedLine = StringUtils.replace(formattedLine, "%LOCAL_SCORED_POINTS%", String.valueOf(user.getStat(StatsStorage.StatisticType.LOCAL_SCORED_POINTS)));
    formattedLine = StringUtils.replace(formattedLine, "%LOCAL_DEATHS%", String.valueOf(user.getStat(StatsStorage.StatisticType.LOCAL_DEATHS)));
    formattedLine = chatManager.colorRawMessage(formattedLine);
    if(plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
      formattedLine = PlaceholderAPI.setPlaceholders(user.getPlayer(), formattedLine);
    }
    return formattedLine;
  }

  private final List<String> cachedBaseFormat = new ArrayList<>();

  private String formatBase(String line, User user, Base base) {
    String formattedLine = line;
    formattedLine = StringUtils.replace(formattedLine, "%BASE%", base.getColor());
    formattedLine = StringUtils.replace(formattedLine, "%BASE_FORMATTED%", base.getFormattedColor());
    formattedLine = StringUtils.replace(formattedLine, "%BASE_PLAYERS%", String.valueOf(base.getPlayers().size()));
    boolean baseYou = false;
    if(formattedLine.contains("%BASE_YOU%")) baseYou = true;
    if(base.getPlayers().contains(user.getPlayer())) {
      formattedLine = StringUtils.replace(formattedLine, "%BASE_YOU%", String.valueOf(LanguageManager.getLanguageMessage("Scoreboard.Bases.Inside")));
    } else {
      formattedLine = StringUtils.replace(formattedLine, "%BASE_YOU%", "");
    }
    if(line.contains("%FORMATTED_POINTS%")) {
      StringBuilder points = new StringBuilder();
      String got = LanguageManager.getLanguageMessage("Scoreboard.Mode." + arena.getMode().toString() + ".Got");
      String missing = LanguageManager.getLanguageMessage("Scoreboard.Mode." + arena.getMode().toString() + ".Missing");
      for(int i = 0; i + 1 <= arena.getOption(ArenaOption.MODE_VALUE); i++) {
        if(i >= base.getPoints()) {
          points.append(arena.getMode() == Arena.Mode.HEARTS ? got : missing);
        } else {
          points.append(arena.getMode() == Arena.Mode.HEARTS ? missing : got);
        }
      }
      formattedLine = StringUtils.replace(formattedLine, "%FORMATTED_POINTS%", points.toString());
    }
    formattedLine = chatManager.colorRawMessage(formattedLine);
    if(!baseYou)
      cachedBaseFormat.add(formattedLine);
    return formattedLine;
  }

  public void resetBaseCache() {
    cachedBaseFormat.clear();
  }

}
