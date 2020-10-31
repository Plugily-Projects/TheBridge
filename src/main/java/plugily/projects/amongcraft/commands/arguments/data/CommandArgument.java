package plugily.projects.amongcraft.commands.arguments.data;

import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

/**
 * @author Plajer
 * <p>
 * Created at 31.10.2020
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
