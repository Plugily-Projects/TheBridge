package plugily.projects.thebridge.handlers.setup.components;

import com.github.stefvanschie.inventoryframework.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.plajerlair.commonsbox.minecraft.configuration.ConfigUtils;
import pl.plajerlair.commonsbox.minecraft.item.ItemBuilder;
import plugily.projects.thebridge.Main;
import plugily.projects.thebridge.arena.Arena;
import plugily.projects.thebridge.arena.options.ArenaOption;
import plugily.projects.thebridge.handlers.setup.SetupInventory;

public class ModeComponent implements SetupComponent {

  private SetupInventory setupInventory;

  @Override
  public void prepare(SetupInventory setupInventory) {
    this.setupInventory = setupInventory;
  }

  @Override
  public void injectComponents(StaticPane pane) {
    Arena arena = setupInventory.getArena();
    if (arena == null) {
      return;
    }
    Player player = setupInventory.getPlayer();
    FileConfiguration config = setupInventory.getConfig();
    Main plugin = setupInventory.getPlugin();

    pane.addItem(new GuiItem(new ItemBuilder(Material.REDSTONE)
      .amount(setupInventory.getSetupUtilities().getMinimumValueHigherThanZero("modevalue"))
      .name(plugin.getChatManager().colorRawMessage("&e&lSet Mode Value Amount"))
      .lore(ChatColor.GRAY + "LEFT click to decrease")
      .lore(ChatColor.GRAY + "RIGHT click to increase")
      .lore(ChatColor.DARK_GRAY + "(how many points until the mode choose winner)")
      .lore("", setupInventory.getSetupUtilities().isOptionDone("instances." + arena.getId() + ".modevalue"))
      .build(), e -> {
      ItemStack itemStack = e.getInventory().getItem(e.getSlot());
      if (itemStack == null || e.getCurrentItem() == null) {
        return;
      }
      if (e.getClick().isRightClick()) {
        e.getCurrentItem().setAmount(e.getCurrentItem().getAmount() + 1);
      }
      if (e.getClick().isLeftClick()) {
        e.getCurrentItem().setAmount(e.getCurrentItem().getAmount() - 1);
      }
      if (itemStack.getAmount() < 1) {
        e.getWhoClicked().sendMessage(plugin.getChatManager().colorRawMessage("&c&l✖ &cWarning | Please do not set amount lower than 1!"));
        itemStack.setAmount(1);
      }
      config.set("instances." + arena.getId() + ".modevalue", e.getCurrentItem().getAmount());
      arena.setOptionValue(ArenaOption.MODE_VALUE, e.getCurrentItem().getAmount());
      ConfigUtils.saveConfig(plugin, config, "arenas");
      new SetupInventory(arena, setupInventory.getPlayer()).openInventory();
    }), 0, 0);

    pane.addItem(new GuiItem(new ItemBuilder(Material.REDSTONE)
      .amount(setupInventory.getSetupUtilities().getMinimumValueHigherThanZero("mode"))
      .name(plugin.getChatManager().colorRawMessage("&e&lSet Mode"))
      .lore(ChatColor.GRAY + "LEFT click to set HEARTS")
      .lore(ChatColor.GRAY + "RIGHT click to set POINTS")
      .lore(ChatColor.DARK_GRAY + "(Points: Base with the most points win")
      .lore(ChatColor.DARK_GRAY + "(Hearts: Kinda Last Team Standing)")
      .lore("", setupInventory.getSetupUtilities().isOptionDone("instances." + arena.getId() + ".mode"))
      .build(), e -> {
      Arena.Mode mode = Arena.Mode.POINTS;
      if (e.isLeftClick()) {
        mode = Arena.Mode.HEARTS;
      }
      config.set("instances." + arena.getId() + ".mode", mode);
      arena.setMode(mode);
      ConfigUtils.saveConfig(plugin, config, "arenas");
      new SetupInventory(arena, setupInventory.getPlayer()).openInventory();
    }), 1, 0);

  }
}
