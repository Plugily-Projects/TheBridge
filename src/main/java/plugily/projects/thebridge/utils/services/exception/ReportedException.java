package plugily.projects.thebridge.utils.services.exception;

import plugily.projects.thebridge.utils.services.ServiceRegistry;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.logging.Level;

/**
 * Create reported exception with data sent to plugily.xyz reporter service
 */
public class ReportedException {

  private ReporterService reporterService;

  public ReportedException(JavaPlugin plugin, Exception e) {
    Exception exception = e.getCause() != null ? (Exception) e.getCause() : e;
    StringBuilder stacktrace = new StringBuilder(exception.getClass().getSimpleName());
    if (exception.getMessage() != null) {
      stacktrace.append(" (").append(exception.getMessage()).append(")");
    }
    stacktrace.append("\n");
    for (StackTraceElement str : exception.getStackTrace()) {
      stacktrace.append(str.toString()).append("\n");
    }

    plugin.getLogger().log(Level.WARNING, "[Reporter service] <<-----------------------------[START]----------------------------->>");
    plugin.getLogger().log(Level.WARNING, stacktrace.toString());
    plugin.getLogger().log(Level.WARNING, "[Reporter service] <<------------------------------[END]------------------------------>>");

    if (!ServiceRegistry.isServiceEnabled() || System.currentTimeMillis() - ServiceRegistry.getServiceCooldown() < 900000) {
      return;
    }
    ServiceRegistry.setServiceCooldown(System.currentTimeMillis());
    new BukkitRunnable() {
      @Override
      public void run() {
        reporterService = new ReporterService(plugin, plugin.getName(), plugin.getDescription().getVersion(), plugin.getServer().getBukkitVersion() + " " + plugin.getServer().getVersion(),
          stacktrace.toString());
        reporterService.reportException();
      }
    }.runTaskAsynchronously(plugin);
  }
}