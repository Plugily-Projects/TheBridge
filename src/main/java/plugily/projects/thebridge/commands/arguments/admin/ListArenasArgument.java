package plugily.projects.thebridge.commands.arguments.admin;

import org.bukkit.command.CommandSender;
import plugily.projects.thebridge.arena.Arena;
import plugily.projects.thebridge.arena.ArenaRegistry;
import plugily.projects.thebridge.commands.arguments.ArgumentsRegistry;
import plugily.projects.thebridge.commands.arguments.data.CommandArgument;
import plugily.projects.thebridge.commands.arguments.data.LabelData;
import plugily.projects.thebridge.commands.arguments.data.LabeledCommandArgument;
import plugily.projects.thebridge.handlers.ChatManager;

/**
 * @author Tigerpanzer_02 & 2Wild4You
 * <p>
 * Created at 31.10.2020
 */
public class ListArenasArgument {

  public ListArenasArgument(ArgumentsRegistry registry, ChatManager chatManager) {
    registry.mapArgument("thebridgeadmin", new LabeledCommandArgument("list", "thebridge.admin.list", CommandArgument.ExecutorType.BOTH,
      new LabelData("/tba list", "/tba list", "&7Shows list with all loaded arenas\n&6Permission: &7thebridge.admin.list")) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        sender.sendMessage(chatManager.colorMessage("Commands.Admin-Commands.List-Command.Header"));
        int i = 0;
        for (Arena arena : ArenaRegistry.getArenas()) {
          sender.sendMessage(chatManager.colorMessage("Commands.Admin-Commands.List-Command.Format").replace("%arena%", arena.getId())
            .replace("%status%", arena.getArenaState().getFormattedName()).replace("%players%", String.valueOf(arena.getPlayers().size()))
            .replace("%maxplayers%", String.valueOf(arena.getMaximumPlayers())));
          i++;
        }
        if (i == 0) {
          sender.sendMessage(chatManager.colorMessage("Commands.Admin-Commands.List-Command.No-Arenas"));
        }
      }
    });
  }

}
