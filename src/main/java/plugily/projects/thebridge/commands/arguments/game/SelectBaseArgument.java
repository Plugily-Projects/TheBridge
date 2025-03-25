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
import plugily.projects.minigamesbox.api.arena.IArenaState;
import plugily.projects.minigamesbox.classic.commands.arguments.data.CommandArgument;
import plugily.projects.thebridge.arena.Arena;
import plugily.projects.thebridge.commands.arguments.ArgumentsRegistry;

/**
 * @author Tigerpanzer_02
 * <p>Created at 27.12.2020
 */
public class SelectBaseArgument {

  public SelectBaseArgument(ArgumentsRegistry registry) {
    registry.mapArgument(
        "thebridge",
        new CommandArgument(
            "selectbase", "thebridge.command.selectbase", CommandArgument.ExecutorType.PLAYER) {
          @Override
          public void execute(CommandSender sender, String[] args) {
            if(!registry.getPlugin().getBukkitHelper().checkIsInGameInstance((Player) sender)) {
              return;
            }
            Arena arena = (Arena) registry.getPlugin().getArenaRegistry().getArena((Player) sender);
            if(arena != null) {
              if(arena.getArenaState() == IArenaState.WAITING_FOR_PLAYERS
                  || arena.getArenaState() == IArenaState.STARTING || arena.getArenaState() == IArenaState.FULL_GAME)
                arena.getPlugin().getBaseMenuHandler().createMenu((Player) sender, arena);
            }
          }
        });
  }
}
