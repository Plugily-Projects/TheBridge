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

package plugily.projects.thebridge.handlers.setup;

import com.github.stefvanschie.inventoryframework.Gui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import pl.plajerlair.commonsbox.minecraft.configuration.ConfigUtils;
import plugily.projects.thebridge.Main;
import plugily.projects.thebridge.arena.Arena;
import plugily.projects.thebridge.handlers.setup.components.*;

import java.util.Random;

/**
 * Created by Tom on 15/06/2015.
 */
public class SetupInventory {

  public static final String VIDEO_LINK = "https://tutorial.plugily.xyz";
  private static final Random random = new Random();
  private static Main plugin;
  private final FileConfiguration config;
  private final Arena arena;
  private final Player player;
  private Gui gui;
  private Gui bases;
  private Gui modes;
  private final SetupUtilities setupUtilities;

  public SetupInventory(Arena arena, Player player) {
    this.config = ConfigUtils.getConfig(plugin, "arenas");
    this.arena = arena;
    this.player = player;
    this.setupUtilities = new SetupUtilities(config, arena);
    prepareGuis();
  }

  public static void init(Main plugin) {
    SetupInventory.plugin = plugin;
  }

  private void prepareGuis() {
    this.gui = new Gui(plugin, 2, "The Bridge Arena Setup");
    this.gui.setOnGlobalClick(e -> e.setCancelled(true));
    StaticPane gui = new StaticPane(9, 4);
    this.gui.addPane(gui);

    this.bases = new Gui(plugin, 2, "The Bridge Setup > Base");
    this.bases.setOnGlobalClick(e -> e.setCancelled(true));
    StaticPane bases = new StaticPane(9, 4);
    this.bases.addPane(bases);

    this.modes = new Gui(plugin, 2, "The Bridge Setup > Mode");
    this.modes.setOnGlobalClick(e -> e.setCancelled(true));
    StaticPane modes = new StaticPane(9, 4);
    this.modes.addPane(modes);

    prepareComponentsGui(gui);
    prepareComponentsBase(bases);
    prepareComponentsMode(modes);
  }

  private void prepareComponentsGui(StaticPane pane) {
    SpawnComponents spawnComponents = new SpawnComponents();
    spawnComponents.prepare(this);
    spawnComponents.injectComponents(pane);

    PlayerAmountComponents playerAmountComponents = new PlayerAmountComponents();
    playerAmountComponents.prepare(this);
    playerAmountComponents.injectComponents(pane);

    MiscComponents miscComponents = new MiscComponents();
    miscComponents.prepare(this);
    miscComponents.injectComponents(pane);

    ArenaRegisterComponent arenaRegisterComponent = new ArenaRegisterComponent();
    arenaRegisterComponent.prepare(this);
    arenaRegisterComponent.injectComponents(pane);
  }

  private void prepareComponentsBase(StaticPane pane) {
    BaseComponent baseComponent = new BaseComponent();
    baseComponent.prepare(this);
    baseComponent.injectComponents(pane);
  }

  private void prepareComponentsMode(StaticPane pane) {
    ModeComponent modeComponent = new ModeComponent();
    modeComponent.prepare(this);
    modeComponent.injectComponents(pane);
  }

  private void sendProTip(Player p) {
    int rand = random.nextInt(8 + 1);
    switch (rand) {
      case 2:
        p.sendMessage(plugin.getChatManager().colorRawMessage("&e&lTIP: &7Help us translating plugin to your language here: https://translate.plugily.xyz"));
        break;
      case 3:
        p.sendMessage(plugin.getChatManager().colorRawMessage("&e&lTIP: &7LeaderHeads leaderboard plugin is supported with our plugin! Check here: https://bit.ly/2Riu5L0"));
        break;
      case 4:
        p.sendMessage(plugin.getChatManager().colorRawMessage("&e&lTIP: &7Achievements, custom kits and replay ability are things available in our paid addon for this minigame!"));
        break;
      case 5:
        p.sendMessage(plugin.getChatManager().colorRawMessage("&e&lTIP: &7We are open source! You can always help us by contributing! Check https://github.com/Plugily-Projects/TheBridge"));
        break;
      case 6:
        p.sendMessage(plugin.getChatManager().colorRawMessage("&e&lTIP: &7Need help? Check wiki &8https://wiki.plugily.xyz/thebridge &7or discord https://discord.plugily.xyz"));
        break;
      case 8:
        p.sendMessage(plugin.getChatManager().colorRawMessage("&e&lTIP: &7Suggest new ideas for the plugin or vote on current ones! https://app.feedbacky.net/b/TheBridge"));
        break;
      default:
        break;
    }
  }

  public void openInventory() {
    sendProTip(player);
    gui.show(player);
  }

  public void openBases() {
    sendProTip(player);
    bases.show(player);
  }

  public void openModes() {
    sendProTip(player);
    modes.show(player);
  }

  public Main getPlugin() {
    return plugin;
  }

  public FileConfiguration getConfig() {
    return config;
  }

  public Arena getArena() {
    return arena;
  }

  public Player getPlayer() {
    return player;
  }

  public Gui getGui() {
    return gui;
  }

  public SetupUtilities getSetupUtilities() {
    return setupUtilities;
  }
}
