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

package plugily.projects.thebridge.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import pl.plajerlair.commonsbox.minecraft.item.ItemBuilder;
import plugily.projects.thebridge.Main;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Tigerpanzer
 * <p>
 * Created at 18.12.2020
 */
public class CuboidSelector implements Listener {

  private Main plugin;
  private final Map<Player, Selection> selections = new HashMap<>();

  public CuboidSelector(Main plugin) {
    this.plugin = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  public void giveSelectorWand(Player p) {
    ItemStack stack = new ItemBuilder(Material.BLAZE_ROD)
      .name(plugin.getChatManager().colorRawMessage("&6&lLocation wand"))
      .lore(plugin.getChatManager().colorRawMessage("Use this tool to set up location cuboids"))
      .lore(plugin.getChatManager().colorRawMessage("Set the first corner with left click"))
      .lore(plugin.getChatManager().colorRawMessage("and the second with right click"))
      .build();
    p.getInventory().addItem(stack);

    p.sendMessage(plugin.getChatManager().colorRawMessage(plugin.getChatManager().getPrefix() + "&eYou received Location wand!"));
    p.sendMessage(plugin.getChatManager().colorRawMessage(plugin.getChatManager().getPrefix() + "&eSelect bottom corner using left click!"));
  }

  public Selection getSelection(Player p) {
    return selections.getOrDefault(p, null);
  }

  public void removeSelection(Player p) {
    selections.remove(p);
  }

  @EventHandler
  public void onWandUse(PlayerInteractEvent e) {
    if (!Utils.isNamed(e.getItem()) || !e.getItem().getItemMeta().getDisplayName().equals(plugin.getChatManager().colorRawMessage("&6&lLocation wand"))) {
      return;
    }
    e.setCancelled(true);
    switch (e.getAction()) {
      case LEFT_CLICK_BLOCK:
        selections.put(e.getPlayer(), new Selection(e.getClickedBlock().getLocation(), null));
        e.getPlayer().sendMessage(plugin.getChatManager().colorRawMessage(plugin.getChatManager().getPrefix() + "&eNow select top corner using right click!"));
        break;
      case RIGHT_CLICK_BLOCK:
        if (!selections.containsKey(e.getPlayer())) {
          e.getPlayer().sendMessage(plugin.getChatManager().colorRawMessage(plugin.getChatManager().getPrefix() + "&cPlease select bottom corner using left click first!"));
          break;
        }
        selections.put(e.getPlayer(), new Selection(selections.get(e.getPlayer()).getFirstPos(), e.getClickedBlock().getLocation()));
        e.getPlayer().sendMessage(plugin.getChatManager().colorRawMessage(plugin.getChatManager().getPrefix() + "&eNow you can add Location via menu!"));
        break;
      case LEFT_CLICK_AIR:
        selections.put(e.getPlayer(), new Selection(e.getPlayer().getLocation(), null));
        e.getPlayer().sendMessage(plugin.getChatManager().colorRawMessage(plugin.getChatManager().getPrefix() + "&eNow select top corner using right click!"));
        e.getPlayer().sendMessage(plugin.getChatManager().colorRawMessage(plugin.getChatManager().getPrefix() + "&cPlease keep in mind to use blocks instead of player location for precise coordinates!"));
        break;
      case RIGHT_CLICK_AIR:
        if (!selections.containsKey(e.getPlayer())) {
          e.getPlayer().sendMessage(plugin.getChatManager().colorRawMessage(plugin.getChatManager().getPrefix() + "&cPlease select bottom corner using left click first!"));
          break;
        }
        selections.put(e.getPlayer(), new Selection(selections.get(e.getPlayer()).getFirstPos(), e.getPlayer().getLocation()));
        e.getPlayer().sendMessage(plugin.getChatManager().colorRawMessage(plugin.getChatManager().getPrefix() + "&eNow you can add Location via menu!"));
        e.getPlayer().sendMessage(plugin.getChatManager().colorRawMessage(plugin.getChatManager().getPrefix() + "&cPlease keep in mind to use blocks instead of player location for precise coordinates!"));
        break;
       default:
        break;
    }
  }

  public class Selection {

    private Location firstPos;
    private Location secondPos;

    public Selection(Location firstPos, Location secondPos) {
      this.firstPos = firstPos;
      this.secondPos = secondPos;
    }

    public Location getFirstPos() {
      return firstPos;
    }

    public Location getSecondPos() {
      return secondPos;
    }
  }

}
