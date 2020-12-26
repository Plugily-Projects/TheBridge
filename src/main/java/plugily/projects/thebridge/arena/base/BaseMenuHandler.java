package plugily.projects.thebridge.arena.base;

import com.github.stefvanschie.inventoryframework.Gui;
import com.github.stefvanschie.inventoryframework.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import pl.plajerlair.commonsbox.minecraft.compat.XMaterial;
import pl.plajerlair.commonsbox.minecraft.item.ItemBuilder;
import pl.plajerlair.commonsbox.minecraft.item.ItemUtils;
import plugily.projects.thebridge.Main;
import plugily.projects.thebridge.api.events.player.TBPlayerChooseBaseEvent;
import plugily.projects.thebridge.api.events.player.TBPlayerChooseKitEvent;
import plugily.projects.thebridge.arena.Arena;
import plugily.projects.thebridge.arena.ArenaRegistry;
import plugily.projects.thebridge.handlers.items.SpecialItem;
import plugily.projects.thebridge.handlers.items.SpecialItemManager;
import plugily.projects.thebridge.kits.KitRegistry;
import plugily.projects.thebridge.kits.basekits.Kit;
import plugily.projects.thebridge.user.User;
import plugily.projects.thebridge.utils.Utils;

public class BaseMenuHandler implements Listener {

  private final Main plugin;
  private final String fullTeam;
  private final String emptyTeam;
  private final String insideTeam;
  private final String teamName;
  private final SpecialItem baseItem;

  public BaseMenuHandler(Main plugin) {
    this.plugin = plugin;
    this.baseItem = plugin.getSpecialItemManager().getSpecialItem(SpecialItemManager.SpecialItems.BASE_SELECTOR.getName());
    fullTeam = plugin.getChatManager().colorMessage("TEAM IS FULL");
    emptyTeam = plugin.getChatManager().colorMessage("EMPTY TEAM");
    insideTeam = plugin.getChatManager().colorMessage("INSIDE TEAM");
    teamName = plugin.getChatManager().colorMessage("Team Name");
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  public void createMenu(Player player, Arena arena) {
    Gui gui = new Gui(plugin, Utils.serializeInt(KitRegistry.getKits().size()) / 9, plugin.getChatManager().colorMessage("open base menu"));
    StaticPane pane = new StaticPane(9, gui.getRows());
    gui.addPane(pane);
    int x = 0;
    int y = 0;
    for (Base base : arena.getBases()) {
      ItemStack itemStack = new ItemStack(XMaterial.matchXMaterial(base.getColor().toUpperCase() + "_WOOL").get().parseMaterial(), base.getPlayers().size() == 0 ? 1 : base.getPlayers().size());
      if (base.getPlayers().size() >= base.getMaximumSize()) {
        itemStack = new ItemBuilder(itemStack).lore(fullTeam).build();
      } else {
        itemStack = new ItemBuilder(itemStack).lore(emptyTeam).build();
      }
      if (base.getPlayers().contains(player)) {
        itemStack = new ItemBuilder(itemStack).lore(insideTeam).build();
      }
      itemStack = new ItemBuilder(itemStack).name(teamName).build();
      pane.addItem(new GuiItem(itemStack, e -> {
        e.setCancelled(true);
        if (!(e.getWhoClicked() instanceof Player) || !(e.isLeftClick() || e.isRightClick())) {
          return;
        }
        if (!ItemUtils.isItemStackNamed(e.getCurrentItem())) {
          return;
        }
        TBPlayerChooseBaseEvent event = new TBPlayerChooseBaseEvent(player, base, arena);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
          return;
        }
        if (base.getPlayers().contains(player)) {
          player.sendMessage(plugin.getChatManager().colorMessage("Already member of this base"));
          return;
        }
        if (base.getPlayers().size() >= base.getMaximumSize()) {
          player.sendMessage(plugin.getChatManager().colorMessage("This base is full!"));
          return;
        }
        if (arena.inBase(player)){
          arena.getBase(player).removePlayer(player);
        }
        base.addPlayer(player);
        player.sendMessage(plugin.getChatManager().colorMessage("base choose message"));
      }), x, y);
      x++;
      if (x == 9) {
        x = 0;
        y++;
      }
    }
    gui.show(player);
  }

  @EventHandler
  public void onBaseMenuItemClick(PlayerInteractEvent e) {
    if (!(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) {
      return;
    }
    ItemStack stack = e.getPlayer().getInventory().getItemInMainHand();
    if (!stack.equals(baseItem.getItemStack())) {
      return;
    }
    Arena arena = ArenaRegistry.getArena(e.getPlayer());
    if (arena == null) {
      return;
    }
    e.setCancelled(true);
    createMenu(e.getPlayer(), arena);
  }

}
