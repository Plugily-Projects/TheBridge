package plugily.projects.thebridge.commands.arguments.admin.arena;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import pl.plajerlair.commonsbox.minecraft.configuration.ConfigUtils;
import plugily.projects.thebridge.arena.Arena;
import plugily.projects.thebridge.arena.ArenaManager;
import plugily.projects.thebridge.arena.ArenaRegistry;
import plugily.projects.thebridge.commands.arguments.ArgumentsRegistry;
import plugily.projects.thebridge.commands.arguments.data.CommandArgument;
import plugily.projects.thebridge.commands.arguments.data.LabelData;
import plugily.projects.thebridge.commands.arguments.data.LabeledCommandArgument;
import plugily.projects.thebridge.handlers.ChatManager;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Tigerpanzer_02 & 2Wild4You
 * <p>
 * Created at 31.10.2020
 */
public class DeleteArgument {

  private final Set<CommandSender> confirmations = new HashSet<>();

  public DeleteArgument(ArgumentsRegistry registry, ChatManager chatManager) {
    registry.mapArgument("thebridgeadmin", new LabeledCommandArgument("delete", "thebridge.admin.delete", CommandArgument.ExecutorType.PLAYER,
      new LabelData("/tba delete &6<arena>", "/tba delete <arena>",
        "&7Deletes specified arena\n&6Permission: &7thebridge.admin.delete")) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        if (args.length == 1) {
          sender.sendMessage(chatManager.getPrefix() + chatManager.colorMessage("Commands.Type-Arena-Name"));
          return;
        }
        Arena arena = ArenaRegistry.getArena(args[1]);
        if (arena == null) {
          sender.sendMessage(chatManager.getPrefix() + chatManager.colorMessage("Commands.No-Arena-Like-That"));
          return;
        }
        if (!confirmations.contains(sender)) {
          confirmations.add(sender);
          Bukkit.getScheduler().runTaskLater(registry.getPlugin(), () -> confirmations.remove(sender), 20 * 10);
          sender.sendMessage(chatManager.getPrefix() + chatManager.colorRawMessage("&cAre you sure you want to do this action? Type the command again &6within 10 seconds &cto confirm!"));
          return;
        }
        confirmations.remove(sender);
        ArenaManager.stopGame(true, arena);
        FileConfiguration config = ConfigUtils.getConfig(registry.getPlugin(), "arenas");
        config.set("instances." + args[1], null);
        ConfigUtils.saveConfig(registry.getPlugin(), config, "arenas");
        ArenaRegistry.unregisterArena(arena);
        sender.sendMessage(chatManager.getPrefix() + chatManager.colorMessage("Commands.Removed-Game-Instance"));
      }
    });
  }

}
