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

import org.bukkit.configuration.file.FileConfiguration;
import pl.plajerlair.commonsbox.minecraft.configuration.ConfigUtils;
import plugily.projects.thebridge.Main;
import plugily.projects.thebridge.utils.Debugger;
import plugily.projects.thebridge.utils.services.ServiceRegistry;
import plugily.projects.thebridge.utils.services.locale.Locale;
import plugily.projects.thebridge.utils.services.locale.LocaleRegistry;
import plugily.projects.thebridge.utils.services.locale.LocaleService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;


/**
 * @author Tigerpanzer_02, 2Wild4You
 * <p>
 * Created at 31.10.2020
 */
public class LanguageManager {

  private static final Properties properties = new Properties();
  private static Main plugin;
  private static Locale pluginLocale;
  private static FileConfiguration languageConfig;
  private static FileConfiguration defaultLanguageConfig;

  private LanguageManager() {
  }

  public static void init(Main plugin) {
    LanguageManager.plugin = plugin;
    if(!new File(LanguageManager.plugin.getDataFolder() + File.separator + "language.yml").exists()) {
      plugin.saveResource("language.yml", false);
    }
    //auto update
    plugin.saveResource("locales/language_default.yml", true);

    new LanguageMigrator(plugin);
    languageConfig = ConfigUtils.getConfig(plugin, "language");
    defaultLanguageConfig = ConfigUtils.getConfig(plugin, "locales/language_default");
    registerLocales();
    setupLocale();
  }

  private static void registerLocales() {
    Arrays.asList(
      new Locale("Czech", "Český", "cs_CZ", "POEditor contributors", Arrays.asList("czech", "cesky", "český", "cs")),
      new Locale("Dutch", "Nederlands", "nl_NL", "POEditor contributors", Arrays.asList("dutch", "nederlands", "nl")),
      new Locale("English", "English", "en_GB", "Tigerpanzer_02", Arrays.asList("default", "english", "en")),
      new Locale("French", "Français", "fr_FR", "POEditor contributors", Arrays.asList("french", "francais", "français", "fr")),
      new Locale("German", "Deutsch", "de_DE", "2Wild4You, Tigerpanzer_02 and POEditor contributors", Arrays.asList("deutsch", "german", "de")),
      new Locale("Hungarian", "Magyar", "hu_HU", "POEditor contributors", Arrays.asList("hungarian", "magyar", "hu")),
      new Locale("Italian", "Italiano", "it_IT", "POEditor contributors", Arrays.asList("italian", "italiano", "it")),
      new Locale("Polish", "Polski", "pl_PL", "Plajer", Arrays.asList("polish", "polski", "pl")),
      new Locale("Portuguese (BR)", "Português Brasileiro", "pt_BR", "POEditor contributors", Arrays.asList("brazilian", "brasil", "brasileiro", "pt-br", "pt_br")),
      new Locale("Russian", "Pусский", "ru_RU", "POEditor contributors", Arrays.asList("russian", "pусский", "pyccknn", "russkiy", "ru")),
      new Locale("Spanish", "Español", "es_ES", "POEditor contributors", Arrays.asList("spanish", "espanol", "español", "es")))
      .forEach(LocaleRegistry::registerLocale);
  }

  private static void loadProperties() {
    LocaleService service = ServiceRegistry.getLocaleService(plugin);
    /* is beta release */
    if((plugin.getDescription().getVersion().contains("locales") || plugin.getDescription().getVersion().contains("pre")) && !plugin.getConfig().getBoolean("Developer-Mode", false)) {
      Debugger.sendConsoleMsg("&c[The Bridge] Locales aren't supported in beta versions because they're lacking latest translations! Enabling English one...");
      pluginLocale = LocaleRegistry.getByName("English");
      return;
    }
    if(service == null) {
      Debugger.sendConsoleMsg("&c[The Bridge] Locales cannot be downloaded because API website is unreachable, locales will be disabled.");
      pluginLocale = LocaleRegistry.getByName("English");
      return;
    }
    if(service.isValidVersion()) {
      LocaleService.DownloadStatus status = service.demandLocaleDownload(pluginLocale);
      if(status == LocaleService.DownloadStatus.FAIL) {
        pluginLocale = LocaleRegistry.getByName("English");
        Debugger.sendConsoleMsg("&c[The Bridge] Locale service couldn't download latest locale for plugin! English locale will be used instead!");
        return;
      } else if(status == LocaleService.DownloadStatus.SUCCESS) {
        Debugger.sendConsoleMsg("&c[The Bridge] Downloaded locale " + pluginLocale.getPrefix() + " properly!");
      } else if(status == LocaleService.DownloadStatus.LATEST) {
        Debugger.sendConsoleMsg("&c[The Bridge] Locale " + pluginLocale.getPrefix() + " is latest! Awesome!");
      }
    } else {
      pluginLocale = LocaleRegistry.getByName("English");
      Debugger.sendConsoleMsg("&c[The Bridge] Your plugin version is too old to use latest locale! Please update plugin to access latest updates of locale!");
      return;
    }
    try(InputStreamReader reader = new InputStreamReader(new FileInputStream(plugin.getDataFolder() + "/locales/"
      + pluginLocale.getPrefix() + ".properties"), StandardCharsets.UTF_8)) {
      properties.load(reader);
    } catch(IOException e) {
      e.printStackTrace();
    }
  }

