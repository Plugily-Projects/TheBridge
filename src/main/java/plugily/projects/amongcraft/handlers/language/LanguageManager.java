package plugily.projects.amongcraft.handlers.language;

import plugily.projects.amongcraft.Main;
import plugily.projects.amongcraft.utils.services.locale.LocaleRegistry;

import pl.plajerlair.commonsbox.minecraft.configuration.ConfigUtils;

import java.io.File;
import java.util.Arrays;
import java.util.Locale;

/**
 * @author Plajer
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
    if (!new File(LanguageManager.plugin.getDataFolder() + File.separator + "language.yml").exists()) {
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
      new Locale("English", "English", "en_GB", "Plajer", Arrays.asList("default", "english", "en")),
      .forEach(LocaleRegistry::registerLocale);
  }

  private static void loadProperties() {
    LocaleService service = ServiceRegistry.getLocaleService(plugin);
    if (service == null) {
      Debugger.sendConsoleMsg("&c[Murder Mystery] Locales cannot be downloaded because API website is unreachable, locales will be disabled.");
      pluginLocale = LocaleRegistry.getByName("English");
      return;
    }
    if (service.isValidVersion()) {
      LocaleService.DownloadStatus status = service.demandLocaleDownload(pluginLocale);
      if (status == LocaleService.DownloadStatus.FAIL) {
        pluginLocale = LocaleRegistry.getByName("English");
        Debugger.sendConsoleMsg("&c[Murder Mystery] Locale service couldn't download latest locale for plugin! English locale will be used instead!");
        return;
      } else if (status == LocaleService.DownloadStatus.SUCCESS) {
        Debugger.sendConsoleMsg("&c[Murder Mystery] Downloaded locale " + pluginLocale.getPrefix() + " properly!");
      } else if (status == LocaleService.DownloadStatus.LATEST) {
        Debugger.sendConsoleMsg("&c[Murder Mystery] Locale " + pluginLocale.getPrefix() + " is latest! Awesome!");
      }
    } else {
      pluginLocale = LocaleRegistry.getByName("English");
      Debugger.sendConsoleMsg("&c[Murder Mystery] Your plugin version is too old to use latest locale! Please update plugin to access latest updates of locale!");
      return;
    }
    try (InputStreamReader reader = new InputStreamReader(new FileInputStream(plugin.getDataFolder() + "/locales/"
      + pluginLocale.getPrefix() + ".properties"), StandardCharsets.UTF_8)) {
      properties.load(reader);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void setupLocale() {
    String localeName = plugin.getConfig().getString("locale", "default").toLowerCase();
    for (Locale locale : LocaleRegistry.getRegisteredLocales()) {
      if (locale.getPrefix().equalsIgnoreCase(localeName)) {
        pluginLocale = locale;
        break;
      }
      for (String alias : locale.getAliases()) {
        if (alias.equals(localeName)) {
          pluginLocale = locale;
          break;
        }
      }
    }
    if (pluginLocale == null) {
      Debugger.sendConsoleMsg("&c[Murder Mystery] Plugin locale is invalid! Using default one...");
      pluginLocale = LocaleRegistry.getByName("English");
    }
    Debugger.sendConsoleMsg("&a[Murder Mystery] Loaded locale " + pluginLocale.getName() + " (" + pluginLocale.getOriginalName() + " ID: "
      + pluginLocale.getPrefix() + ") by " + pluginLocale.getAuthor());
    loadProperties();
  }

  public static boolean isDefaultLanguageUsed() {
    return pluginLocale.getName().equals("English");
  }

  public static String getLanguageMessage(String path) {
    if (isDefaultLanguageUsed()) {
      return getString(path);
    }
    String prop = properties.getProperty(path);
    if (prop == null){
      return getString(path);
    }
    if (getString(path).equalsIgnoreCase(defaultLanguageConfig.getString(path, "not found"))){
      return prop;
    }
    return getString(path);
  }

  public static List<String> getLanguageList(String path) {
    if (isDefaultLanguageUsed()) {
      return getStrings(path);
    }
    String prop = properties.getProperty(path);
    if (prop == null) {
      return getStrings(path);
    }
    if (getString(path).equalsIgnoreCase(defaultLanguageConfig.getString(path, "not found"))){
      return Arrays.asList(plugin.getChatManager().colorRawMessage(prop).split(";"));
    }
    return getStrings(path);
  }


  private static List<String> getStrings(String path) {
    if (!languageConfig.isSet(path)) {
      Debugger.sendConsoleMsg("&c[MurderMystery] Game message not found in your locale!");
      Debugger.sendConsoleMsg("&c[MurderMystery] Please regenerate your language.yml file! If error still occurs report it to the developer on discord!");
      Debugger.sendConsoleMsg("&c[MurderMystery] Path: " + path);
      return Collections.singletonList("ERR_MESSAGE_" + path + "_NOT_FOUND");
    }
    List<String> list = languageConfig.getStringList(path);
    list = list.stream().map(string -> string = plugin.getChatManager().colorRawMessage(string)).collect(Collectors.toList());
    return list;
  }


  private static String getString(String path) {
    if (!languageConfig.isSet(path)) {
      Debugger.sendConsoleMsg("&c[MurderMystery] Game message not found in your locale!");
      Debugger.sendConsoleMsg("&c[MurderMystery] Please regenerate your language.yml file! If error still occurs report it to the developer on discord!");
      Debugger.sendConsoleMsg("&c[MurderMystery] Path: " + path);
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
