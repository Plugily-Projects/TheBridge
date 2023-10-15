package plugily.projects.thebridge.boot;

import plugily.projects.minigamesbox.classic.handlers.language.Message;
import plugily.projects.minigamesbox.classic.handlers.language.MessageManager;
import plugily.projects.minigamesbox.classic.utils.services.locale.Locale;
import plugily.projects.minigamesbox.classic.utils.services.locale.LocaleRegistry;
import plugily.projects.thebridge.Main;

import java.util.Arrays;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 15.10.2022
 */
public class MessageInitializer {
  private final Main plugin;

  public MessageInitializer(Main plugin) {
    this.plugin = plugin;
  }

  public void registerMessages() {
    getMessageManager().registerMessage("", new Message("", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_GAME_END_PLACEHOLDERS_BASE_HEARTS", new Message("In-Game.Messages.Game-End.Placeholders.Base.HEARTS", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_GAME_END_PLACEHOLDERS_BASE_POINTS", new Message("In-Game.Messages.Game-End.Placeholders.Base.POINTS", ""));
    getMessageManager().registerMessage("SCOREBOARD_BASES_FORMAT", new Message("Scoreboard.Bases.Format", ""));
    getMessageManager().registerMessage("SCOREBOARD_BASES_NOT_INSIDE", new Message("Scoreboard.Bases.Not-Inside", ""));
    getMessageManager().registerMessage("SCOREBOARD_BASES_INSIDE", new Message("Scoreboard.Bases.Inside", ""));

    getMessageManager().registerMessage("IN_GAME_MESSAGES_ARENA_COOLDOWN", new Message("In-Game.Messages.Arena.Cooldown", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_ARENA_DAMAGE", new Message("In-Game.Messages.Arena.Damage", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_ARENA_DEATH", new Message("In-Game.Messages.Arena.Death", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_ARENA_KILLED", new Message("In-Game.Messages.Arena.Killed", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_ARENA_BUILD_BREAK", new Message("In-Game.Messages.Arena.Build-Break", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_ARENA_TIME_LEFT", new Message("In-Game.Messages.Arena.Time-Left", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_ARENA_PORTAL_OWN", new Message("In-Game.Messages.Arena.Portal.Own", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_ARENA_PORTAL_OPPONENT", new Message("In-Game.Messages.Arena.Portal.Opponent", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_ARENA_PORTAL_SCORED_TITLE", new Message("In-Game.Messages.Arena.Portal.Scored.Title", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_ARENA_PORTAL_HOLOGRAM", new Message("In-Game.Messages.Arena.Portal.Hologram", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_ARENA_PORTAL_OUT", new Message("In-Game.Messages.Arena.Portal.Out", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_ARENA_BLOCKED_RESET", new Message("In-Game.Messages.Arena.Blocked.Reset", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_ARENA_BLOCKED_RUN", new Message("In-Game.Messages.Arena.Blocked.Run", ""));
    getMessageManager().registerMessage("IN_GAME_MESSAGES_ARENA_BLOCKED_TITLE", new Message("In-Game.Messages.Arena.Blocked.Title", ""));

    getMessageManager().registerMessage("BASES_TEAM_FULL", new Message("Bases.Team.Full", ""));
    getMessageManager().registerMessage("BASES_TEAM_EMPTY", new Message("Bases.Team.Empty", ""));
    getMessageManager().registerMessage("BASES_TEAM_INSIDE", new Message("Bases.Team.Inside", ""));
    getMessageManager().registerMessage("BASES_TEAM_NAME", new Message("Bases.Team.Name", ""));
    getMessageManager().registerMessage("BASES_TEAM_MENU", new Message("Bases.Team.Menu", ""));
    getMessageManager().registerMessage("BASES_TEAM_CHOOSE", new Message("Bases.Team.Choose", ""));
    getMessageManager().registerMessage("BASES_TEAM_MEMBER", new Message("Bases.Team.Member", ""));
    getMessageManager().registerMessage("BASES_COLORS", new Message("Bases.Colors", ""));

    getMessageManager().registerMessage("LEADERBOARD_STATISTICS_KILLS", new Message("Leaderboard.Statistics.Kills", ""));
    getMessageManager().registerMessage("LEADERBOARD_STATISTICS_DEATHS", new Message("Leaderboard.Statistics.Deaths", ""));
    getMessageManager().registerMessage("LEADERBOARD_STATISTICS_SCORED_POINTS", new Message("Leaderboard.Statistics.Score", ""));

  }

  private MessageManager getMessageManager() {
    return plugin.getMessageManager();
  }

}