  private static void setupLocale() {
    String localeName = plugin.getConfig().getString("locale", "default").toLowerCase();
    for(Locale locale : LocaleRegistry.getRegisteredLocales()) {
      if(locale.getPrefix().equalsIgnoreCase(localeName)) {
        pluginLocale = locale;
        break;
      }
      for(String alias : locale.getAliases()) {
        if(alias.equals(localeName)) {
          pluginLocale = locale;
          break;
        }
      }
    }
    if(pluginLocale == null) {
      Debugger.sendConsoleMsg("&c[The Bridge] Plugin locale is invalid! Using default one...");
      pluginLocale = LocaleRegistry.getByName("English");
    }
    Debugger.sendConsoleMsg("&a[The Bridge] Loaded locale " + pluginLocale.getName() + " (" + pluginLocale.getOriginalName() + " ID: "
      + pluginLocale.getPrefix() + ") by " + pluginLocale.getAuthor());
    loadProperties();
  }

  public static boolean isDefaultLanguageUsed() {
    return pluginLocale.getName().equals("English");
  }

  public static String getLanguageMessage(String path) {
    if(isDefaultLanguageUsed()) {
      return getString(path);
    }
    String prop = properties.getProperty(path);
    if(prop == null) {
      return getString(path);
    }
    if(getString(path).equalsIgnoreCase(defaultLanguageConfig.getString(path, "not found"))) {
      return prop;
    }
    return getString(path);
  }

  public static List<String> getLanguageList(String path) {
    if(isDefaultLanguageUsed()) {
      return getStrings(path);
    }
    String prop = properties.getProperty(path);
    if(prop == null) {
      return getStrings(path);
    }
    if(getString(path).equalsIgnoreCase(defaultLanguageConfig.getString(path, "not found"))) {
      return Arrays.asList(plugin.getChatManager().colorRawMessage(prop).split(";"));
    }
    return getStrings(path);
  }


  private static List<String> getStrings(String path) {
    if(!languageConfig.isSet(path)) {
      Debugger.sendConsoleMsg("&c[TheBridge] Game message not found in your locale!");
      Debugger.sendConsoleMsg("&c[TheBridge] Please regenerate your language.yml file! If error still occurs report it to the developer on discord!");
      Debugger.sendConsoleMsg("&c[TheBridge] Path: " + path);
      return Collections.singletonList("ERR_MESSAGE_" + path + "_NOT_FOUND");
    }
    List<String> list = languageConfig.getStringList(path);
    list = list.stream().map(string -> string = plugin.getChatManager().colorRawMessage(string)).collect(Collectors.toList());
    return list;
  }


  private static String getString(String path) {
    if(!languageConfig.isSet(path)) {
      Debugger.sendConsoleMsg("&c[TheBridge] Game message not found in your locale!");
      Debugger.sendConsoleMsg("&c[TheBridge] Please regenerate your language.yml file! If error still occurs report it to the developer on discord!");
      Debugger.sendConsoleMsg("&c[TheBridge] Path: " + path);
      return "ERR_MESSAGE_" + path + "_NOT_FOUND";
    }
    return languageConfig.getString(path, "not found");
  }

  public static void reloadConfig() {
    languageConfig = ConfigUtils.getConfig(plugin, "language");
  }

  public static Locale getPluginLocale() {
    return pluginLocale;
  }

}
