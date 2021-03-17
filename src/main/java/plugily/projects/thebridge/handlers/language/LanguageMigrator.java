
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

  public static final int CONFIG_FILE_VERSION = 3;
  public static final int LANGUAGE_FILE_VERSION = 4;
  private final Main plugin;

  public LanguageMigrator(Main plugin) {
    this.plugin = plugin;

    //initializes migrator to update files with latest values
    configUpdate();
    languageFileUpdate();
  }

  private void configUpdate() {
    if(plugin.getConfig().getInt("Version") == CONFIG_FILE_VERSION) {
      return;
    }
    Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[The Bridge] System notify >> Your config file is outdated! Updating...");
    File file = new File(plugin.getDataFolder() + "/config.yml");
    File bungeefile = new File(plugin.getDataFolder() + "/bungee.yml");

    int version = plugin.getConfig().getInt("Version", CONFIG_FILE_VERSION - 1);

    for(int i = version; i < CONFIG_FILE_VERSION; i++) {
      switch(i) {
        case 1:
          MigratorUtils.addNewLines(file, "\r\n" +
            "#Disable Party features of external party plugins (such as PAF, Parties ...)\r\n" +
            "Disable-Parties: true\r\n");
          MigratorUtils.addNewLines(file, "\r\n" +
            "# Should we disable all chat related stuff?\r\n" +
            "# It will disable the separated chat, for example\r\n" +
            "Disable-Separate-Chat: false\r\n");
          MigratorUtils.addNewLines(file, "\r\n" +
            "# Enable in game (eg. '[KIT][BASE][LEVEL] Plugily: hey') special formatting?\r\n" +
            "# Formatting is configurable in language.yml\r\n" +
            "# You can use PlaceholderAPI placeholders in chat format!\r\n" +
            "ChatFormat-Enabled: true\r\n");
          MigratorUtils.addNewLines(file, "\r\n" +
            "# Enable bossbar support?\r\n" +
            "Bossbar-Enabled: true\r\n");
          MigratorUtils.addNewLines(file, "\r\n" +
            "# Select locale of The Bridge, default it's English.\r\n" +
            "# Available locales:\r\n" +
            "#    default - English language. Uses 'language.yml'.\r\n" +
            "#    de - Deutsche sprache          pl - Język polski\r\n" +
            "#    es - Idioma español\r\n" +
            "#    fr - Langue française\r\n" +
            "#    hu - Magyar nyelv\r\n" +
            "#    cs - Český jazyk\r\n" +
            "#    pt_br - Português Brasileiro\r\n" +
            "#    it - Lingua italiana           ru - Русский язык\r\n" +
            "#    nl - Dutch\r\n" +
            "locale: default\r\n");
          break;
        case 2:
          MigratorUtils.addNewLines(file, "\r\n" +
              "#Disable Food lose\r\n" +
              "Disable-Food-Lose: true\r\n");
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
    if(config.getString("File-Version-Do-Not-Edit", "").equals(String.valueOf(LANGUAGE_FILE_VERSION))) {
      return;
    }
    Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[The Bridge] [System notify] Your language file is outdated! Updating...");

    int version = LANGUAGE_FILE_VERSION - 1;
    if(NumberUtils.isNumber(config.getString("File-Version-Do-Not-Edit"))) {
      version = Integer.parseInt(config.getString("File-Version-Do-Not-Edit"));
    } else {
      Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[The Bridge] [System notify] Failed to parse language file version!");
    }
    updateLanguageVersionControl(version);

    File file = new File(plugin.getDataFolder() + "/language.yml");

    for(int i = version; i < LANGUAGE_FILE_VERSION; i++) {
      switch(version) {
        case 1:
          MigratorUtils.insertAfterLine(file, "  Item:", "    Name: \"&f%mapname%\"");
          break;
        case 2:
          MigratorUtils.insertAfterLine(file, "  Stats-Command:", "    Wins: \"&aWins: &e\"\n" +
              "    Loses: \"&aLoses: &e\"");
          break;
        case 3:
          MigratorUtils.addNewLines(file, "Placeholders:\r\n" +
              "  Game-States:\r\n" +
              "    Waiting: \"&lWaiting for players...\"\r\n" +
              "    Starting: \"&e&lStarting\"\r\n" +
              "    Playing: \"&lPlaying\"\r\n" +
              "    Ending: \"&lEnding\"\r\n" +
              "    Restarting: \"&c&lRestarting\"\r\n");
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
