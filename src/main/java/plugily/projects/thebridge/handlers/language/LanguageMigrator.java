
package plugily.projects.thebridge.handlers.language;

import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import pl.plajerlair.commonsbox.minecraft.configuration.ConfigUtils;
import pl.plajerlair.commonsbox.minecraft.migrator.MigratorUtils;
import plugily.projects.thebridge.Main;

import java.io.File;

/*
  NOTE FOR CONTRIBUTORS - Please do not touch this class if you don't now how it works! You can break migrator modyfing these values!
 */
@SuppressWarnings("deprecation")
public class LanguageMigrator {

  public static final int CONFIG_FILE_VERSION = 1;
  public static final int LANGUAGE_FILE_VERSION = 1;
  private final Main plugin;

  public LanguageMigrator(Main plugin) {
    this.plugin = plugin;

    //initializes migrator to update files with latest values
    configUpdate();
    languageFileUpdate();
  }

  private void configUpdate() {
    if (plugin.getConfig().getInt("Version") == CONFIG_FILE_VERSION) {
      return;
    }
    Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[The Bridge] System notify >> Your config file is outdated! Updating...");
    File file = new File(plugin.getDataFolder() + "/config.yml");
    File bungeefile = new File(plugin.getDataFolder() + "/bungee.yml");

    int version = plugin.getConfig().getInt("Version", CONFIG_FILE_VERSION - 1);

    for (int i = version; i < CONFIG_FILE_VERSION; i++) {
      switch (i) {
        case 1:
          break;
        default:
          break;
      }
      i++;
    }
    updateConfigVersionControl(version);
    plugin.reloadConfig();
    Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[The Bridge] [System notify] Config updated, no comments were removed :)");
    Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[The Bridge] [System notify] You're using latest config file version! Nice!");
  }

  private void languageFileUpdate() {
    FileConfiguration config = ConfigUtils.getConfig(plugin, "language");
    if (config.getString("File-Version-Do-Not-Edit", "").equals(String.valueOf(LANGUAGE_FILE_VERSION))) {
      return;
    }
    Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[The Bridge] [System notify] Your language file is outdated! Updating...");

    int version = LANGUAGE_FILE_VERSION - 1;
    if (NumberUtils.isNumber(config.getString("File-Version-Do-Not-Edit"))) {
      version = Integer.parseInt(config.getString("File-Version-Do-Not-Edit"));
    } else {
      Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[The Bridge] [System notify] Failed to parse language file version!");
    }
    updateLanguageVersionControl(version);

    File file = new File(plugin.getDataFolder() + "/language.yml");

    for (int i = version; i < LANGUAGE_FILE_VERSION; i++) {
      switch (version) {
        case 1:
          break;
        default:
          break;
      }
      version++;
    }
    Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[The Bridge] [System notify] Language file updated! Nice!");
    Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[The Bridge] [System notify] You're using latest language file version! Nice!");
  }

  private void updateConfigVersionControl(int oldVersion) {
    File file = new File(plugin.getDataFolder() + "/config.yml");
    MigratorUtils.removeLineFromFile(file, "# Don't modify");
    MigratorUtils.removeLineFromFile(file, "Version: " + oldVersion);
    MigratorUtils.removeLineFromFile(file, "# No way! You've reached the end! But... where's the dragon!?");
    MigratorUtils.addNewLines(file, "# Don't modify\r\nVersion: " + CONFIG_FILE_VERSION + "\r\n# No way! You've reached the end! But... where's the dragon!?");
  }

  private void updateLanguageVersionControl(int oldVersion) {
    File file = new File(plugin.getDataFolder() + "/language.yml");
    MigratorUtils.removeLineFromFile(file, "# Don't edit it. But who's stopping you? It's your server!");
    MigratorUtils.removeLineFromFile(file, "# Really, don't edit ;p");
    MigratorUtils.removeLineFromFile(file, "File-Version-Do-Not-Edit: " + oldVersion);
    MigratorUtils.addNewLines(file, "# Don't edit it. But who's stopping you? It's your server!\r\n# Really, don't edit ;p\r\nFile-Version-Do-Not-Edit: " + LANGUAGE_FILE_VERSION + "\r\n");
  }
}
