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

package plugily.projects.thebridge.arena;

import org.bukkit.plugin.java.JavaPlugin;
import plugily.projects.thebridge.Main;

/**
 * @author Tigerpanzer_02, 2Wild4You
 * <p>
 * Created at 31.10.2020
 */
public enum ArenaState {
  WAITING_FOR_PLAYERS("Waiting"), STARTING("Starting"), IN_GAME("Playing"), ENDING("Ending"), RESTARTING("Restarting");

  private final String formattedName;
  private final String placeholder;

  ArenaState(String formattedName) {
    this.formattedName = formattedName;
    this.placeholder = JavaPlugin.getPlugin(Main.class).getChatManager().colorMessage("Placeholders.Game-States." + formattedName);
  }

  public String getPlaceholder() {
    return placeholder;
  }

  public String getFormattedName() {
    return formattedName;
  }
}
