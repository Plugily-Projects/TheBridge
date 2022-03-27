package plugily.projects.thebridge.handlers.setup.components;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import plugily.projects.minigamesbox.classic.commonsbox.number.NumberUtils;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.handlers.setup.PluginSetupInventory;
import plugily.projects.minigamesbox.classic.handlers.setup.SetupUtilities;
import plugily.projects.minigamesbox.classic.handlers.setup.items.LocationItem;
import plugily.projects.minigamesbox.classic.handlers.setup.pages.SetupPage;
import plugily.projects.minigamesbox.classic.utils.configuration.ConfigUtils;
import plugily.projects.minigamesbox.classic.utils.conversation.SimpleConversationBuilder;
import plugily.projects.minigamesbox.classic.utils.dimensional.Cuboid;
import plugily.projects.minigamesbox.classic.utils.helper.ItemBuilder;
import plugily.projects.minigamesbox.classic.utils.hologram.ArmorStandHologram;
import plugily.projects.minigamesbox.classic.utils.serialization.LocationSerializer;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XMaterial;
import plugily.projects.minigamesbox.inventory.common.item.ClickableItem;
import plugily.projects.minigamesbox.inventory.normal.NormalFastInv;
import plugily.projects.thebridge.Main;
import plugily.projects.thebridge.arena.Arena;
import plugily.projects.thebridge.arena.base.Base;
import plugily.projects.thebridge.commands.arguments.admin.CuboidSelector;
import plugily.projects.thebridge.handlers.setup.BaseUtilities;

import java.util.HashMap;

public class BasePage extends NormalFastInv implements SetupPage {

  private final PluginSetupInventory setupInventory;

  public BasePage(int size, String title, PluginSetupInventory pluginSetupInventory) {
    super(size, title);
    this.setupInventory = pluginSetupInventory;
    prepare();
  }

  @Override
  public void prepare() {
    injectItems();
    setForceRefresh(true);
    setDefaultItem(ClickableItem.of(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem()));
    refresh();
  }

