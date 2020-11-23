package plugily.projects.thebridge.commands.arguments.game;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import plugily.projects.thebridge.ConfigPreferences;
import plugily.projects.thebridge.arena.Arena;
import plugily.projects.thebridge.arena.ArenaManager;
import plugily.projects.thebridge.arena.ArenaRegistry;
import plugily.projects.thebridge.commands.arguments.ArgumentsRegistry;
import plugily.projects.thebridge.commands.arguments.data.CommandArgument;
import plugily.projects.thebridge.handlers.ChatManager;
import plugily.projects.thebridge.utils.Debugger;
import plugily.projects.thebridge.utils.Utils;

/**
 * @author Tigerpanzer_02 & 2Wild4You
 * <p>
 * Created at 31.10.2020
 */
public class LeaveArgument {

  public LeaveArgument(ArgumentsRegistry registry, ChatManager chatManager) {
    registry.mapArgument("thebridge", new CommandArgument("leave", "", CommandArgument.ExecutorType.PLAYER) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        if (!registry.getPlugin().getConfig().getBoolean("Disable-Leave-Command", false)) {
          Player player = (Player) sender;
          if (!Utils.checkIsInGameInstance((Player) sender)) {
            return;
          }
          player.sendMessage(chatManager.getPrefix() + chatManager.colorMessage("Commands.Teleported-To-The-Lobby", player));
          if (registry.getPlugin().getConfigPreferences().getOption(ConfigPreferences.Option.BUNGEE_ENABLED)) {
            registry.getPlugin().getBungeeManager().connectToHub(player);
            Debugger.debug("{0} was teleported to the Hub server", player.getName());
            return;
          }
          Arena arena = ArenaRegistry.getArena(player);
          ArenaManager.leaveAttempt(player, arena);
          Debugger.debug("{0} has left the arena {1}! Teleported to end location.", player.getName(), arena.getId());
        }
      }
    });
  }

}
