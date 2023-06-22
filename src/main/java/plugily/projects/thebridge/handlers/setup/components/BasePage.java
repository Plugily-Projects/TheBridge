package plugily.projects.thebridge.handlers.setup.components;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.handlers.setup.SetupInventory;
import plugily.projects.minigamesbox.classic.handlers.setup.inventories.InventoryHandler;
import plugily.projects.minigamesbox.classic.handlers.setup.items.category.LocationItem;
import plugily.projects.minigamesbox.classic.handlers.setup.items.category.LocationSelectorItem;
import plugily.projects.minigamesbox.classic.utils.configuration.ConfigUtils;
import plugily.projects.minigamesbox.classic.utils.conversation.SimpleConversationBuilder;
import plugily.projects.minigamesbox.classic.utils.dimensional.Cuboid;
import plugily.projects.minigamesbox.classic.utils.helper.ItemBuilder;
import plugily.projects.minigamesbox.classic.utils.hologram.ArmorStandHologram;
import plugily.projects.minigamesbox.classic.utils.serialization.LocationSerializer;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XMaterial;
import plugily.projects.minigamesbox.inventory.common.item.ClickableItem;
import plugily.projects.minigamesbox.inventory.normal.NormalFastInv;
import plugily.projects.minigamesbox.number.NumberUtils;
import plugily.projects.thebridge.arena.Arena;
import plugily.projects.thebridge.arena.base.Base;
import plugily.projects.thebridge.handlers.setup.BaseUtilities;

import java.util.Arrays;
import java.util.HashMap;

public class BasePage extends NormalFastInv implements InventoryHandler {

  private final SetupInventory setupInventory;

  public BasePage(int size, String title, SetupInventory pluginSetupInventory) {
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
    setupInventory.getPlugin().getDebugger().debug("[BASE EDITING] Open for {0} base {1}", setupInventory.getPlayer(), getId(setupInventory.getPlayer()));
  }

