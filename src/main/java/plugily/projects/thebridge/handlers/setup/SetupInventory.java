package plugily.projects.thebridge.handlers.setup;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.arena.PluginArena;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.handlers.setup.PluginSetupInventory;
import plugily.projects.minigamesbox.classic.handlers.setup.SetupUtilities;
import plugily.projects.minigamesbox.classic.handlers.setup.items.CountItem;
import plugily.projects.minigamesbox.classic.handlers.setup.items.LocationItem;
import plugily.projects.minigamesbox.classic.utils.configuration.ConfigUtils;
import plugily.projects.minigamesbox.classic.utils.conversation.SimpleConversationBuilder;
import plugily.projects.minigamesbox.classic.utils.dimensional.Cuboid;
import plugily.projects.minigamesbox.classic.utils.dimensional.CuboidSelector;
import plugily.projects.minigamesbox.classic.utils.helper.ItemBuilder;
import plugily.projects.minigamesbox.classic.utils.serialization.LocationSerializer;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XMaterial;
import plugily.projects.minigamesbox.inventory.common.item.ClickableItem;
import plugily.projects.minigamesbox.inventory.normal.NormalFastInv;
import plugily.projects.thebridge.Main;
import plugily.projects.thebridge.arena.Arena;
import plugily.projects.thebridge.handlers.setup.components.BasePage;

public class SetupInventory extends PluginSetupInventory {


  private final Main plugin;
  private Arena arena;
  private final Player player;


  public SetupInventory(Main plugin, @Nullable PluginArena arena, Player player) {
    super(plugin, arena, player);
    this.plugin = plugin;
    this.player = player;
    setArena(player, arena);
    open();
  }

  public SetupInventory(Main plugin, @Nullable PluginArena arena, Player player, SetupUtilities.InventoryStage inventoryStage) {
    super(plugin, arena, player, inventoryStage);
    this.plugin = plugin;
    this.player = player;
    setArena(player, arena);
    open();
  }

  @Override
  public void setArena(Player player, PluginArena arena) {
    if(arena == null && plugin.getSetupUtilities().getArena(player) != null) {
      this.arena = plugin.getArenaRegistry().getArena(plugin.getSetupUtilities().getArena(player).getId());
      setInventoryStage(SetupUtilities.InventoryStage.PAGED_GUI);
    } else if(arena != null) {
      this.arena = plugin.getArenaRegistry().getArena(arena.getId());
    } else {
      this.arena = null;
    }
    setArena(this.arena);
  }

