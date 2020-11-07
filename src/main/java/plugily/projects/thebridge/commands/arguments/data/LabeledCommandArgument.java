package plugily.projects.thebridge.commands.arguments.data;

import java.util.List;

/**
 * @author Tigerpanzer_02 & 2Wild4You
 * <p>
 * Created at 31.10.2020
 */
public class LabeledCommandArgument extends CommandArgument {

  private final LabelData labelData;

  public LabeledCommandArgument(String argumentName, List<String> permissions, ExecutorType validExecutors, LabelData labelData) {
    super(argumentName, permissions, validExecutors);
    this.labelData = labelData;
  }

  public LabeledCommandArgument(String argumentName, String permissions, ExecutorType validExecutors, LabelData labelData) {
    super(argumentName, permissions, validExecutors);
    this.labelData = labelData;
  }

  /**
   * @return label data of command (description and usages of command)
   */
  public LabelData getLabelData() {
    return labelData;
  }

}
