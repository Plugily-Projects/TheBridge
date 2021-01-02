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

package plugily.projects.thebridge.commands.completion;

import plugily.projects.thebridge.arena.Arena;
import plugily.projects.thebridge.arena.ArenaRegistry;
import plugily.projects.thebridge.commands.arguments.ArgumentsRegistry;
import plugily.projects.thebridge.commands.arguments.data.CommandArgument;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Tigerpanzer_02, 2Wild4You
 * <p>
 * Created at 31.10.2020
 */
public class TabCompletion implements TabCompleter {

  private final List<CompletableArgument> registeredCompletions = new ArrayList<>();
  private final ArgumentsRegistry registry;

  public TabCompletion(ArgumentsRegistry registry) {
    this.registry = registry;
  }

  public void registerCompletion(CompletableArgument completion) {
    registeredCompletions.add(completion);
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
    List<String> completionList = new ArrayList<>();
    List<String> commands = new ArrayList<>();
    String partOfCommand = null;

    if (cmd.getName().equalsIgnoreCase("thebridgeadmin")) {
      if (args.length == 1) {
        commands.addAll(registry.getMappedArguments().get(cmd.getName().toLowerCase()).stream().map(CommandArgument::getArgumentName)
          .collect(Collectors.toList()));
        partOfCommand = args[0];
      } else if (args.length == 2 && args[0].equalsIgnoreCase("delete")) {
        commands.addAll(ArenaRegistry.getArenas().stream().map(Arena::getId).collect(Collectors.toList()));
        partOfCommand = args[1];
      }
    }

    if (cmd.getName().equalsIgnoreCase("thebridge")) {
      if (args.length == 2 && args[0].equalsIgnoreCase("join")) {
        commands.addAll(ArenaRegistry.getArenas().stream().map(Arena::getId).collect(Collectors.toList()));
        partOfCommand = args[1];
      } else if (args.length == 1) {
        commands.addAll(registry.getMappedArguments().get(cmd.getName().toLowerCase()).stream().map(CommandArgument::getArgumentName)
          .collect(Collectors.toList()));
        partOfCommand = args[0];
      }
    }

    // Completes the player names
    if (commands.isEmpty() || partOfCommand == null) {
      for (CompletableArgument completion : registeredCompletions) {
        if (!cmd.getName().equalsIgnoreCase(completion.getMainCommand()) || !completion.getArgument().equalsIgnoreCase(args[0])) {
          continue;
        }
        return completion.getCompletions();
      }

      return null;
    }

    StringUtil.copyPartialMatches(partOfCommand, commands, completionList);
    Collections.sort(completionList);
    return completionList;
  }
}