  @Override
  public void addExternalItems(NormalFastInv inv) {
    switch (getInventoryStage()) {
      case SETUP_GUI:
        break;
      case ARENA_LIST:
        break;
      case PAGED_GUI:
        inv.setItem(10, ClickableItem.of(new ItemBuilder(XMaterial.ORANGE_STAINED_GLASS_PANE.parseMaterial()).name("&e&lEdit Base").lore(ChatColor.GRAY + "Here you can add/edit a base")
            .lore(ChatColor.GRAY + "Make sure to register the base before continuing!").build(), event -> openBaseMenu()));
        break;
      case PAGED_VALUES:
        inv.setItem(41, ClickableItem.of(new ItemBuilder(Material.APPLE)
            .name(new MessageBuilder("&e&lSet Mode").build())
            .lore(ChatColor.DARK_GRAY + "(Points: Base with the most points win")
            .lore(ChatColor.DARK_GRAY + "(Hearts: Kinda Last Team Standing)")
            .lore("", new MessageBuilder("&a&lCurrently: &e" + getPlugin().getSetupUtilities().getConfig().getString("instances." + getArena().getId() + ".mode", "none")).build())
            .build(), event -> {
          event.getWhoClicked().closeInventory();
          new SimpleConversationBuilder(getPlugin()).withPrompt(new StringPrompt() {
            @Override
            public @NotNull
            String getPromptText(ConversationContext context) {
              return new MessageBuilder("&ePlease type in chat mode name! (HEARTS or POINTS)").prefix().build();
            }

            @Override
            public Prompt acceptInput(ConversationContext context, String input) {
              String name = new MessageBuilder(input).build();
              Arena.Mode mode = Arena.Mode.valueOf(name);
              getPlayer().sendRawMessage(new MessageBuilder("&e✔ Completed | &aMode of arena " + getArena().getId() + " set to " + mode).build());
              arena.setMode(mode);
              getPlugin().getSetupUtilities().getConfig().set("instances." + getArena().getId() + ".mode", mode);
              ConfigUtils.saveConfig(plugin, plugin.getSetupUtilities().getConfig(), "arenas");

              open(SetupUtilities.InventoryStage.PAGED_VALUES);
              return Prompt.END_OF_CONVERSATION;
            }
          }).buildFor(getPlayer());
        }));
        break;
      case PAGED_BOOLEAN:
        break;
      case PAGED_COUNTABLE:
        inv.setItem(10, new CountItem(new ItemBuilder(Material.REDSTONE)
            .amount(plugin.getSetupUtilities().getMinimumValueHigherThanZero("maximumsize", this))
            .name(new MessageBuilder("&e&lSet Maximum Players Per Base Amount").build())
            .lore(ChatColor.GRAY + "LEFT click to decrease")
            .lore(ChatColor.GRAY + "RIGHT click to increase")
            .lore(ChatColor.DARK_GRAY + "(how many players one base can hold)")
            .lore("", plugin.getSetupUtilities().isOptionDone("maximumsize", this))
            .build(), e -> {
          ItemStack currentItem = e.getCurrentItem();
          if(currentItem == null) {
            return;
          }
          plugin.getSetupUtilities().getConfig().set("instances." + arena.getId() + ".maximumsize", e.getCurrentItem().getAmount());
          arena.setArenaOption("BASE_PLAYER_SIZE", e.getCurrentItem().getAmount());
          ConfigUtils.saveConfig(plugin, plugin.getSetupUtilities().getConfig(), "arenas");
          inv.refresh();
        }));
        inv.setItem(11, new CountItem(new ItemBuilder(Material.REDSTONE_TORCH)
            .amount(plugin.getSetupUtilities().getMinimumValueHigherThanZero("modevalue", this))
            .name(new MessageBuilder("&e&lSet Mode Value Amount").build())
            .lore(ChatColor.GRAY + "LEFT click to decrease")
            .lore(ChatColor.GRAY + "RIGHT click to increase")
            .lore(ChatColor.DARK_GRAY + "(how many points until the mode choose winner)")
            .lore("", plugin.getSetupUtilities().isOptionDone("modevalue", this))
            .build(), e -> {
          ItemStack currentItem = e.getCurrentItem();
          if(currentItem == null) {
            return;
          }
          plugin.getSetupUtilities().getConfig().set("instances." + arena.getId() + ".modevalue", e.getCurrentItem().getAmount());
          arena.setArenaOption("MODE_VALUE", e.getCurrentItem().getAmount());
          ConfigUtils.saveConfig(plugin, plugin.getSetupUtilities().getConfig(), "arenas");
          inv.refresh();
        }));
        inv.setItem(12, new CountItem(new ItemBuilder(Material.REDSTONE_LAMP)
            .amount(plugin.getSetupUtilities().getMinimumValueHigherThanZero("resetblocks", this))
            .name(new MessageBuilder("&e&lSet reset blocks round").build())
            .lore(ChatColor.GRAY + "LEFT click to decrease")
            .lore(ChatColor.GRAY + "RIGHT click to increase")
            .lore(ChatColor.DARK_GRAY + "(After how many rounds should we reset blocks?)")
            .lore(ChatColor.DARK_GRAY + "(SHIFT LEFT CLICK to disable reset)")
            .lore("", plugin.getSetupUtilities().isOptionDone("resetblocks", this))
            .build(), e -> {
          ItemStack currentItem = e.getCurrentItem();
          if(currentItem == null) {
            return;
          }
          int amount = e.getCurrentItem().getAmount();
          if(e.getClick().isShiftClick() && e.getClick().isLeftClick()) {
            amount = 0;
          }
          plugin.getSetupUtilities().getConfig().set("instances." + arena.getId() + ".resetblocks", amount);
          arena.setArenaOption("RESET_BLOCKS", amount);
          ConfigUtils.saveConfig(plugin, plugin.getSetupUtilities().getConfig(), "arenas");
          inv.refresh();
        }));


        break;
      case PAGED_LOCATIONS:
        inv.setItem(22, new LocationItem(new ItemBuilder(XMaterial.BEACON.parseMaterial())
            .name(new MessageBuilder("&e&lSet Mid Location").build())
            .lore(ChatColor.GRAY + "Click to set the mid location")
            .lore(ChatColor.GRAY + "on the place where you are standing.")
            .lore(ChatColor.DARK_GRAY + "(location where all lines will be")
            .lore(ChatColor.DARK_GRAY + "crossed from each base)")
            .lore("", plugin.getSetupUtilities().isOptionDoneBool("midlocation", this))
            .build(), e -> {
          String serializedLocation = player.getLocation().getWorld().getName() + "," + player.getLocation().getX() + "," + player.getLocation().getY() + ","
              + player.getLocation().getZ() + "," + player.getLocation().getYaw() + ",0.0";

          plugin.getSetupUtilities().getConfig().set("instances." + arena.getId() + ".midlocation", serializedLocation);
          arena.setMidLocation(player.getLocation());
          new MessageBuilder("&e✔ Completed | &aMid location for arena " + arena.getId() + " set at your location!").player(player).sendPlayer();
          ConfigUtils.saveConfig(plugin, plugin.getSetupUtilities().getConfig(), "arenas");
        }, event -> {
          new MessageBuilder("&cNot supported!").prefix().player(player).sendPlayer();
        }, false, false, true));

        inv.setItem(21, new LocationItem(new ItemBuilder(XMaterial.BEDROCK.parseMaterial())
            .name(new MessageBuilder("&e&lSet Arena Location").build())
            .lore(ChatColor.GRAY + "Click to set the arena location")
            .lore(ChatColor.GRAY + "after you selected it with the location wand")
            .lore(ChatColor.DARK_GRAY + "[location where all bases and lines")
            .lore(ChatColor.DARK_GRAY + "are in (+ players can build inside)]")
            .lore("", plugin.getSetupUtilities().isOptionDoneBool("arenalocation1", this))
            .build(), e -> {
          CuboidSelector.Selection selection = plugin.getCuboidSelector().getSelection(player);
          if(selection == null || selection.getFirstPos() == null || selection.getSecondPos() == null) {
            new MessageBuilder("&cPlease select both corners before adding an arena location!").prefix().player(player).sendPlayer();
            return;
          }
          LocationSerializer.saveLoc(plugin, plugin.getSetupUtilities().getConfig(), "arenas", "instances." + arena.getId() + ".arenalocation1", selection.getFirstPos());
          LocationSerializer.saveLoc(plugin, plugin.getSetupUtilities().getConfig(), "arenas", "instances." + arena.getId() + ".arenalocation2", selection.getSecondPos());
          arena.setArenaBorder(new Cuboid(selection.getFirstPos(), selection.getSecondPos()));
          new MessageBuilder("&e✔ Completed | &aArena location for arena " + arena.getId() + " set at your location!").player(player).sendPlayer();
          ConfigUtils.saveConfig(plugin, plugin.getSetupUtilities().getConfig(), "arenas");
        }, event -> {
          new MessageBuilder("&cNot supported!").prefix().player(player).sendPlayer();
        }, false, false, true));
        break;
      default:
        break;
    }
    inv.refresh();
}

