package plugily.projects.thebridge.handlers.setup.components;

import com.github.stefvanschie.inventoryframework.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import pl.plajerlair.commonsbox.minecraft.configuration.ConfigUtils;
import pl.plajerlair.commonsbox.minecraft.item.ItemBuilder;
import pl.plajerlair.commonsbox.minecraft.serialization.LocationSerializer;
import plugily.projects.thebridge.Main;
import plugily.projects.thebridge.arena.Arena;
import plugily.projects.thebridge.arena.base.Base;
import plugily.projects.thebridge.handlers.setup.SetupInventory;
import plugily.projects.thebridge.utils.CuboidSelector;
import plugily.projects.thebridge.utils.conversation.SimpleConversationBuilder;

import java.util.Arrays;
import java.util.HashMap;

public class BaseComponent implements SetupComponent {

  private SetupInventory setupInventory;
  private HashMap<Player, Integer> baseId = new HashMap<>();

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

    pane.addItem(new GuiItem(new ItemBuilder(Material.NAME_TAG)
      .name(plugin.getChatManager().colorRawMessage("&e&lSet Color"))
      .lore(ChatColor.GRAY + "Click to set base color name")
      .lore("", plugin.getChatManager().colorRawMessage("&a&lCurrently: &e" + config.getString("instances." + arena.getId() + ".color")))
      .build(), e -> {
      e.getWhoClicked().closeInventory();
      new SimpleConversationBuilder().withPrompt(new StringPrompt() {
        @Override
        public String getPromptText(ConversationContext context) {
          return plugin.getChatManager().colorRawMessage(plugin.getChatManager().getPrefix() + "&ePlease type in chat color name! Please use one of them: " + Arrays.toString(ChatColor.values()));
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
          if (!ChatColor.valueOf(input).isColor()) {
            player.sendRawMessage(plugin.getChatManager().colorRawMessage("&cTry again. This is not an color!"));
          }
          String color = ChatColor.valueOf(input).name();
          player.sendRawMessage(plugin.getChatManager().colorRawMessage("&e✔ Completed | &aColor of base " + getId(player) + " set to " + color));
          config.set("instances." + arena.getId() + ".bases." + getId(player) + ".color", ChatColor.valueOf(input));
          ConfigUtils.saveConfig(plugin, config, "arenas");
          new SetupInventory(arena, player).openBases();
          return Prompt.END_OF_CONVERSATION;
        }
      }).buildFor(player);
    }), 0, 0);

    pane.addItem(new GuiItem(new ItemBuilder(Material.REDSTONE_BLOCK)
      .name(plugin.getChatManager().colorRawMessage("&e&lSet Base Location"))
      .lore(ChatColor.GRAY + "Click to set the base location")
      .lore(ChatColor.GRAY + "after you selected it with the location wand")
      .lore(ChatColor.DARK_GRAY + "(corners of one base)")
      .lore("", setupInventory.getSetupUtilities().isOptionDoneBool("instances." + arena.getId() + ".bases." + getId(player) + ".baselocation1"))
      .build(), e -> {
      e.getWhoClicked().closeInventory();
      CuboidSelector.Selection selection = plugin.getCuboidSelector().getSelection(player);
      if (selection == null || selection.getFirstPos() == null || selection.getSecondPos() == null) {
        player.sendMessage(plugin.getChatManager().colorRawMessage(plugin.getChatManager().getPrefix() + "&cPlease select both corners before adding an base location!"));
        return;
      }
      LocationSerializer.saveLoc(plugin, config, "arenas", "instances." + arena.getId() + ".bases." + getId(player) + ".baselocation1", selection.getFirstPos());
      LocationSerializer.saveLoc(plugin, config, "arenas", "instances." + arena.getId() + ".bases." + getId(player) + ".baselocation2", selection.getSecondPos());
      arena.setEndLocation(player.getLocation());
      player.sendMessage(plugin.getChatManager().colorRawMessage("&e✔ Completed | &aBase location for arena " + arena.getId() + " set with your selection!"));
      ConfigUtils.saveConfig(plugin, config, "arenas");
    }), 1, 0);