  @Override
  public void injectItems() {
    setItem(5, ClickableItem.of(new ItemBuilder(Material.APPLE)
        .name(new MessageBuilder("&e&lSet Color").build())
        .lore(ChatColor.GRAY + "Click to set base color name")
        .lore("", new MessageBuilder("&a&lCurrently: &e" + setupInventory.getPlugin().getSetupUtilities().getConfig().getString("instances." + setupInventory.getArena().getId() + ".bases." + getId(setupInventory.getPlayer()) + ".color", "none")).build())
        .build(), event -> {
      event.getWhoClicked().closeInventory();
      new SimpleConversationBuilder(setupInventory.getPlugin()).withPrompt(new StringPrompt() {
        @Override
        public @NotNull
        String getPromptText(ConversationContext context) {
          return new MessageBuilder("&ePlease type in chat color name (USE UPPERCASE)!").prefix().build();
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
          if(!ChatColor.valueOf(input).isColor()) {
            setupInventory.getPlayer().sendRawMessage(new MessageBuilder("&cTry again. This is not an color!").prefix().build());
            return Prompt.END_OF_CONVERSATION;
          }
          String color = ChatColor.valueOf(input).name();
          setupInventory.getPlayer().sendRawMessage(new MessageBuilder("&e✔ Completed | &aColor of base " + getId(setupInventory.getPlayer()) + " set to " + color).build());
          setupInventory.getPlugin().getSetupUtilities().getConfig().set("instances." + setupInventory.getArena().getId() + ".bases." + getId(setupInventory.getPlayer()) + ".color", color);
          ConfigUtils.saveConfig(setupInventory.getPlugin(), setupInventory.getPlugin().getSetupUtilities().getConfig(), "arenas");
          BaseUtilities.addEditing(setupInventory.getPlayer());
          open(setupInventory.getPlayer());
          return Prompt.END_OF_CONVERSATION;
        }
      }).buildFor(setupInventory.getPlayer());
    }));
    setItem(6, new LocationItem(new ItemBuilder(XMaterial.BEDROCK.parseMaterial())
        .name(new MessageBuilder("&e&lSet Base Location").build())
        .lore(ChatColor.GRAY + "Click to set the base location")
        .lore(ChatColor.GRAY + "after you selected it with the location wand")
        .lore(ChatColor.DARK_GRAY + "(corners of one base)")
        .lore("", setupInventory.getPlugin().getSetupUtilities().isOptionDoneBool("bases." + getId(setupInventory.getPlayer()) + ".baselocation1", setupInventory))
        .build(), e -> {
      CuboidSelector.Selection selection = ((Main) setupInventory.getArena().getPlugin()).getCuboidSelector().getSelection(setupInventory.getPlayer());
      if(selection == null || selection.getFirstPos() == null || selection.getSecondPos() == null) {
        new MessageBuilder("&cPlease select both corners before adding an base location!").prefix().player(setupInventory.getPlayer()).sendPlayer();
        return;
      }
      LocationSerializer.saveLoc(setupInventory.getPlugin(), setupInventory.getPlugin().getSetupUtilities().getConfig(), "arenas", "instances." + setupInventory.getArena().getId() + ".bases." + getId(setupInventory.getPlayer()) + ".baselocation1", selection.getFirstPos());
      LocationSerializer.saveLoc(setupInventory.getPlugin(), setupInventory.getPlugin().getSetupUtilities().getConfig(), "arenas", "instances." + setupInventory.getArena().getId() + ".bases." + getId(setupInventory.getPlayer()) + ".baselocation2", selection.getSecondPos());
      new MessageBuilder("&e✔ Completed | &aBase location for arena " + setupInventory.getArena().getId() + " set with your selection!").player(setupInventory.getPlayer()).sendPlayer();
      BaseUtilities.addEditing(setupInventory.getPlayer());
      ConfigUtils.saveConfig(setupInventory.getPlugin(), setupInventory.getPlugin().getSetupUtilities().getConfig(), "arenas");
    }, event -> {
      new MessageBuilder("&cNot supported!").prefix().player(setupInventory.getPlayer()).sendPlayer();
    }, false, false, true));

    setItem(7, new LocationItem(new ItemBuilder(XMaterial.ENDER_EYE.parseMaterial())
        .name(new MessageBuilder("&e&lSet Portal Location").build())
        .lore(ChatColor.GRAY + "Click to set the portal location")
        .lore(ChatColor.GRAY + "after you selected it with the location wand")
        .lore(ChatColor.DARK_GRAY + "(corners of the portal on the base)")
        .lore("", setupInventory.getPlugin().getSetupUtilities().isOptionDoneBool("bases." + getId(setupInventory.getPlayer()) + ".portallocation1", setupInventory))
        .build(), e -> {
      CuboidSelector.Selection selection = ((Main) setupInventory.getArena().getPlugin()).getCuboidSelector().getSelection(setupInventory.getPlayer());
      if(selection == null || selection.getFirstPos() == null || selection.getSecondPos() == null) {
        new MessageBuilder("&cPlease select both corners before adding an portal location!").prefix().player(setupInventory.getPlayer()).sendPlayer();
        return;
      }
      LocationSerializer.saveLoc(setupInventory.getPlugin(), setupInventory.getPlugin().getSetupUtilities().getConfig(), "arenas", "instances." + setupInventory.getArena().getId() + ".bases." + getId(setupInventory.getPlayer()) + ".portallocation1", selection.getFirstPos());
      LocationSerializer.saveLoc(setupInventory.getPlugin(), setupInventory.getPlugin().getSetupUtilities().getConfig(), "arenas", "instances." + setupInventory.getArena().getId() + ".bases." + getId(setupInventory.getPlayer()) + ".portallocation2", selection.getSecondPos());
      LocationSerializer.saveLoc(setupInventory.getPlugin(), setupInventory.getPlugin().getSetupUtilities().getConfig(), "arenas", "instances." + setupInventory.getArena().getId() + ".bases." + getId(setupInventory.getPlayer()) + ".portalhologram", new Cuboid(selection.getFirstPos(), selection.getSecondPos()).getCenter().add(0, 2, 0));
      new MessageBuilder("&e✔ Completed | &aPortal location for arena " + setupInventory.getArena().getId() + " set with your selection!").player(setupInventory.getPlayer()).sendPlayer();
      BaseUtilities.addEditing(setupInventory.getPlayer());
      ConfigUtils.saveConfig(setupInventory.getPlugin(), setupInventory.getPlugin().getSetupUtilities().getConfig(), "arenas");
    }, event -> {
      new MessageBuilder("&cNot supported!").prefix().player(setupInventory.getPlayer()).sendPlayer();
    }, false, false, true));

    setItem(8, new LocationItem(new ItemBuilder(XMaterial.GLASS.parseMaterial())
        .name(new MessageBuilder("&e&lSet Cage Location (Only floor)").build())
        .lore(ChatColor.GRAY + "Click to set the cage location (only floor needed)")
        .lore(ChatColor.GRAY + "after you selected it with the location wand")
        .lore(ChatColor.DARK_GRAY + "(Please just select the blocks that should be removed/set)")
        .lore("", setupInventory.getPlugin().getSetupUtilities().isOptionDoneBool("bases." + getId(setupInventory.getPlayer()) + ".cagelocation1", setupInventory))
        .build(), e -> {
      CuboidSelector.Selection selection = ((Main) setupInventory.getArena().getPlugin()).getCuboidSelector().getSelection(setupInventory.getPlayer());
      if(selection == null || selection.getFirstPos() == null || selection.getSecondPos() == null) {
        new MessageBuilder("&cPlease select both corners before adding an cage location!").prefix().player(setupInventory.getPlayer()).sendPlayer();
        return;
      }
      if(new Cuboid(selection.getFirstPos(), selection.getSecondPos()).contains(XMaterial.AIR.parseMaterial())) {
        new MessageBuilder("&cPlease select only the floor of the cage! Make sure that it is not air!").prefix().player(setupInventory.getPlayer()).sendPlayer();
        return;
      }
      LocationSerializer.saveLoc(setupInventory.getPlugin(), setupInventory.getPlugin().getSetupUtilities().getConfig(), "arenas", "instances." + setupInventory.getArena().getId() + ".bases." + getId(setupInventory.getPlayer()) + ".cagelocation1", selection.getFirstPos());
      LocationSerializer.saveLoc(setupInventory.getPlugin(), setupInventory.getPlugin().getSetupUtilities().getConfig(), "arenas", "instances." + setupInventory.getArena().getId() + ".bases." + getId(setupInventory.getPlayer()) + ".cagelocation2", selection.getSecondPos());
      new MessageBuilder("&e✔ Completed | &aCage location for arena " + setupInventory.getArena().getId() + " set with your selection!").player(setupInventory.getPlayer()).sendPlayer();
      BaseUtilities.addEditing(setupInventory.getPlayer());
      ConfigUtils.saveConfig(setupInventory.getPlugin(), setupInventory.getPlugin().getSetupUtilities().getConfig(), "arenas");
    }, event -> {
      new MessageBuilder("&cNot supported!").prefix().player(setupInventory.getPlayer()).sendPlayer();
    }, false, false, true));

    setItem(9, new LocationItem(new ItemBuilder(XMaterial.EMERALD_BLOCK.parseMaterial())
        .name(new MessageBuilder("&e&lSet SpawnPoint Location").build())
        .lore(ChatColor.GRAY + "Click to set the spawn point location")
        .lore(ChatColor.GRAY + "on the place where you are standing.")
        .lore(ChatColor.DARK_GRAY + "(location where players spawns first time")
        .lore(ChatColor.DARK_GRAY + "and on every round reset)")
        .lore("", setupInventory.getPlugin().getSetupUtilities().isOptionDoneBool("bases." + getId(setupInventory.getPlayer()) + ".spawnpoint", setupInventory))
        .build(), e -> {
      String serializedLocation = setupInventory.getPlayer().getLocation().getWorld().getName() + "," + setupInventory.getPlayer().getLocation().getX() + "," + setupInventory.getPlayer().getLocation().getY() + ","
          + setupInventory.getPlayer().getLocation().getZ() + "," + setupInventory.getPlayer().getLocation().getYaw() + ",0.0";
      setupInventory.getPlugin().getSetupUtilities().getConfig().set("instances." + setupInventory.getArena().getId() + ".bases." + getId(setupInventory.getPlayer()) + ".spawnpoint", serializedLocation);
      new MessageBuilder("&e✔ Completed | &aSpawnPoint location for base " + setupInventory.getArena().getId() + " set at your location!").player(setupInventory.getPlayer()).sendPlayer();
      BaseUtilities.addEditing(setupInventory.getPlayer());
      ConfigUtils.saveConfig(setupInventory.getPlugin(), setupInventory.getPlugin().getSetupUtilities().getConfig(), "arenas");
    }, event -> {
      new MessageBuilder("&cNot supported!").prefix().player(setupInventory.getPlayer()).sendPlayer();
    }, false, false, true));
    setItem(10, new LocationItem(new ItemBuilder(XMaterial.LAPIS_BLOCK.parseMaterial())
        .name(new MessageBuilder("&e&lSet ReSpawnPoint Location").build())
        .lore(ChatColor.GRAY + "Click to set the respawn point location")
        .lore(ChatColor.GRAY + "on the place where you are standing.")
        .lore(ChatColor.DARK_GRAY + "(location where players respawns every")
        .lore(ChatColor.DARK_GRAY + "time after death)")
        .lore("", setupInventory.getPlugin().getSetupUtilities().isOptionDoneBool("bases." + getId(setupInventory.getPlayer()) + ".respawnpoint", setupInventory))
        .build(), e -> {
      String serializedLocation = setupInventory.getPlayer().getLocation().getWorld().getName() + "," + setupInventory.getPlayer().getLocation().getX() + "," + setupInventory.getPlayer().getLocation().getY() + ","
          + setupInventory.getPlayer().getLocation().getZ() + "," + setupInventory.getPlayer().getLocation().getYaw() + ",0.0";
      setupInventory.getPlugin().getSetupUtilities().getConfig().set("instances." + setupInventory.getArena().getId() + ".bases." + getId(setupInventory.getPlayer()) + ".respawnpoint", serializedLocation);
      new MessageBuilder("&e✔ Completed | &aReSpawnPoint location for base " + setupInventory.getArena().getId() + " set at your location!").player(setupInventory.getPlayer()).sendPlayer();
      BaseUtilities.addEditing(setupInventory.getPlayer());
      ConfigUtils.saveConfig(setupInventory.getPlugin(), setupInventory.getPlugin().getSetupUtilities().getConfig(), "arenas");
    }, event -> {
      new MessageBuilder("&cNot supported!").prefix().player(setupInventory.getPlayer()).sendPlayer();
    }, false, false, true));
    setItem(11, new LocationItem(new ItemBuilder(XMaterial.ARMOR_STAND.parseMaterial())
        .name(new MessageBuilder("&e&lSet Portal Hologram Location").build())
        .lore(ChatColor.GRAY + "Click to set the portal hologram location")
        .lore(ChatColor.GRAY + "on the place where you are standing.")
        .lore("", setupInventory.getPlugin().getSetupUtilities().isOptionDoneBool("bases." + getId(setupInventory.getPlayer()) + ".portalhologram", setupInventory))
        .build(), e -> {
      String serializedLocation = setupInventory.getPlayer().getLocation().getWorld().getName() + "," + setupInventory.getPlayer().getLocation().getX() + "," + setupInventory.getPlayer().getLocation().getY() + ","
          + setupInventory.getPlayer().getLocation().getZ() + "," + setupInventory.getPlayer().getLocation().getYaw() + ",0.0";
      setupInventory.getPlugin().getSetupUtilities().getConfig().set("instances." + setupInventory.getArena().getId() + ".bases." + getId(setupInventory.getPlayer()) + ".portalhologram", serializedLocation);
      if(setupInventory.getPlugin().getSetupUtilities().getConfig().getBoolean("instances." + setupInventory.getArena().getId() + ".bases." + getId(setupInventory.getPlayer()) + ".isdone", false)) {
        new MessageBuilder("&cLocation changes take affect after restart!").player(setupInventory.getPlayer()).sendPlayer();
      }
      new MessageBuilder("&e✔ Completed | &aPortalHologram location for base " + setupInventory.getArena().getId() + " set at your location!").player(setupInventory.getPlayer()).sendPlayer();
      BaseUtilities.addEditing(setupInventory.getPlayer());
      ConfigUtils.saveConfig(setupInventory.getPlugin(), setupInventory.getPlugin().getSetupUtilities().getConfig(), "arenas");
    }, event -> {
      new MessageBuilder("&cNot supported!").prefix().player(setupInventory.getPlayer()).sendPlayer();
    }, false, false, true));

    setItem(12, ClickableItem.of(new ItemBuilder(XMaterial.FIREWORK_ROCKET.parseMaterial())
        .name(new MessageBuilder("&e&lFinish Base").build())
        .lore(ChatColor.GREEN + "Click to finish & save the setup of this base")
        .build(), e -> {
      String path = "instances." + setupInventory.getArena().getId() + ".bases." + getId(setupInventory.getPlayer());
      if(setupInventory.getPlugin().getSetupUtilities().getConfig().get(path + ".baselocation1") == null) {
        new MessageBuilder("&c&l✘ &cBase validation failed! Please configure base location properly!").prefix().player(setupInventory.getPlayer()).sendPlayer();
        return;
      }
      if(setupInventory.getPlugin().getSetupUtilities().getConfig().get(path  + ".portallocation1") == null) {
        new MessageBuilder("&c&l✘ &cBase validation failed! Please configure portal location properly!").prefix().player(setupInventory.getPlayer()).sendPlayer();
        return;
      }
      if(setupInventory.getPlugin().getSetupUtilities().getConfig().get(path  + ".spawnpoint") == null) {
        new MessageBuilder("&c&l✘ &cBase validation failed! Please configure spawnpoint properly!").prefix().player(setupInventory.getPlayer()).sendPlayer();
        return;
      }
      if(setupInventory.getPlugin().getSetupUtilities().getConfig().get(path  + ".respawnpoint") == null) {
        new MessageBuilder("&c&l✘ &cBase validation failed! Please configure respawnpoint properly!").prefix().player(setupInventory.getPlayer()).sendPlayer();
        return;
      }
      if(setupInventory.getPlugin().getSetupUtilities().getConfig().get(path  + ".color") == null) {
        new MessageBuilder("&c&l✘ &cBase validation failed! Please configure color properly!").prefix().player(setupInventory.getPlayer()).sendPlayer();
        return;
      }
      if(setupInventory.getPlugin().getSetupUtilities().getConfig().get(path  + ".portalhologram") == null) {
        new MessageBuilder("&c&l✘ &cBase validation failed! Please configure portalhologram properly!").prefix().player(setupInventory.getPlayer()).sendPlayer();
        return;
      }
      new MessageBuilder("&a&l✔ &aValidation succeeded! Registering new base: "+getId(setupInventory.getPlayer())).prefix().player(setupInventory.getPlayer()).sendPlayer();
      setupInventory.getPlugin().getSetupUtilities().getConfig().set(path  + ".isdone", true);
      Base base = new Base(
          setupInventory.getPlugin().getSetupUtilities().getConfig().getString(path + ".color"),
          LocationSerializer.getLocation(setupInventory.getPlugin().getSetupUtilities().getConfig().getString(path  + ".baselocation1")),
          LocationSerializer.getLocation(setupInventory.getPlugin().getSetupUtilities().getConfig().getString(path  + ".baselocation2")),
          LocationSerializer.getLocation(setupInventory.getPlugin().getSetupUtilities().getConfig().getString(path + ".spawnpoint")),
          LocationSerializer.getLocation(setupInventory.getPlugin().getSetupUtilities().getConfig().getString(path  + ".respawnpoint")),
          LocationSerializer.getLocation(setupInventory.getPlugin().getSetupUtilities().getConfig().getString(path + ".portallocation1")),
          LocationSerializer.getLocation(setupInventory.getPlugin().getSetupUtilities().getConfig().getString(path + ".portallocation2")),
          setupInventory.getPlugin().getSetupUtilities().getConfig().getInt(path + ".maximumsize")
      );
      if(setupInventory.getPlugin().getSetupUtilities().getConfig().get(path  + ".cagelocation1") != null)
        base.setCageCuboid(new Cuboid(LocationSerializer.getLocation(setupInventory.getPlugin().getSetupUtilities().getConfig().getString(path  + ".cagelocation1")), LocationSerializer.getLocation(setupInventory.getPlugin().getSetupUtilities().getConfig().getString(path + ".cagelocation2"))));
      ((Arena)setupInventory.getArena()).addBase(base);
      ArmorStandHologram portal = new ArmorStandHologram(setupInventory.getPlugin().getBukkitHelper().getBlockCenter(LocationSerializer.getLocation(setupInventory.getPlugin().getSetupUtilities().getConfig().getString(path + ".portalhologram"))));
      for(String str : setupInventory.getPlugin().getLanguageManager().getLanguageMessage(setupInventory.getPlugin().getMessageManager().getPath("IN_GAME_MESSAGES_ARENA_PORTAL_HOLOGRAM")).split(";")) {
        portal.appendLine(str.replace("%arena_base_color_formatted%", base.getFormattedColor()));
      }
      base.setArmorStandHologram(portal);
      ConfigUtils.saveConfig(setupInventory.getPlugin(), setupInventory.getPlugin().getSetupUtilities().getConfig(), "arenas");
      BaseUtilities.getBaseId().remove(setupInventory.getPlayer());
      BaseUtilities.removeEditing(setupInventory.getPlayer());
      setupInventory.getPlayer().closeInventory();
    }));
    setItem(13, ClickableItem.of(new ItemBuilder(Material.NAME_TAG)
        .name(new MessageBuilder("&e&lEdit a already created base").build())
        .lore(ChatColor.GRAY + "Click to enter base id (first created base = 0)")
        .lore("", new MessageBuilder("&a&lCurrently: &e" + setupInventory.getPlugin().getSetupUtilities().isOptionDone("bases." + getId(setupInventory.getPlayer()) + ".color", setupInventory)).build())
        .build(), event -> {
      event.getWhoClicked().closeInventory();
      new SimpleConversationBuilder(setupInventory.getPlugin()).withPrompt(new StringPrompt() {
        @Override
        public @NotNull
        String getPromptText(ConversationContext context) {
          return new MessageBuilder("&ePlease type in base id (starts with 0)!").prefix().build();
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
          if(!NumberUtils.isInteger(input)) {
            setupInventory.getPlayer().sendRawMessage(new MessageBuilder("&cTry again. Its not a number!").prefix().build());
            return Prompt.END_OF_CONVERSATION;
          }
          int number = Integer.parseInt(input);

          if(setupInventory.getPlugin().getSetupUtilities().getConfig().getConfigurationSection("instances." + setupInventory.getArena().getId() + ".bases") != null) {
            if(number >= setupInventory.getPlugin().getSetupUtilities().getConfig().getConfigurationSection("instances." + setupInventory.getArena().getId() + ".bases").getKeys(false).size()) {
              setupInventory.getPlayer().sendRawMessage(new MessageBuilder("&cTry again. The number is higher than bases that you have!").prefix().build());
              return Prompt.END_OF_CONVERSATION;
            }
          } else {
            setupInventory.getPlayer().sendRawMessage(new MessageBuilder("&cYou do not have bases atm!").prefix().build());
            return Prompt.END_OF_CONVERSATION;
          }
          setId(setupInventory.getPlayer(), number);
          BaseUtilities.addEditing(setupInventory.getPlayer());
          open(setupInventory.getPlayer());
          setupInventory.getPlayer().sendRawMessage(new MessageBuilder("&e✔ Completed | &aNow editing base " + getId(setupInventory.getPlayer()) + " with color " + setupInventory.getPlugin().getSetupUtilities().getConfig().getString("instances." + setupInventory.getArena().getId() + ".bases." + getId(setupInventory.getPlayer()) + ".color")).build());
          return Prompt.END_OF_CONVERSATION;
        }
      }).buildFor(setupInventory.getPlayer());
    }));

  }

  @Override
  protected void onClick(InventoryClickEvent event) {
    injectItems();
    refresh();
  }


  public int getId(Player player) {
    if(!BaseUtilities.check((Arena) setupInventory.getArena(), player)) {
      int id = 0;
      if(setupInventory.getPlugin().getSetupUtilities().getConfig().getConfigurationSection("instances." + setupInventory.getArena().getId() + ".bases") != null) {
        id = setupInventory.getPlugin().getSetupUtilities().getConfig().getConfigurationSection("instances." + setupInventory.getArena().getId() + ".bases").getKeys(false).size();
      }
      HashMap<String, Integer> secondMap = new HashMap<>();
      secondMap.put(setupInventory.getArena().getId(), id);
      BaseUtilities.getBaseId().put(player, secondMap);
    }
    return BaseUtilities.getBaseId().get(player).get(setupInventory.getArena().getId());
  }

  public void setId(Player player, int id) {
    HashMap<String, Integer> secondMap = new HashMap<>();
    secondMap.put(setupInventory.getArena().getId(), id);
    BaseUtilities.getBaseId().remove(player);
    BaseUtilities.getBaseId().put(player, secondMap);
  }
}