  public void openBaseMenu() {
    NormalFastInv pagedGui = new BasePage(54, plugin.getPluginMessagePrefix() + "Base Editor Menu", this);
    pagedGui.open(player);
  }


  @Override
  public boolean addAdditionalArenaValidateValues(InventoryClickEvent event, PluginArena arena, PluginMain plugin, FileConfiguration config) {
    if(config.getConfigurationSection("instances." + arena.getId() + ".bases") == null || config.getConfigurationSection("instances." + arena.getId() + ".bases").getKeys(false).size() < 1) {
      new MessageBuilder("&c&l✘ &cArena validation failed! Please configure bases properly!").send(event.getWhoClicked());
      return false;
    }
    int basesDone = 0;
    if(config.isConfigurationSection("instances." + arena.getId() + ".bases")) {
      for(String baseID : config.getConfigurationSection("instances." + arena.getId() + ".bases").getKeys(false)) {
        if(config.isSet("instances." + arena.getId() + ".bases." + baseID + ".isdone")) {
          basesDone++;
        }
      }
    }
    if(basesDone < 2) {
      new MessageBuilder("&c&l✘ &cArena validation failed! Please configure bases properly!").send(event.getWhoClicked());
      return false;
    }


    if(!config.isSet("instances." + arena.getId() + ".maximumsize")) {
      new MessageBuilder("&c&l✘ &cArena validation failed! Please configure maximumsize properly!").send(event.getWhoClicked());
      return false;
    }
    if(!config.isSet("instances." + arena.getId() + ".mode")) {
      new MessageBuilder("&c&l✘ &cArena validation failed! Please configure mode properly!").send(event.getWhoClicked());
      return false;
    }
    if(!config.isSet("instances." + arena.getId() + ".resetblocks")) {
      new MessageBuilder("&c&l✘ &cArena validation failed! Please configure resetblocks properly!").send(event.getWhoClicked());
      return false;
    }

    return true;
  }

  @Override
  public void addAdditionalArenaSetValues(PluginArena arena, FileConfiguration config) {
    Arena pluginArena = plugin.getArenaRegistry().getArena(arena.getId());
    if(pluginArena == null) {
      return;
    }
    int basesDone = 0;
    if(config.isConfigurationSection("instances." + arena.getId() + ".bases")) {
      for(String baseID : config.getConfigurationSection("instances." + arena.getId() + ".bases").getKeys(false)) {
        if(config.isSet("instances." + arena.getId() + ".bases." + baseID + ".isdone")) {
          basesDone++;
        }
      }
    }
    pluginArena.setArenaOption("BASE_PLAYER_SIZE", config.getInt("instances." + arena.getId() + ".maximumsize", 3));
    pluginArena.setMaximumPlayers(basesDone * arena.getArenaOption("BASE_PLAYER_SIZE"));
    pluginArena.setArenaOption("RESET_TIME", config.getInt("instances." + arena.getId() + ".resettime", 5));
  }
}