  @Override
  public void injectItems() {
    setItem(0, ClickableItem.of(new ItemBuilder(Material.APPLE)
      .name(new MessageBuilder("&e&lSet Color").build())
      .lore(ChatColor.GRAY + "Click to set base color name")
      .lore("", new MessageBuilder("&a&lCurrently: &e" + setupInventory.isOptionDone("bases." + getId(setupInventory.getPlayer()) + ".color")).build())
      .build(), event -> {
      event.getWhoClicked().closeInventory();
      new SimpleConversationBuilder(setupInventory.getPlugin()).withPrompt(new StringPrompt() {
        @Override
        public @NotNull
        String getPromptText(ConversationContext context) {
          return new MessageBuilder("&ePlease type in chat color name! BLACK, DARK_BLUE, DARK_GREEN, DARK_AQUA, DARK_RED, DARK_PURPLE, GOLD, GRAY, DARK_GRAY, BLUE, GREEN, AQUA, RED, LIGHT_PURPLE, YELLOW, WHITE").prefix().build();
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
          try {
            String color = ChatColor.valueOf(input.toUpperCase()).name();
            context.getForWhom().sendRawMessage(new MessageBuilder("&e✔ Completed | &aColor of base " + getId(setupInventory.getPlayer()) + " set to " + color).build());
            setupInventory.setConfig("bases." + getId((Player) context.getForWhom()) + ".color", color);
            NormalFastInv pagedGui = new BasePage(9, setupInventory.getPlugin().getPluginMessagePrefix() + "Base Editor Menu", setupInventory);
            pagedGui.open(setupInventory.getPlayer());
            return Prompt.END_OF_CONVERSATION;
          } catch (IllegalArgumentException ignored) {
            context.getForWhom().sendRawMessage(new MessageBuilder("&cTry again. This is not an color!").prefix().build());
            return Prompt.END_OF_CONVERSATION;
          }
        }
      }).buildFor((Player) event.getWhoClicked());
    }));

    LocationSelectorItem baseCorners = new LocationSelectorItem(setupInventory, new ItemBuilder(XMaterial.BEDROCK.parseMaterial()), "Base", "Set the corners of one base",  "bases." + getId(setupInventory.getPlayer()) + ".baselocation");
    setItem(1, baseCorners);

    //portal in mid function? LocationSerializer.saveLoc(setupInventory.getPlugin(), setupInventory.getConfig(), "arenas", "instances." + setupInventory.getArenaKey() + ".bases." + getId(setupInventory.getPlayer()) + ".portalhologram", new Cuboid(selection.getFirstPos(), selection.getSecondPos()).getCenter().add(0, 2, 0));
    LocationSelectorItem portalCorners = new LocationSelectorItem(setupInventory, new ItemBuilder(XMaterial.ENDER_EYE.parseMaterial()), "Portal", "Set the corners of the portal on the base", "bases." + getId(setupInventory.getPlayer()) + ".portallocation");
    setItem(2, portalCorners);

    LocationSelectorItem cageCorners = new LocationSelectorItem(setupInventory, new ItemBuilder(XMaterial.GLASS.parseMaterial()), "Cage", "Set the corners of the cage, all inside will be removed (Make sure to select the full cage, not only the floor!) \n its optional!", "bases." + getId(setupInventory.getPlayer()) + ".cagelocation");
    setItem(3, cageCorners);


    LocationItem spawnPoint = new LocationItem(setupInventory, new ItemBuilder(XMaterial.EMERALD_BLOCK.parseMaterial()), "SpawnPoint", "Position where players spawn the first time and on round reset", "bases." + getId(setupInventory.getPlayer()) + ".spawnpoint");
    setItem(4, spawnPoint);

    LocationItem respawnPoint = new LocationItem(setupInventory, new ItemBuilder(XMaterial.EMERALD_BLOCK.parseMaterial()), "ReSpawnPoint", "Position where players spawn on respawn (death)", "bases." + getId(setupInventory.getPlayer()) + ".respawnpoint");
    setItem(5, respawnPoint);

    LocationItem hologramLocation = new LocationItem(setupInventory, new ItemBuilder(XMaterial.ARMOR_STAND.parseMaterial()), "Portal Hologram", "The hologram postion for the portal. Best is to set it with player position!", "bases." + getId(setupInventory.getPlayer()) + ".portalhologram");
    setItem(6, hologramLocation);


    setItem(7, ClickableItem.of(new ItemBuilder(XMaterial.FIREWORK_ROCKET.parseMaterial())
      .name(new MessageBuilder("&e&lFinish Base").build())
      .lore(ChatColor.GREEN + "Click to finish & save the setup of this base")
      .build(), event -> {
      String path = "instances." + setupInventory.getArenaKey() + ".bases." + getId(setupInventory.getPlayer());
      if (setupInventory.getConfig().get(path + ".baselocation.1") == null) {
        new MessageBuilder("&c&l✘ &cBase validation failed! Please configure base location properly!").prefix().player(setupInventory.getPlayer()).sendPlayer();
        return;
      }
      if (setupInventory.getConfig().get(path + ".portallocation.1") == null) {
        new MessageBuilder("&c&l✘ &cBase validation failed! Please configure portal location properly!").prefix().player(setupInventory.getPlayer()).sendPlayer();
        return;
      }
      if (setupInventory.getConfig().get(path + ".spawnpoint") == null) {
        new MessageBuilder("&c&l✘ &cBase validation failed! Please configure spawnpoint properly!").prefix().player(setupInventory.getPlayer()).sendPlayer();
        return;
      }
      if (setupInventory.getConfig().get(path + ".respawnpoint") == null) {
        new MessageBuilder("&c&l✘ &cBase validation failed! Please configure respawnpoint properly!").prefix().player(setupInventory.getPlayer()).sendPlayer();
        return;
      }
      if (setupInventory.getConfig().get(path + ".color") == null) {
        new MessageBuilder("&c&l✘ &cBase validation failed! Please configure color properly!").prefix().player(setupInventory.getPlayer()).sendPlayer();
        return;
      }
      if (setupInventory.getConfig().get(path + ".portalhologram") == null) {
        new MessageBuilder("&c&l✘ &cBase validation failed! Please configure portalhologram properly!").prefix().player(setupInventory.getPlayer()).sendPlayer();
        return;
      }
      new MessageBuilder("&a&l✔ &aValidation succeeded! Registering new base: " + getId(setupInventory.getPlayer())).prefix().player(setupInventory.getPlayer()).sendPlayer();
      setupInventory.setConfig("bases." + getId(setupInventory.getPlayer()) + ".isdone", true);
      Base base = new Base(
        setupInventory.getConfig().getString(path + ".color"),
        LocationSerializer.getLocation(setupInventory.getConfig().getString(path + ".baselocation.1")),
        LocationSerializer.getLocation(setupInventory.getConfig().getString(path + ".baselocation.2")),
        LocationSerializer.getLocation(setupInventory.getConfig().getString(path + ".spawnpoint")),
        LocationSerializer.getLocation(setupInventory.getConfig().getString(path + ".respawnpoint")),
        LocationSerializer.getLocation(setupInventory.getConfig().getString(path + ".portallocation.1")),
        LocationSerializer.getLocation(setupInventory.getConfig().getString(path + ".portallocation.2")),
        setupInventory.getConfig().getInt(path + ".maximumsize")
      );
      if (setupInventory.getConfig().get(path + ".cagelocation.1") != null && setupInventory.getConfig().get(path + ".cagelocation.2") != null) {
        base.setCageCuboid(new Cuboid(LocationSerializer.getLocation(setupInventory.getConfig().getString(path + ".cagelocation.1")), LocationSerializer.getLocation(setupInventory.getConfig().getString(path + ".cagelocation.2"))));
      }
      Arena arena = (Arena) setupInventory.getPlugin().getArenaRegistry().getArena(setupInventory.getArenaKey());
      arena.addBase(base);
      ArmorStandHologram portal = new ArmorStandHologram(setupInventory.getPlugin().getBukkitHelper().getBlockCenter(LocationSerializer.getLocation(setupInventory.getConfig().getString(path + ".portalhologram"))));
      for (String str : setupInventory.getPlugin().getLanguageManager().getLanguageMessage(setupInventory.getPlugin().getMessageManager().getPath("IN_GAME_MESSAGES_ARENA_PORTAL_HOLOGRAM")).split(";")) {
        portal.appendLine(str.replace("%arena_base_color_formatted%", base.getFormattedColor()));
      }
      base.setArmorStandHologram(portal);
      BaseUtilities.getBaseId().remove(event.getWhoClicked());
      event.getWhoClicked().closeInventory();
    }));
    setItem(8, ClickableItem.of(new ItemBuilder(Material.NAME_TAG)
      .name(new MessageBuilder("&e&lEdit a already created base").build())
      .lore(ChatColor.GRAY + "Click to enter base id (first created base = 0)")
      .lore("", new MessageBuilder("&a&lCurrently: &e" + setupInventory.isOptionDone("bases." + getId(setupInventory.getPlayer()) + ".color")).build())
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
          if (!NumberUtils.isInteger(input)) {
            context.getForWhom().sendRawMessage(new MessageBuilder("&cTry again. Its not a number!").prefix().build());
            return Prompt.END_OF_CONVERSATION;
          }
          int number = Integer.parseInt(input);

          if (setupInventory.getConfig().getConfigurationSection("instances." + setupInventory.getArenaKey() + ".bases") != null) {
            if (number >= setupInventory.getConfig().getConfigurationSection("instances." + setupInventory.getArenaKey() + ".bases").getKeys(false).size()) {
              context.getForWhom().sendRawMessage(new MessageBuilder("&cTry again. The number is higher than bases that you have!").prefix().build());
              return Prompt.END_OF_CONVERSATION;
            }
          } else {
            context.getForWhom().sendRawMessage(new MessageBuilder("&cYou do not have bases atm!").prefix().build());
            return Prompt.END_OF_CONVERSATION;
          }
          setId(setupInventory.getPlayer(), number);
          NormalFastInv pagedGui = new BasePage(9, setupInventory.getPlugin().getPluginMessagePrefix() + "Base Editor Menu", setupInventory);
          pagedGui.open(setupInventory.getPlayer());
          context.getForWhom().sendRawMessage(new MessageBuilder("&e✔ Completed | &aNow editing base " + getId(setupInventory.getPlayer()) + " with color " + setupInventory.getConfig().getString("instances." + setupInventory.getArenaKey() + ".bases." + getId(setupInventory.getPlayer()) + ".color")).build());
          return Prompt.END_OF_CONVERSATION;
        }
      }).buildFor((Conversable) event.getWhoClicked());
    }));

  }


  public int getId(Player player) {
    if (!BaseUtilities.check(player)) {
      int id = 0;
      if (setupInventory.getConfig().getConfigurationSection("instances." + setupInventory.getArenaKey() + ".bases") != null) {
        id = setupInventory.getConfig().getConfigurationSection("instances." + setupInventory.getArenaKey() + ".bases").getKeys(false).size();
      }
      BaseUtilities.getBaseId().put(player, id);
    }
    return BaseUtilities.getBaseId().get(player);
  }

  public void setId(Player player, int id) {
    BaseUtilities.getBaseId().remove(player);
    BaseUtilities.getBaseId().put(player, id);
  }
}
