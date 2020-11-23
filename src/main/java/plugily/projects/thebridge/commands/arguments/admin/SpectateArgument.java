package plugily.projects.thebridge.commands.arguments.admin;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import plugily.projects.thebridge.commands.arguments.ArgumentsRegistry;
import plugily.projects.thebridge.commands.arguments.data.CommandArgument;
import plugily.projects.thebridge.commands.arguments.data.LabelData;
import plugily.projects.thebridge.commands.arguments.data.LabeledCommandArgument;
import plugily.projects.thebridge.user.User;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 30.06.2020
 */

public class SpectateArgument {

  public SpectateArgument(ArgumentsRegistry registry) {
    registry.mapArgument("thebridgeadmin", new LabeledCommandArgument("spectate", "thebridge.admin.spectate", CommandArgument.ExecutorType.PLAYER,
      new LabelData("/tba spectate", "/tba spectate", "&7Enable/Disable permanent spectator mode\n&6Permission: &7thebridge.admin.spectate")) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        User user = registry.getPlugin().getUserManager().getUser((Player) sender);
        user.setPermanentSpectator(!user.isPermanentSpectator());
      }
    });
  }

}
