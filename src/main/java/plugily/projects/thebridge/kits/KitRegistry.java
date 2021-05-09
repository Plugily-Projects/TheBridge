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

package plugily.projects.thebridge.kits;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import pl.plajerlair.commonsbox.minecraft.configuration.ConfigUtils;
import plugily.projects.thebridge.Main;
import plugily.projects.thebridge.kits.basekits.FreeKit;
import plugily.projects.thebridge.kits.basekits.Kit;
import plugily.projects.thebridge.kits.free.BridgeKit;
import plugily.projects.thebridge.kits.free.LightTankKit;
import plugily.projects.thebridge.kits.level.ArcherKit;
import plugily.projects.thebridge.kits.level.HardcoreKit;
import plugily.projects.thebridge.kits.level.HealerKit;
import plugily.projects.thebridge.kits.level.MediumTankKit;
import plugily.projects.thebridge.kits.level.TerminatorKit;
import plugily.projects.thebridge.kits.premium.HeavyTankKit;
import plugily.projects.thebridge.kits.premium.NakedKit;
import plugily.projects.thebridge.kits.premium.PremiumHardcoreKit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

/**
 * Kit registry class for registering new kits.
 *
 * @author TomTheDeveloper
 */
public class KitRegistry {

  private static final List<Kit> kits = new ArrayList<>();
  private static Kit defaultKit = null;
  private static Main plugin;
  private static final List<Class<?>> classKitNames = Arrays.asList(LightTankKit.class, ArcherKit.class, HealerKit.class,
    MediumTankKit.class, TerminatorKit.class, HardcoreKit.class, PremiumHardcoreKit.class, NakedKit.class, HeavyTankKit.class);

  private KitRegistry() {
  }

  public static void init(Main plugin) {
    KitRegistry.plugin = plugin;
    setupGameKits();
  }

  /**
   * Method for registering new kit
   *
   * @param kit Kit to register
   */
  public static void registerKit(Kit kit) {
    kits.add(kit);
  }

  /**
   * Return default game kit
   *
   * @return default game kit
   */
  public static Kit getDefaultKit() {
    return defaultKit;
  }

  /**
   * Sets default game kit
   *
   * @param defaultKit default kit to set, must be FreeKit
   */
  public static void setDefaultKit(Kit defaultKit) {
    KitRegistry.defaultKit = defaultKit;
  }

  /**
   * Returns all available kits
   *
   * @return list of all registered kits
   */
  public static List<Kit> getKits() {
    return kits;
  }

  /**
   * Get registered kit by it's represented item stack
   *
   * @param itemStack itemstack that kit represents
   * @return Registered kit or default if not found
   */
  public static Kit getKit(ItemStack itemStack) {
    for(Kit kit : kits) {
      if(itemStack.getType() == kit.getMaterial()) {
        return kit;
      }
    }

    return getDefaultKit();
  }

  private static void setupGameKits() {
    BridgeKit bridgeKit = new BridgeKit();
    FileConfiguration config = ConfigUtils.getConfig(plugin, "kits");
    for(Class<?> kitClass : classKitNames) {
      if(config.getBoolean("Enabled-Game-Kits." + kitClass.getSimpleName().replace("Kit", ""))) {
        try {
          Class.forName(kitClass.getName()).newInstance();
        } catch(ClassNotFoundException | IllegalAccessException | InstantiationException e) {
          plugin.getLogger().log(Level.SEVERE, "Fatal error while registering existing game kit! Report this error to the developer!");
          plugin.getLogger().log(Level.SEVERE, "Cause: " + e.getMessage() + " (kitClass " + kitClass.getName() + ")");
        }
      }
    }

    KitRegistry.setDefaultKit(bridgeKit);
  }

}