    pane.addItem(new GuiItem(new ItemBuilder(Material.REDSTONE_BLOCK)
      .name(plugin.getChatManager().colorRawMessage("&e&lSet Portal Location"))
      .lore(ChatColor.GRAY + "Click to set the portal location")
      .lore(ChatColor.GRAY + "after you selected it with the location wand")
      .lore(ChatColor.DARK_GRAY + "(corners of the portal on the base)")
      .lore("", setupInventory.getSetupUtilities().isOptionDoneBool("instances." + arena.getId() + ".bases." + getId(player) + ".portallocation1"))
      .build(), e -> {
      e.getWhoClicked().closeInventory();
      CuboidSelector.Selection selection = plugin.getCuboidSelector().getSelection(player);
      if (selection == null || selection.getFirstPos() == null || selection.getSecondPos() == null) {
        player.sendMessage(plugin.getChatManager().colorRawMessage(plugin.getChatManager().getPrefix() + "&cPlease select both corners before adding an base location!"));
        return;
      }
      LocationSerializer.saveLoc(plugin, config, "arenas", "instances." + arena.getId() + ".bases." + getId(player) + ".portallocation1", selection.getFirstPos());
      LocationSerializer.saveLoc(plugin, config, "arenas", "instances." + arena.getId() + ".bases." + getId(player) + ".portallocation2", selection.getSecondPos());
      arena.setEndLocation(player.getLocation());
      player.sendMessage(plugin.getChatManager().colorRawMessage("&e✔ Completed | &aBase location for arena " + arena.getId() + " set with your selection!"));
      ConfigUtils.saveConfig(plugin, config, "arenas");
    }), 2, 0);

    String serializedLocation = player.getLocation().getWorld().getName() + "," + player.getLocation().getX() + "," + player.getLocation().getY() + ","
      + player.getLocation().getZ() + "," + player.getLocation().getYaw() + ",0.0";
    pane.addItem(new GuiItem(new ItemBuilder(Material.REDSTONE_BLOCK)
      .name(plugin.getChatManager().colorRawMessage("&e&lSet SpawnPoint Location"))
      .lore(ChatColor.GRAY + "Click to set the spawn point location")
      .lore(ChatColor.GRAY + "on the place where you are standing.")
      .lore(ChatColor.DARK_GRAY + "(location where players spawns first time")
      .lore(ChatColor.DARK_GRAY + "and on every round reset)")
      .lore("", setupInventory.getSetupUtilities().isOptionDoneBool("instances." + arena.getId() + ".bases." + getId(player) + ".spawnpoint"))
      .build(), e -> {
      e.getWhoClicked().closeInventory();
      config.set("instances." + arena.getId() + ".bases." + getId(player) + ".spawnpoint", serializedLocation);
      player.sendMessage(plugin.getChatManager().colorRawMessage("&e✔ Completed | &aSpawnPoint location for base " + getId(player) + " set at your location!"));
      ConfigUtils.saveConfig(plugin, config, "arenas");
    }), 3, 0);

    pane.addItem(new GuiItem(new ItemBuilder(Material.REDSTONE_BLOCK)
      .name(plugin.getChatManager().colorRawMessage("&e&lSet ReSpawnPoint Location"))
      .lore(ChatColor.GRAY + "Click to set the respawn point location")
      .lore(ChatColor.GRAY + "on the place where you are standing.")
      .lore(ChatColor.DARK_GRAY + "(location where players respawns every")
      .lore(ChatColor.DARK_GRAY + "time after death)")
      .lore("", setupInventory.getSetupUtilities().isOptionDoneBool("instances." + arena.getId() + ".bases." + getId(player) + ".respawnpoint"))
      .build(), e -> {
      e.getWhoClicked().closeInventory();
      config.set("instances." + arena.getId() + ".bases." + getId(player) + ".respawnpoint", serializedLocation);
      player.sendMessage(plugin.getChatManager().colorRawMessage("&e✔ Completed | &aReSpawnPoint location for base " + getId(player) + " set at your location!"));
      ConfigUtils.saveConfig(plugin, config, "arenas");
    }), 4, 0);

