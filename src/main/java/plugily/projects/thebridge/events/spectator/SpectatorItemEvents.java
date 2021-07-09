
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

package plugily.projects.thebridge.events.spectator;

import plugily.projects.inventoryframework.gui.GuiItem;
import plugily.projects.inventoryframework.gui.type.ChestGui;
import plugily.projects.inventoryframework.pane.OutlinePane;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import plugily.projects.commonsbox.minecraft.compat.VersionUtils;
import plugily.projects.commonsbox.minecraft.compat.events.api.CBPlayerInteractEvent;
import plugily.projects.commonsbox.minecraft.compat.xseries.XMaterial;
import plugily.projects.commonsbox.minecraft.item.ItemUtils;
import plugily.projects.commonsbox.minecraft.misc.stuff.ComplementAccessor;
import plugily.projects.commonsbox.number.NumberUtils;
import plugily.projects.thebridge.Main;
import plugily.projects.thebridge.arena.Arena;
import plugily.projects.thebridge.arena.ArenaManager;
import plugily.projects.thebridge.arena.ArenaRegistry;
import plugily.projects.thebridge.handlers.ChatManager;
import plugily.projects.thebridge.handlers.items.SpecialItemManager;
import plugily.projects.thebridge.utils.Utils;

import java.util.Collections;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 23.11.2020
 */
public class SpectatorItemEvents implements Listener {

  private final Main plugin;
  private final ChatManager chatManager;
  private final SpectatorSettingsMenu spectatorSettingsMenu;

  public SpectatorItemEvents(Main plugin) {
    this.plugin = plugin;
    chatManager = plugin.getChatManager();
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
    spectatorSettingsMenu = new SpectatorSettingsMenu(plugin, chatManager.colorMessage("In-Game.Spectator.Settings-Menu.Inventory-Name"),
        chatManager.colorMessage("In-Game.Spectator.Settings-Menu.Speed-Name"));
  }

  @EventHandler
  public void onSpectatorItemClick(CBPlayerInteractEvent e) {
    if(e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.PHYSICAL) {
      return;
    }
    Arena arena = ArenaRegistry.getArena(e.getPlayer());
    ItemStack stack = VersionUtils.getItemInHand(e.getPlayer());
    if(arena == null || !ItemUtils.isItemStackNamed(stack)) {
      return;
    }
    if(plugin.getSpecialItemManager().getRelatedSpecialItem(stack).getName().equals(SpecialItemManager.SpecialItems.PLAYERS_LIST.getName())) {
      e.setCancelled(true);
      openSpectatorMenu(e.getPlayer(), arena);
    } else if(plugin.getSpecialItemManager().getRelatedSpecialItem(stack).getName().equals(SpecialItemManager.SpecialItems.SPECTATOR_OPTIONS.getName())) {
      e.setCancelled(true);
      spectatorSettingsMenu.openSpectatorSettingsMenu(e.getPlayer());
    } else if(plugin.getSpecialItemManager().getRelatedSpecialItem(stack).getName().equals(SpecialItemManager.SpecialItems.SPECTATOR_LEAVE_ITEM.getName())) {
      e.setCancelled(true);
      ArenaManager.leaveAttempt(e.getPlayer(), arena);
    }
  }

  private void openSpectatorMenu(Player player, Arena arena) {
    int rows = Utils.serializeInt(arena.getPlayers().size()) / 9;
    ChestGui gui = new ChestGui(rows, chatManager.colorMessage("In-Game.Spectator.Spectator-Menu-Name"));
    OutlinePane pane = new OutlinePane(9, rows);
    gui.addPane(pane);

    ItemStack skull = XMaterial.PLAYER_HEAD.parseItem();

    for(Player arenaPlayer : arena.getPlayers()) {
      if(plugin.getUserManager().getUser(arenaPlayer).isSpectator()) {
        continue;
      }
      ItemStack cloneSkull = skull.clone();
      SkullMeta meta = VersionUtils.setPlayerHead(arenaPlayer, (SkullMeta) cloneSkull.getItemMeta());
      ComplementAccessor.getComplement().setDisplayName(meta, arenaPlayer.getName());
      ComplementAccessor.getComplement().setLore(meta, Collections.singletonList(chatManager.colorMessage("In-Game.Spectator.Target-Player-Health")
          .replace("%health%", Double.toString(NumberUtils.round(arenaPlayer.getHealth(), 2)))));
      cloneSkull.setItemMeta(meta);
      pane.addItem(new GuiItem(cloneSkull, e -> {
        e.setCancelled(true);
        e.getWhoClicked().sendMessage(plugin.getChatManager().formatMessage(arena, chatManager.colorMessage("Commands.Admin-Commands.Teleported-To-Player"), arenaPlayer));
        e.getWhoClicked().closeInventory();
        e.getWhoClicked().teleport(arenaPlayer);
      }));
    }
    gui.show(player);
  }

}
