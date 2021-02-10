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
import pl.plajerlair.commonsbox.minecraft.compat.VersionUtils;
import pl.plajerlair.commonsbox.minecraft.compat.XMaterial;
import pl.plajerlair.commonsbox.minecraft.item.ItemBuilder;
import plugily.projects.thebridge.Main;
import plugily.projects.thebridge.api.events.player.TBPlayerChooseBaseEvent;
import plugily.projects.thebridge.arena.Arena;
import plugily.projects.thebridge.arena.ArenaRegistry;
import plugily.projects.thebridge.handlers.items.SpecialItem;
import plugily.projects.thebridge.handlers.items.SpecialItemManager;
import plugily.projects.thebridge.kits.KitRegistry;
import plugily.projects.thebridge.utils.Utils;

import java.util.ArrayList;
import java.util.List;

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
    fullTeam = plugin.getChatManager().colorMessage("Bases.Team.Full");
    emptyTeam = plugin.getChatManager().colorMessage("Bases.Team.Empty");
    insideTeam = plugin.getChatManager().colorMessage("Bases.Team.Inside");
    teamName = plugin.getChatManager().colorMessage("Bases.Team.Name");
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  public void createMenu(Player player, Arena arena) {
    Gui gui = new Gui(plugin, Utils.serializeInt(KitRegistry.getKits().size()) / 9, plugin.getChatManager().colorMessage("Bases.Team.Menu-Name"));
    StaticPane pane = new StaticPane(9, gui.getRows());
    gui.addPane(pane);
    int x = 0;
    int y = 0;
    for(Base base : arena.getBases()) {
      ItemStack itemStack = XMaterial.matchXMaterial(base.getColor().toUpperCase() + "_WOOL").get().parseItem();
      itemStack.setAmount(base.getPlayers().size() == 0 ? 1 : base.getPlayers().size());
      if(base.getPlayers().size() >= base.getMaximumSize()) {
        itemStack = new ItemBuilder(itemStack).lore(fullTeam).build();
      } else {
        itemStack = new ItemBuilder(itemStack).lore(emptyTeam).build();
      }
      if(base.getPlayers().size() > 0) {
        List<String> players = new ArrayList<>();
        for(Player inside : base.getPlayers()) {
          players.add("- " + inside.getName());
        }
        itemStack = new ItemBuilder(itemStack).lore(players).build();
      }
      if(base.getPlayers().contains(player)) {
        itemStack = new ItemBuilder(itemStack).lore(insideTeam).build();
      }
      itemStack = new ItemBuilder(itemStack).name(teamName.replace("%base%", base.getFormattedColor())).build();
      pane.addItem(new GuiItem(itemStack, e -> {
        e.setCancelled(true);
        if(!(e.getWhoClicked() instanceof Player) || !(e.isLeftClick() || e.isRightClick())) {
          return;
        }
        TBPlayerChooseBaseEvent event = new TBPlayerChooseBaseEvent(player, base, arena);
        Bukkit.getPluginManager().callEvent(event);
        if(event.isCancelled()) {
          return;
        }
        if(base.getPlayers().contains(player)) {
          player.sendMessage(plugin.getChatManager().colorMessage("Bases.Team.Member"));
          return;
        }
        if(base.getPlayers().size() >= base.getMaximumSize()) {
          player.sendMessage(plugin.getChatManager().colorMessage("Bases.Team.Full"));
          return;
        }
        if(arena.inBase(player)) {
          arena.getBase(player).removePlayer(player);
        }
        base.addPlayer(player);
        player.sendMessage(plugin.getChatManager().colorMessage("Bases.Team.Base-Choose").replace("%base%", base.getFormattedColor()));
        e.getWhoClicked().closeInventory();
      }), x, y);
      x++;
      if(x == 9) {
        x = 0;
        y++;
      }
    }
    gui.show(player);
  }

  @EventHandler
  public void onBaseMenuItemClick(PlayerInteractEvent e) {
    if(!(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) {
      return;
    }
    ItemStack stack = VersionUtils.getItemInHand(e.getPlayer());
    if(!stack.equals(baseItem.getItemStack())) {
      return;
    }
    Arena arena = ArenaRegistry.getArena(e.getPlayer());
    if(arena == null) {
      return;
    }
    e.setCancelled(true);
    createMenu(e.getPlayer(), arena);
  }

}
