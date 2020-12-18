package plugily.projects.thebridge.commands.arguments.admin;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import plugily.projects.thebridge.commands.arguments.ArgumentsRegistry;
import plugily.projects.thebridge.commands.arguments.data.CommandArgument;
import plugily.projects.thebridge.commands.arguments.data.LabelData;
import plugily.projects.thebridge.commands.arguments.data.LabeledCommandArgument;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 18.12.2020
 */
public class LocationWandArgument {

  public LocationWandArgument(ArgumentsRegistry registry) {
    registry.mapArgument("thebridgeadmin", new LabeledCommandArgument("locwand", "thebridge.admin.locwand", CommandArgument.ExecutorType.PLAYER,
      new LabelData("/tba locwand", "/tba locwand",
        "&7Get location wand to setup locations\n&6Permission: &7thebridge.admin.locwand")) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        registry.getPlugin().getCuboidSelector().giveSelectorWand((Player) sender);
      }
    });
  }

}
