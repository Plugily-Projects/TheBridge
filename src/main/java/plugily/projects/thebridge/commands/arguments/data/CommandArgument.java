/*
 * thebridge - Jump into the portal of your opponent and collect points to win!
 * Copyright (C) 2020  Plugily Projects - maintained by Tigerpanzer_02, 2Wild4You and contributors
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
 */

package plugily.projects.thebridge.commands.arguments.data;

import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

/**
 * @author Tigerpanzer, 2Wild4You
 * <p>
 * Created at 11.01.2019
 */
public class CommandArgument {

  private final String argumentName;
  private final List<String> permissions;
  private final ExecutorType validExecutors;

  public CommandArgument(String argumentName, String permissions, ExecutorType validExecutors) {
    this.argumentName = argumentName;
    this.permissions = Collections.singletonList(permissions);
    this.validExecutors = validExecutors;
  }

  public CommandArgument(String argumentName, List<String> permissions, ExecutorType validExecutors) {
    this.argumentName = argumentName;
    this.permissions = permissions;
    this.validExecutors = validExecutors;
  }

  public String getArgumentName() {
    return argumentName;
  }

  public List<String> getPermissions() {
    return permissions;
  }

  public ExecutorType getValidExecutors() {
    return validExecutors;
  }

  public void execute(CommandSender sender, String[] args) {
  }

  public enum ExecutorType {
    BOTH, CONSOLE, PLAYER
  }

}
