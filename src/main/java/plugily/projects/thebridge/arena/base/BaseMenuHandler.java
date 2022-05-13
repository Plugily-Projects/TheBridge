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

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import plugily.projects.minigamesbox.classic.handlers.items.SpecialItem;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.utils.helper.ItemBuilder;
import plugily.projects.minigamesbox.classic.utils.version.VersionUtils;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XMaterial;
import plugily.projects.minigamesbox.inventory.common.item.SimpleClickableItem;
import plugily.projects.minigamesbox.inventory.normal.NormalFastInv;
import plugily.projects.thebridge.Main;
import plugily.projects.thebridge.api.events.player.TBPlayerChooseBaseEvent;
import plugily.projects.thebridge.arena.Arena;

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

    this.baseItem = plugin.getSpecialItemManager().getSpecialItem("BASE_SELECTOR");
    fullTeam = new MessageBuilder("BASES_TEAM_FULL").asKey().build();
    emptyTeam = new MessageBuilder("BASES_TEAM_EMPTY").asKey().build();
    insideTeam = new MessageBuilder("BASES_TEAM_INSIDE").asKey().build();
    teamName = new MessageBuilder("BASES_TEAM_NAME").asKey().build();
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  public void createMenu(Player player, Arena arena) {
    NormalFastInv gui =
        new NormalFastInv(
            plugin.getBukkitHelper().serializeInt(plugin.getKitRegistry().getKits().size()) / 9,
            new MessageBuilder("BASES_TEAM_MENU").asKey().build());
    for(Base base : arena.getBases()) {
      ItemStack itemStack =
          XMaterial.matchXMaterial(base.getMaterialColor().toUpperCase() + "_WOOL")
              .get()
              .parseItem();
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
      itemStack =
          new ItemBuilder(itemStack)
              .name(teamName.replace("%base%", base.getFormattedColor()))
              .build();
      gui.addItem(
          new SimpleClickableItem(
              itemStack,
              e -> {
                e.setCancelled(true);
                if(!(e.getWhoClicked() instanceof Player)
                    || !(e.isLeftClick() || e.isRightClick())) {
                  return;
                }
                TBPlayerChooseBaseEvent event = new TBPlayerChooseBaseEvent(player, base, arena);
                Bukkit.getPluginManager().callEvent(event);
                if(event.isCancelled()) {
                  return;
                }
                if(!base.addPlayer(player)) {
                  return;
                }

                player.sendMessage(new MessageBuilder("BASES_TEAM_CHOOSE").asKey().build().replace("%base%", base.getFormattedColor()));
                e.getWhoClicked().closeInventory();
              }));
    }
    gui.refresh();
    gui.open(player);
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
    Arena arena = plugin.getArenaRegistry().getArena(e.getPlayer());
    if(arena == null) {
      return;
    }
    e.setCancelled(true);
    createMenu(e.getPlayer(), arena);
  }
}
