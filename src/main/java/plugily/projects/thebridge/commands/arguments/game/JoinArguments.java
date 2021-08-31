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

package plugily.projects.thebridge.commands.arguments.game;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import plugily.projects.thebridge.ConfigPreferences;
import plugily.projects.thebridge.arena.Arena;
import plugily.projects.thebridge.arena.ArenaManager;
import plugily.projects.thebridge.arena.ArenaRegistry;
import plugily.projects.thebridge.arena.ArenaState;
import plugily.projects.thebridge.commands.arguments.ArgumentsRegistry;
import plugily.projects.thebridge.commands.arguments.data.CommandArgument;
import plugily.projects.thebridge.handlers.ChatManager;
import plugily.projects.thebridge.utils.Utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * @author Tigerpanzer_02, 2Wild4You
 * <p>
 * Created at 31.10.2020
 */
public class JoinArguments {

  public JoinArguments(ArgumentsRegistry registry, ChatManager chatManager) {
    //join argument
    registry.mapArgument("thebridge", new CommandArgument("join", "", CommandArgument.ExecutorType.PLAYER) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        if(args.length == 1) {
          sender.sendMessage(chatManager.getPrefix() + chatManager.colorMessage("Commands.Type-Arena-Name"));
          return;
        }
        if(!ArenaRegistry.getArenas().isEmpty() && args[1].equalsIgnoreCase("maxplayers") && ArenaRegistry.getArena("maxplayers") == null) {
          List<Arena> arenaList = new ArrayList<>();
          Map<Arena, Integer> arenas = new HashMap<>();
          for(Arena arena : ArenaRegistry.getArenas()) {
            if(!(arena.getArenaState() == ArenaState.WAITING_FOR_PLAYERS || arena.getArenaState() == ArenaState.STARTING)) continue;
            arenas.put(arena, arena.getPlayers().size());
          }
          if(args.length == 3) {
            if(!Utils.isInteger(args[2])) {
              sender.sendMessage(registry.getPlugin().getChatManager().getPrefix() + registry.getPlugin().getChatManager().colorMessage("Commands.Wrong-Usage")
                  .replace("%correct%", "/tb join maxplayers <teamSize>"));
              return;
            }
            arenas.clear();
            for(Arena arena : ArenaRegistry.getArenas()) {
              if(!(arena.getArenaState() == ArenaState.WAITING_FOR_PLAYERS || arena.getArenaState() == ArenaState.STARTING)) continue;
              if(arena.getBases().get(0).getMaximumSize() == Integer.parseInt(args[2])) {
                arenas.put(arena, arena.getPlayers().size());
                arenaList.add(arena);
              }
            }
          }
          if(arenas.isEmpty()) {
            sender.sendMessage(registry.getPlugin().getChatManager().getPrefix() + registry.getPlugin().getChatManager().colorMessage("Commands.No-Free-Arenas"));
            return;
          }
          if(ArenaRegistry.getArenaPlayersOnline() == 0) {
            if(!arenaList.isEmpty()) {
              Arena arena = arenaList.get(ThreadLocalRandom.current().nextInt(arenaList.size()));
              ArenaManager.joinAttempt((Player) sender, arena);
              return;
            }
            ArenaManager.joinAttempt((Player) sender, ArenaRegistry.getArenas().get(ThreadLocalRandom.current().nextInt(ArenaRegistry.getArenas().size())));
            return;
          }

          LinkedHashMap<Arena, Integer> orderedArenas = new LinkedHashMap<>();
          arenas.entrySet()
              .stream()
              .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
              .forEachOrdered(x -> orderedArenas.put(x.getKey(), x.getValue()));

          if(!orderedArenas.isEmpty()) {
            Arena arena = orderedArenas.keySet().stream().findFirst().get();
            ArenaManager.joinAttempt((Player) sender, arena);
            return;
          }
          return;
        }
        for(Arena arena : ArenaRegistry.getArenas()) {
          if(args[1].equalsIgnoreCase(arena.getId())) {
            ArenaManager.joinAttempt((Player) sender, arena);
            return;
          }
        }
        sender.sendMessage(chatManager.getPrefix() + chatManager.colorMessage("Commands.No-Arena-Like-That"));
      }
    });

    //random join argument, register only for multi arena
    if(!registry.getPlugin().getConfigPreferences().getOption(ConfigPreferences.Option.BUNGEE_ENABLED)) {
      registry.mapArgument("thebridge", new CommandArgument("randomjoin", "", CommandArgument.ExecutorType.PLAYER) {
        @Override
        public void execute(CommandSender sender, String[] args) {
          if(args.length == 2) {
            if(!Utils.isInteger(args[1])) {
              sender.sendMessage(registry.getPlugin().getChatManager().getPrefix() + registry.getPlugin().getChatManager().colorMessage("Commands.Wrong-Usage")
                  .replace("%correct%", "/tb randomjoin <teamSize>"));
              return;
            }
            randomJoin(Integer.parseInt(args[1]), sender, chatManager);
            return;
          }
          randomJoin(0, sender, chatManager);
        }
      });
    }
  }

  private void randomJoin(int teamSize, CommandSender sender, ChatManager chatManager) {
    //check starting arenas -> random
    List<Arena> arenas = ArenaRegistry.getArenas().stream().filter(arena -> arena.getArenaState() == ArenaState.STARTING && arena.getPlayers().size() < arena.getMaximumPlayers()).collect(Collectors.toList());
    if(teamSize != 0) {
      arenas = ArenaRegistry.getArenas().stream().filter(arena -> arena.getBases().get(0).getMaximumSize() == teamSize).filter(arena -> arena.getArenaState() == ArenaState.STARTING && arena.getPlayers().size() < arena.getMaximumPlayers()).collect(Collectors.toList());
    }
    if(!arenas.isEmpty()) {
      Arena arena = arenas.get(ThreadLocalRandom.current().nextInt(arenas.size()));
      ArenaManager.joinAttempt((Player) sender, arena);
      return;
    }
    //check waiting arenas -> random
    arenas = ArenaRegistry.getArenas().stream().filter(arena -> (arena.getArenaState() == ArenaState.WAITING_FOR_PLAYERS || arena.getArenaState() == ArenaState.STARTING)
        && arena.getPlayers().size() < arena.getMaximumPlayers()).collect(Collectors.toList());
    if(teamSize != 0) {
      arenas = ArenaRegistry.getArenas().stream().filter(arena -> arena.getBases().get(0).getMaximumSize() == teamSize).filter(arena -> (arena.getArenaState() == ArenaState.WAITING_FOR_PLAYERS || arena.getArenaState() == ArenaState.STARTING)
          && arena.getPlayers().size() < arena.getMaximumPlayers()).collect(Collectors.toList());
    }
    if(!arenas.isEmpty()) {
      Arena arena = arenas.get(ThreadLocalRandom.current().nextInt(arenas.size()));
      ArenaManager.joinAttempt((Player) sender, arena);
      return;
    }
    sender.sendMessage(chatManager.getPrefix() + chatManager.colorMessage("Commands.No-Free-Arenas"));
  }

}
