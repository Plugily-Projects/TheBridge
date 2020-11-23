package plugily.projects.thebridge.commands.arguments.admin.arena;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import plugily.projects.thebridge.arena.Arena;
import plugily.projects.thebridge.arena.ArenaRegistry;
import plugily.projects.thebridge.arena.ArenaState;
import plugily.projects.thebridge.commands.arguments.ArgumentsRegistry;
import plugily.projects.thebridge.commands.arguments.data.CommandArgument;
import plugily.projects.thebridge.commands.arguments.data.LabelData;
import plugily.projects.thebridge.commands.arguments.data.LabeledCommandArgument;
import plugily.projects.thebridge.handlers.ChatManager;
import plugily.projects.thebridge.utils.Utils;

/**
 * @author Tigerpanzer_02 & 2Wild4You
 * <p>
 * Created at 31.10.2020
 */
public class ForceStartArgument {

  public ForceStartArgument(ArgumentsRegistry registry, ChatManager chatManager) {
    registry.mapArgument("thebridgeadmin", new LabeledCommandArgument("forcestart", "thebridge.admin.forcestart", CommandArgument.ExecutorType.PLAYER,
      new LabelData("/tba forcestart", "/tba forcestart", "&7Force starts arena you're in\n&6Permission: &7thebridge.admin.forcestart")) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        if (!Utils.checkIsInGameInstance((Player) sender)) {
          return;
        }

        Arena arena = ArenaRegistry.getArena((Player) sender);
        if (arena.getPlayers().size() < 2) {
          chatManager.broadcast(arena, chatManager.formatMessage(arena, chatManager.colorMessage("In-Game.Messages.Lobby-Messages.Waiting-For-Players"), arena.getMinimumPlayers()));
          return;
        }
        if (arena.getArenaState() == ArenaState.WAITING_FOR_PLAYERS || arena.getArenaState() == ArenaState.STARTING) {
          arena.setArenaState(ArenaState.STARTING);
          arena.setForceStart(true);
          arena.setTimer(0);
          for (Player player : ArenaRegistry.getArena((Player) sender).getPlayers()) {
            player.sendMessage(chatManager.getPrefix() + chatManager.colorMessage("In-Game.Messages.Admin-Messages.Set-Starting-In-To-0"));
          }
        }
      }
    });
  }

}
