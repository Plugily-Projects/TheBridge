package plugily.projects.thebridge.arena;

/**
 * @author Tigerpanzer_02 & 2Wild4You
 * <p>
 * Created at 31.10.2020
 */
public enum ArenaState {
  WAITING_FOR_PLAYERS("Waiting"), STARTING("Starting"), IN_GAME("Playing"), ENDING("Finishing"), RESTARTING("Restarting");

  String formattedName;

  ArenaState(String formattedName) {
    this.formattedName = formattedName;
  }

  public String getFormattedName() {
    return formattedName;
  }
}
