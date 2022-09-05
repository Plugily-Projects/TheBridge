package plugily.projects.thebridge.handlers.setup.components;

import org.bukkit.ChatColor;
import org.bukkit.Location;
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
import plugily.projects.minigamesbox.classic.handlers.setup.SetupInventory;
import plugily.projects.minigamesbox.classic.handlers.setup.inventories.InventoryHandler;
import plugily.projects.minigamesbox.classic.handlers.setup.items.LocationItem;
import plugily.projects.minigamesbox.classic.handlers.setup.items.category.LocationItem;
import plugily.projects.minigamesbox.classic.handlers.setup.items.category.LocationSelectorItem;
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
import plugily.projects.thebridge.handlers.setup.BaseUtilities;
import plugily.projects.minigamesbox.classic.utils.dimensional.CuboidSelector;

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
  }

  @Override
  public void injectItems() {


    setItem(5, ClickableItem.of(new ItemBuilder(Material.APPLE)
        .name(new MessageBuilder("&e&lSet Color").build())
        .lore(ChatColor.GRAY + "Click to set base color name")
        .lore("", new MessageBuilder("&a&lCurrently: &e" + setupInventory.getPlugin().getConfig().getString("instances." + setupInventory.getArena().getId() + ".bases." + getId(setupInventory.getPlayer()) + ".color", "none")).build())
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

    LocationSelectorItem baseCorners = new LocationSelectorItem(setupInventory, new ItemBuilder(XMaterial.BEDROCK.parseMaterial()), "Base", "Set the corners of one base", "baselocation");
    setItem(6, baseCorners);

    //portal in mid function? LocationSerializer.saveLoc(setupInventory.getPlugin(), setupInventory.getPlugin().getSetupUtilities().getConfig(), "arenas", "instances." + setupInventory.getArena().getId() + ".bases." + getId(setupInventory.getPlayer()) + ".portalhologram", new Cuboid(selection.getFirstPos(), selection.getSecondPos()).getCenter().add(0, 2, 0));
    LocationSelectorItem portalCorners = new LocationSelectorItem(setupInventory, new ItemBuilder(XMaterial.ENDER_EYE.parseMaterial()), "Base", "Set the corners of the portal on the base", "portallocation");
    setItem(7, portalCorners);

    LocationSelectorItem cageCorners = new LocationSelectorItem(setupInventory, new ItemBuilder(XMaterial.GLASS.parseMaterial()), "Cage", "Set the corners of the cage, all inside will be removed", "cagelocation");
    setItem(8, cageCorners);


    LocationItem spawnPoint = new LocationItem(setupInventory, new ItemBuilder(XMaterial.EMERALD_BLOCK.parseMaterial()), "SpawnPoint", "Position where players spawn the first time and on round reset", "spawnpoint");
    setItem(9, spawnPoint);

    LocationItem respawnPoint = new LocationItem(setupInventory, new ItemBuilder(XMaterial.EMERALD_BLOCK.parseMaterial()), "ReSpawnPoint", "Position where players spawn on respawn (death)", "respawnpoint");
    setItem(10, respawnPoint);

    //todo base isDone
    LocationItem hologramLocation = new LocationItem(setupInventory, new ItemBuilder(XMaterial.ARMOR_STAND.parseMaterial()), "Portal Hologram", "The hologram postion for the portal. Best is to set it with player position!", "portalhologram");
    setItem(11, hologramLocation);


    //todo finish base design
    setItem(12, ClickableItem.of(new ItemBuilder(XMaterial.FIREWORK_ROCKET.parseMaterial())
        .name(new MessageBuilder("&e&lFinish Base").build())
        .lore(ChatColor.GREEN + "Click to finish & save the setup of this base")
        .build(), e -> {
      String path = "instances." + setupInventory.getArena().getId() + ".bases." + getId(setupInventory.getPlayer());
      if(setupInventory.getPlugin().getSetupUtilities().getConfig().get(path + ".baselocation1") == null) {
        new MessageBuilder("&c&l✘ &cBase validation failed! Please configure base location properly!").prefix().player(setupInventory.getPlayer()).sendPlayer();
        return;
      }
      if(setupInventory.getPlugin().getSetupUtilities().getConfig().get(path + ".portallocation1") == null) {
        new MessageBuilder("&c&l✘ &cBase validation failed! Please configure portal location properly!").prefix().player(setupInventory.getPlayer()).sendPlayer();
        return;
      }
      if(setupInventory.getPlugin().getSetupUtilities().getConfig().get(path + ".spawnpoint") == null) {
        new MessageBuilder("&c&l✘ &cBase validation failed! Please configure spawnpoint properly!").prefix().player(setupInventory.getPlayer()).sendPlayer();
        return;
      }
      if(setupInventory.getPlugin().getSetupUtilities().getConfig().get(path + ".respawnpoint") == null) {
        new MessageBuilder("&c&l✘ &cBase validation failed! Please configure respawnpoint properly!").prefix().player(setupInventory.getPlayer()).sendPlayer();
        return;
      }
      if(setupInventory.getPlugin().getSetupUtilities().getConfig().get(path + ".color") == null) {
        new MessageBuilder("&c&l✘ &cBase validation failed! Please configure color properly!").prefix().player(setupInventory.getPlayer()).sendPlayer();
        return;
      }
      if(setupInventory.getPlugin().getSetupUtilities().getConfig().get(path + ".portalhologram") == null) {
        new MessageBuilder("&c&l✘ &cBase validation failed! Please configure portalhologram properly!").prefix().player(setupInventory.getPlayer()).sendPlayer();
        return;
      }
      new MessageBuilder("&a&l✔ &aValidation succeeded! Registering new base: " + getId(setupInventory.getPlayer())).prefix().player(setupInventory.getPlayer()).sendPlayer();
      setupInventory.getPlugin().getSetupUtilities().getConfig().set(path + ".isdone", true);
      Base base = new Base(
          setupInventory.getPlugin().getSetupUtilities().getConfig().getString(path + ".color"),
          LocationSerializer.getLocation(setupInventory.getPlugin().getSetupUtilities().getConfig().getString(path + ".baselocation1")),
          LocationSerializer.getLocation(setupInventory.getPlugin().getSetupUtilities().getConfig().getString(path + ".baselocation2")),
          LocationSerializer.getLocation(setupInventory.getPlugin().getSetupUtilities().getConfig().getString(path + ".spawnpoint")),
          LocationSerializer.getLocation(setupInventory.getPlugin().getSetupUtilities().getConfig().getString(path + ".respawnpoint")),
          LocationSerializer.getLocation(setupInventory.getPlugin().getSetupUtilities().getConfig().getString(path + ".portallocation1")),
          LocationSerializer.getLocation(setupInventory.getPlugin().getSetupUtilities().getConfig().getString(path + ".portallocation2")),
          setupInventory.getPlugin().getSetupUtilities().getConfig().getInt(path + ".maximumsize")
      );
      if(setupInventory.getPlugin().getSetupUtilities().getConfig().get(path + ".cagelocation1") != null)
        base.setCageCuboid(new Cuboid(LocationSerializer.getLocation(setupInventory.getPlugin().getSetupUtilities().getConfig().getString(path + ".cagelocation1")), LocationSerializer.getLocation(setupInventory.getPlugin().getSetupUtilities().getConfig().getString(path + ".cagelocation2"))));
      ((Arena) setupInventory.getArena()).addBase(base);
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