    pane.addItem(new GuiItem(new ItemBuilder(Material.REDSTONE_BLOCK)
      .name(plugin.getChatManager().colorRawMessage("&e&lFinish Base"))
      .lore(ChatColor.GREEN + "Click to finish & save the setup of this base")
      .build(), e -> {
      e.getWhoClicked().closeInventory();
      if (config.getConfigurationSection("instances." + arena.getId() + ".bases." + getId(player) + ".baselocation1") == null) {
        e.getWhoClicked().sendMessage(plugin.getChatManager().colorRawMessage("&c&l✘ &cArena validation failed! Please configure base location properly!"));
        return;
      }
      if (config.getConfigurationSection("instances." + arena.getId() + ".bases." + getId(player) + ".portallocation1") == null) {
        e.getWhoClicked().sendMessage(plugin.getChatManager().colorRawMessage("&c&l✘ &cArena validation failed! Please configure portal location properly!"));
        return;
      }
      if (config.getConfigurationSection("instances." + arena.getId() + ".bases." + getId(player) + ".spawnpoint") == null) {
        e.getWhoClicked().sendMessage(plugin.getChatManager().colorRawMessage("&c&l✘ &cArena validation failed! Please configure spawnpoint properly!"));
        return;
      }
      if (config.getConfigurationSection("instances." + arena.getId() + ".bases." + getId(player) + ".respawnpoint") == null) {
        e.getWhoClicked().sendMessage(plugin.getChatManager().colorRawMessage("&c&l✘ &cArena validation failed! Please configure respawnpoint properly!"));
        return;
      }
      if (config.getConfigurationSection("instances." + arena.getId() + ".bases." + getId(player) + ".color") == null) {
        e.getWhoClicked().sendMessage(plugin.getChatManager().colorRawMessage("&c&l✘ &cArena validation failed! Please configure color properly!"));
        return;
      }
      player.sendMessage(plugin.getChatManager().colorRawMessage("&a&l✔ &aValidation succeeded! Registering new base: " + getId(player)));
      config.set("instances." + arena.getId() + ".bases." + getId(player) + ".isdone", true);
      arena.addBase(new Base(
        config.getColor("instances." + arena.getId() + ".bases." + getId(player) + ".color"),
        LocationSerializer.getLocation("instances." + arena.getId() + ".bases." + getId(player) + ".baselocation1"),
        LocationSerializer.getLocation("instances." + arena.getId() + ".bases." + getId(player) + ".baselocation2"),
        LocationSerializer.getLocation("instances." + arena.getId() + ".bases." + getId(player) + ".spawnpoint"),
        LocationSerializer.getLocation("instances." + arena.getId() + ".bases." + getId(player) + ".respawnpoint"),
        LocationSerializer.getLocation("instances." + arena.getId() + ".bases." + getId(player) + ".portallocation1"),
        LocationSerializer.getLocation("instances." + arena.getId() + ".bases." + getId(player) + ".portallocation2"),
        config.getInt("instances." + arena.getId() + ".maximumsize")
        ));
      ConfigUtils.saveConfig(plugin, config, "arenas");
      baseId.remove(player);
      setupInventory.setBasesDone(setupInventory.getBasesDone() + 1);
    }), 4, 0);


  }

  public int getId(Player player) {
    if (!baseId.containsKey(player)) {
      int id = 0;
      if (setupInventory.getConfig().getConfigurationSection("instances." + setupInventory.getArena().getId() + ".bases") != null) {
        id = setupInventory.getConfig().getConfigurationSection("instances." + setupInventory.getArena().getId() + ".bases").getKeys(false).size() + 1;
      }
      baseId.put(player, id);
    }
    return baseId.get(player);
  }
}
