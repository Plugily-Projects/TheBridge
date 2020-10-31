package plugily.projects.amongcraft.commands.completion;

import java.util.List;

/**
 * @author Plajer
 * <p>
 * Created at 31.10.2020
 */
public class CompletableArgument {

  private final String mainCommand;
  private final String argument;
  private final List<String> completions;

  public CompletableArgument(String mainCommand, String argument, List<String> completions) {
    this.mainCommand = mainCommand;
    this.argument = argument;
    this.completions = completions;
  }

  /**
   * @return main command of the argument
   */
  public String getMainCommand() {
    return mainCommand;
  }

  /**
   * @return argument name
   */
  public String getArgument() {
    return argument;
  }

  /**
   * @return all possible completions for this command argument
   */
  public List<String> getCompletions() {
    return completions;
  }

}
