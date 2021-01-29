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

package plugily.projects.thebridge;


import java.util.HashMap;
import java.util.Map;

/**
 * @author Tigerpanzer_02, 2Wild4You
 * <p>
 * Created at 31.10.2020
 */
public class ConfigPreferences {

  private final Main plugin;
  private final Map<Option, Boolean> options = new HashMap<>();
  private String coloredBlockMaterial;

  public ConfigPreferences(Main plugin) {
    this.plugin = plugin;
    loadOptions();
    loadColoredBlockMaterial();
  }

  /**
   * Returns whether option value is true or false
   *
   * @param option option to get value from
   * @return true or false based on user configuration
   */
  public boolean getOption(Option option) {
    return options.get(option);
  }

  public String getColoredBlockMaterial() {
    return coloredBlockMaterial;
  }

  private void loadOptions() {
    for(Option option : Option.values()) {
      options.put(option, plugin.getConfig().getBoolean(option.getPath(), option.getDefault()));
    }
  }

  private void loadColoredBlockMaterial() {
    coloredBlockMaterial = plugin.getConfig().getString("Colored-Block-Material", "_TERRACOTTA");
  }

  public enum Option {
    BOSSBAR_ENABLED("Bossbar-Enabled", true), BUNGEE_ENABLED("BungeeActivated", false), CHAT_FORMAT_ENABLED("ChatFormat-Enabled", true),
    DATABASE_ENABLED("DatabaseActivated", false), INVENTORY_MANAGER_ENABLED("InventoryManager", true),
    DISABLE_FALL_DAMAGE("Disable-Fall-Damage", false), ENABLE_SHORT_COMMANDS("Enable-Short-Commands", false),
    DISABLE_SEPARATE_CHAT("Disable-Separate-Chat", false), DISABLE_PARTIES("Disable-Parties", true);

    private final String path;
    private final boolean def;

    Option(String path, boolean def) {
      this.path = path;
      this.def = def;
    }

    public String getPath() {
      return path;
    }

    /**
     * @return default value of option if absent in config
     */
    public boolean getDefault() {
      return def;
    }
  }
}
