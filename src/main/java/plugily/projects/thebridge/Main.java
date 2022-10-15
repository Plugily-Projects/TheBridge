package plugily.projects.thebridge;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.jetbrains.annotations.TestOnly;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.handlers.setup.SetupInventory;
import plugily.projects.minigamesbox.classic.handlers.setup.categories.PluginSetupCategoryManager;
import plugily.projects.minigamesbox.classic.utils.services.metrics.Metrics;
import plugily.projects.thebridge.arena.Arena;
import plugily.projects.thebridge.arena.ArenaEvents;
import plugily.projects.thebridge.arena.ArenaManager;
import plugily.projects.thebridge.arena.ArenaRegistry;
import plugily.projects.thebridge.arena.ArenaUtils;
import plugily.projects.thebridge.arena.base.BaseMenuHandler;
import plugily.projects.thebridge.boot.AdditionalValueInitializer;
import plugily.projects.thebridge.boot.MessageInitializer;
import plugily.projects.thebridge.boot.PlaceholderInitializer;
import plugily.projects.thebridge.commands.arguments.ArgumentsRegistry;
import plugily.projects.thebridge.events.PluginEvents;
import plugily.projects.thebridge.handlers.setup.SetupCategoryManager;
import plugily.projects.thebridge.kits.free.BridgeKit;
import plugily.projects.thebridge.kits.free.KnightKit;
import plugily.projects.thebridge.kits.free.LightTankKit;
import plugily.projects.thebridge.kits.level.ArcherKit;
import plugily.projects.thebridge.kits.level.HardcoreKit;
import plugily.projects.thebridge.kits.level.HealerKit;
import plugily.projects.thebridge.kits.level.MediumTankKit;
import plugily.projects.thebridge.kits.level.TerminatorKit;
import plugily.projects.thebridge.kits.premium.HeavyTankKit;
import plugily.projects.thebridge.kits.premium.NakedKit;
import plugily.projects.thebridge.kits.premium.PremiumHardcoreKit;

import java.io.File;
import java.util.logging.Level;

/**
 * Created by Tigerpanzer_02 on 13.03.2022
 */
public class Main extends PluginMain {

  private ArenaRegistry arenaRegistry;
  private ArenaManager arenaManager;
  private ArgumentsRegistry argumentsRegistry;
  private BaseMenuHandler baseMenuHandler;

  @TestOnly
  public Main() {
    super();
  }

  @TestOnly
  protected Main(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
    super(loader, description, dataFolder, file);
  }

  @Override
  public void onEnable() {
    long start = System.currentTimeMillis();
    MessageInitializer messageInitializer = new MessageInitializer(this);
    super.onEnable();
    getDebugger().debug("[System] [Plugin] Initialization start");
    new PlaceholderInitializer(this);
    messageInitializer.registerMessages();
    new AdditionalValueInitializer(this);
    initializePluginClasses();
    addKits();
    getDebugger().debug("Full {0} plugin enabled", getName());
    getDebugger().debug("[System] [Plugin] Initialization finished took {0}ms", System.currentTimeMillis() - start);
  }

  public void initializePluginClasses() {
    addFileName("powerups");
    Arena.init(this);
    ArenaUtils.init(this);
    new ArenaEvents(this);
    arenaManager = new ArenaManager(this);
    arenaRegistry = new ArenaRegistry(this);
    arenaRegistry.registerArenas();
    getSignManager().loadSigns();
    getSignManager().updateSigns();
    argumentsRegistry = new ArgumentsRegistry(this);
    baseMenuHandler = new BaseMenuHandler(this);

    new PluginEvents(this);
    addPluginMetrics();
  }


  public void addKits() {
    long start = System.currentTimeMillis();
    getDebugger().debug("Adding kits...");
    addFileName("kits");
    Class<?>[] classKitNames = new Class[]{BridgeKit.class, KnightKit.class, LightTankKit.class, ArcherKit.class, HealerKit.class,
      MediumTankKit.class, TerminatorKit.class, HardcoreKit.class, PremiumHardcoreKit.class, NakedKit.class, HeavyTankKit.class};
    for(Class<?> kitClass : classKitNames) {
      try {
        kitClass.getDeclaredConstructor().newInstance();
      } catch(Exception e) {
        getLogger().log(Level.SEVERE, "Fatal error while registering existing game kit! Report this error to the developer!");
        getLogger().log(Level.SEVERE, "Cause: " + e.getMessage() + " (kitClass " + kitClass.getName() + ")");
        e.printStackTrace();
      }
    }
    getDebugger().debug("Kit adding finished took {0}ms", System.currentTimeMillis() - start);
  }

  public void addAdditionalValues() {


  }


  private void addPluginMetrics() {
    getMetrics().addCustomChart(new Metrics.SimplePie("hooked_addons", () -> {
      if(getServer().getPluginManager().getPlugin("TheBridge-Enlargement") != null) {
        return "Enlargement";
      }
      return "None";
    }));
  }

  @Override
  public ArenaRegistry getArenaRegistry() {
    return arenaRegistry;
  }

  @Override
  public ArgumentsRegistry getArgumentsRegistry() {
    return argumentsRegistry;
  }

  @Override
  public ArenaManager getArenaManager() {
    return arenaManager;
  }


  public BaseMenuHandler getBaseMenuHandler() {
    return baseMenuHandler;
  }

  @Override
  public PluginSetupCategoryManager getSetupCategoryManager(SetupInventory setupInventory) {
    return new SetupCategoryManager(setupInventory);
  }

}
