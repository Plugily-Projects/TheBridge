package plugily.projects.amongcraft.commands.completion;

import plugily.projects.amongcraft.commands.arguments.ArgumentsRegistry;
import plugily.projects.amongcraft.commands.arguments.data.CommandArgument;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Plajer
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

    if (cmd.getName().equalsIgnoreCase("amongcraftadmin")) {
      if (args.length == 1) {
        commands.addAll(registry.getMappedArguments().get(cmd.getName().toLowerCase()).stream().map(CommandArgument::getArgumentName)
          .collect(Collectors.toList()));
        partOfCommand = args[0];
      } else if (args.length == 2 && args[0].equalsIgnoreCase("delete")) {
        commands.addAll(ArenaRegistry.getArenas().stream().map(Arena::getId).collect(Collectors.toList()));
        partOfCommand = args[1];
      }
    }

    if (cmd.getName().equalsIgnoreCase("amongcraft")) {
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
