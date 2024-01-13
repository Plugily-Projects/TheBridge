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

package plugily.projects.thebridge.arena.states;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import plugily.projects.minigamesbox.classic.arena.PluginArena;
import plugily.projects.minigamesbox.classic.arena.states.PluginInGameState;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.handlers.language.TitleBuilder;
import plugily.projects.thebridge.api.events.game.TBRoundStartEvent;
import plugily.projects.thebridge.arena.Arena;
import plugily.projects.thebridge.arena.ArenaUtils;
import plugily.projects.thebridge.arena.base.Base;

import java.util.logging.Level;

/**
 * @author Plajer
 *     <p>Created at 03.06.2019
 */
public class InGameState extends PluginInGameState {

  @Override
  public void handleCall(PluginArena arena) {
    super.handleCall(arena);
    Arena pluginArena = (Arena) getPlugin().getArenaRegistry().getArena(arena.getId());
    if (pluginArena == null) {
      return;
    }
    if (arena.getTimer() <= 0) {
      Base highestValue = pluginArena.getBases().get(0);
      for (Base base : pluginArena.getBases()) {
        if (highestValue.getPoints() < base.getPoints()) {
          highestValue = base;
        }
      }
      pluginArena.setWinner(highestValue);

      getPlugin().getArenaManager().stopGame(false, arena);
      getPlugin().getDebugger().debug(Level.INFO, "[{0}] Game stopped due to timer ended", arena.getId());
    }
    if (arena.getTimer() == 30 || arena.getTimer() == 60 || arena.getTimer() == 120) {
      new TitleBuilder("IN_GAME_MESSAGES_ARENA_TIME_LEFT").asKey().arena(pluginArena).sendArena();
    }

    if (pluginArena.getResetRound() > 0) {
      new TitleBuilder("IN_GAME_MESSAGES_ARENA_BLOCKED_TITLE")
          .asKey()
          .arena(pluginArena)
          .integer(pluginArena.getResetRound())
          .sendArena();
      pluginArena.setResetRound(pluginArena.getResetRound() - 1);
      }
      if (pluginArena.getResetRound() == 0) {
        new MessageBuilder("IN_GAME_MESSAGES_ARENA_BLOCKED_RUN")
            .asKey()
            .arena(pluginArena)
            .sendArena();
        Bukkit.getPluginManager().callEvent(new TBRoundStartEvent(pluginArena));
        for (Base base : pluginArena.getBases()) {
          base.removeCage();
        }
        pluginArena.setResetRound(-999);
      }

    // no players - stop game
    if (pluginArena.getPlayersLeft().isEmpty()) {
      getPlugin().getArenaManager().stopGame(false, pluginArena);
      getPlugin().getDebugger().debug(Level.INFO, "[{0}] Game stopped due to no players left", arena.getId());
      getPlugin().getDebugger().debug(Level.INFO, "[{0}] Class InGameState, pluginArena.getPlayersLeft().isEmpty() is true");
    } else {
      // winner check
      for (Base base : pluginArena.getBases()) {
        if (base.getPoints() >= pluginArena.getArenaOption("MODE_VALUE")) {
          pluginArena.setWinner(base);
          if (pluginArena.getMode() == Arena.Mode.POINTS) {
            for (Player player : pluginArena.getPlayers()) {
              new TitleBuilder("IN_GAME_MESSAGES_GAME_END_PLACEHOLDERS_BASE_POINTS")
                  .asKey()
                  .arena(pluginArena)
                  .player(player)
                  .sendPlayer();
              if (base.getPlayers().contains(player)) {
                pluginArena.setWinner(pluginArena.getBase(player));
              }
            }
            getPlugin().getArenaManager().stopGame(false, pluginArena);
            getPlugin().getDebugger().debug(Level.INFO, "[{0}] Game stopped due {1} having {2} points.", arena.getId(), base.getColor(), base.getPoints());
            getPlugin().getDebugger().debug(Level.INFO, "[{0}] Class InGameState, pluginArena.getMode() == Arena.Mode.POINTS");
            break;
          }
        }
      }
      if (pluginArena.getMode() == Arena.Mode.HEARTS) {
        if (pluginArena.getOut()
            >= pluginArena.getBases().size() - 1 - ArenaUtils.emptyBases(pluginArena)) {
          for (Player player : pluginArena.getPlayers()) {
            new TitleBuilder("IN_GAME_MESSAGES_GAME_END_PLACEHOLDERS_BASE_HEARTS")
                .asKey()
                .arena(pluginArena)
                .player(player)
                .sendPlayer();
            if (!pluginArena.isDeathPlayer(player)) {
              pluginArena.setWinner(pluginArena.getBase(player));
            }
          }
          getPlugin().getArenaManager().stopGame(false, pluginArena);
          getPlugin().getDebugger().debug(Level.INFO, "[{0}] Game stopped due {1} being winner.", arena.getId(), pluginArena.getWinner().getColor());
          getPlugin().getDebugger().debug(Level.INFO, "[{0}] Class InGameState, pluginArena.getMode() == Arena.Mode.HEARTS");
        }
      }
    }
  }
}
