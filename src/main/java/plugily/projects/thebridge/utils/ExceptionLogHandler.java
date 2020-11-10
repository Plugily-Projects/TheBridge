package plugily.projects.thebridge.utils;

import plugily.projects.thebridge.Main;
import plugily.projects.thebridge.utils.services.exception.ReportedException;

import org.bukkit.Bukkit;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * @author Tigerpanzer_02 & 2Wild4You
 * <p>
 * Created at 31.10.2020
 */
public class ExceptionLogHandler extends Handler {

  //these classes if found in stacktraces won't be reported
  //to the Error Service
  private final List<String> blacklistedClasses = Arrays.asList("plugily.projects.thebridge.user.data.MysqlManager", "plugily.projects.thebridge.plajerlair.commonsbox.database.MysqlDatabase");
  private final Main plugin;

  public ExceptionLogHandler(Main plugin) {
    this.plugin = plugin;
    Bukkit.getLogger().addHandler(this);
  }

  @Override
  public void close() throws SecurityException {
  }

  @Override
  public void flush() {
  }

  @Override
  public void publish(LogRecord record) {
    Throwable throwable = record.getThrown();
    if (!(throwable instanceof Exception) || !throwable.getClass().getSimpleName().contains("Exception")) {
      return;
    }
    /*if (throwable.getStackTrace().length == 0
        || throwable.getCause() != null ? !throwable.getCause().getStackTrace()[0].getClassName().contains("pl.plajer.TheBridge")
        : !throwable.getStackTrace()[0].getClassName().contains("pl.plajer.TheBridge")) {
      return;
    }*/
    if (throwable.getStackTrace().length <= 0 || (throwable.getCause() != null &&
      !throwable.getCause().getStackTrace()[0].getClassName().contains("plugily.projects.thebridge"))) {
      return;
    }
    if (!throwable.getStackTrace()[0].getClassName().contains("plugily.projects.thebridge") || containsBlacklistedClass(throwable)) {
      return;
    }
    new ReportedException(plugin, (Exception) throwable);
    record.setThrown(null);
    record.setMessage("[thebridge] We have found a bug in the code. Contact us at our official discord server (Invite link: https://discordapp.com/invite/UXzUdTP) with the following error given" +
      " above!");
  }

  private boolean containsBlacklistedClass(Throwable throwable) {
    for (StackTraceElement element : throwable.getStackTrace()) {
      for (String blacklist : blacklistedClasses) {
        if (element.getClassName().contains(blacklist)) {
          return true;
        }
      }
    }
    return false;
  }

}
