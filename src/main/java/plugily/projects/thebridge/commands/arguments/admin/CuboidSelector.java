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

package plugily.projects.thebridge.commands.arguments.admin;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import plugily.projects.minigamesbox.classic.handlers.language.MessageBuilder;
import plugily.projects.minigamesbox.classic.utils.helper.ItemBuilder;
import plugily.projects.thebridge.Main;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Tigerpanzer
 *     <p>Created at 18.12.2020
 */
public class CuboidSelector implements Listener {

  private final Main plugin;
  private final Map<Player, Selection> selections = new HashMap<>();

  public CuboidSelector(Main plugin) {
    this.plugin = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  public void giveSelectorWand(Player p) {
    ItemStack stack =
        new ItemBuilder(Material.BLAZE_ROD)
            .name(new MessageBuilder("&6&lLocation wand").build())
            .lore(new MessageBuilder("Use this tool to set up location cuboids").build())
            .lore(new MessageBuilder("Set the first corner with left click").build())
            .lore(new MessageBuilder("and the second with right click").build())
            .build();
    p.getInventory().addItem(stack);

    p.sendMessage(new MessageBuilder("&eYou received Location wand!").prefix().build());
    p.sendMessage(new MessageBuilder("&eSelect bottom corner using left click!").prefix().build());
  }

  public Selection getSelection(Player p) {
    return selections.getOrDefault(p, null);
  }

  public void removeSelection(Player p) {
    selections.remove(p);
  }

  @EventHandler
  public void onWandUse(PlayerInteractEvent e) {
    if (!plugin.getBukkitHelper().isNamed(e.getItem())
        || !e.getItem()
            .getItemMeta()
            .getDisplayName()
            .equals(new MessageBuilder("&6&lLocation wand").build())) {
      return;
    }
    e.setCancelled(true);
    switch (e.getAction()) {
      case LEFT_CLICK_BLOCK:
        selections.put(e.getPlayer(), new Selection(e.getClickedBlock().getLocation(), null));
        e.getPlayer()
            .sendMessage(
                new MessageBuilder("&eNow select top corner using right click!").prefix().build());
        break;
      case RIGHT_CLICK_BLOCK:
        if (!selections.containsKey(e.getPlayer())) {
          e.getPlayer()
              .sendMessage(
                  new MessageBuilder("&cPlease select bottom corner using left click first!")
                      .prefix()
                      .build());
          break;
        }
        selections.put(
            e.getPlayer(),
            new Selection(
                selections.get(e.getPlayer()).getFirstPos(), e.getClickedBlock().getLocation()));
        e.getPlayer()
            .sendMessage(
                new MessageBuilder("&eNow you can add Location via menu!").prefix().build());
        break;
      case LEFT_CLICK_AIR:
        selections.put(e.getPlayer(), new Selection(e.getPlayer().getLocation(), null));
        e.getPlayer()
            .sendMessage(
                new MessageBuilder("&eNow select top corner using right click!").prefix().build());
        e.getPlayer()
            .sendMessage(
                new MessageBuilder(
                        "&cPlease keep in mind to use blocks instead of player location for precise coordinates!")
                    .prefix()
                    .build());
        break;
      case RIGHT_CLICK_AIR:
        if (!selections.containsKey(e.getPlayer())) {
          e.getPlayer()
              .sendMessage(
                  new MessageBuilder("&cPlease select bottom corner using left click first!")
                      .prefix()
                      .build());
          break;
        }
        selections.put(
            e.getPlayer(),
            new Selection(
                selections.get(e.getPlayer()).getFirstPos(), e.getPlayer().getLocation()));
        e.getPlayer()
            .sendMessage(
                new MessageBuilder("&eNow you can add Location via menu!").prefix().build());
        e.getPlayer()
            .sendMessage(
                new MessageBuilder(
                        "&cPlease keep in mind to use blocks instead of player location for precise coordinates!")
                    .prefix()
                    .build());
        break;
      default:
        break;
    }
  }

  public static class Selection {

    private final Location firstPos;
    private final Location secondPos;

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
