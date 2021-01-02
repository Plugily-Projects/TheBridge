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
