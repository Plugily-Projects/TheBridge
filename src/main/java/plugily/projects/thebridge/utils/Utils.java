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

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import pl.plajerlair.commonsbox.minecraft.compat.ServerVersion;
import pl.plajerlair.commonsbox.minecraft.compat.VersionUtils;
import pl.plajerlair.commonsbox.string.StringFormatUtils;
import plugily.projects.thebridge.Main;
import plugily.projects.thebridge.arena.ArenaRegistry;
import plugily.projects.thebridge.arena.ArenaState;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Tigerpanzer_02, 2Wild4You
 * <p>
 * Created at 31.10.2020
 */
public class Utils {

  private static Main plugin;

  private Utils() {
  }

  public static void init(Main plugin) {
    Utils.plugin = plugin;
  }

  /**
   * Serialize int to use it in Inventories size
   * ex. you have 38 kits and it will serialize it to 45 (9*5)
   * because it is valid inventory size
   * next ex. you have 55 items and it will serialize it to 63 (9*7) not 54 because it's too less
   *
   * @param i integer to serialize
   * @return serialized number
   */
  public static int serializeInt(Integer i) {
    if(i == 0) return 9; //The function bellow doesn't work if i == 0, so return 9 in case that happens.
    return (i % 9) == 0 ? i : (i + 9 - 1) / 9 * 9;
  }

  /**
   * Checks whether itemstack is named (not null, has meta and display name)
   *
   * @param stack item stack to check
   * @return true if named, false otherwise
   */
  public static boolean isNamed(ItemStack stack) {
    return stack != null && stack.hasItemMeta() && stack.getItemMeta().hasDisplayName();
  }

  public static void applyActionBarCooldown(Player p, int seconds) {
    new BukkitRunnable() {
      int ticks = 0;

      @Override
      public void run() {
        if(!ArenaRegistry.isInArena(p) || ArenaRegistry.getArena(p).getArenaState() != ArenaState.IN_GAME) {
          this.cancel();
        }
        if(ticks >= seconds * 20) {
          this.cancel();
        }
        String progress = StringFormatUtils.getProgressBar(ticks, seconds * 20, 10, "■", ChatColor.COLOR_CHAR + "a", ChatColor.COLOR_CHAR + "c");
        VersionUtils.sendActionBar(p, plugin.getChatManager().colorMessage("In-Game.Cooldown-Format", p)
            .replace("%progress%", progress).replace("%time%", String.valueOf((double) ((seconds * 20) - ticks) / 20)));
        ticks += 10;
      }
    }.runTaskTimer(plugin, 0, 10);
  }

  public static List<Block> getNearbyBlocks(Location location, int radius) {
    List<Block> blocks = new ArrayList<>();
    for(int x = location.getBlockX() - radius; x <= location.getBlockX() + radius; x++) {
      for(int y = location.getBlockY() - radius; y <= location.getBlockY() + radius; y++) {
        for(int z = location.getBlockZ() - radius; z <= location.getBlockZ() + radius; z++) {
          blocks.add(location.getWorld().getBlockAt(x, y, z));
        }
      }
    }
    return blocks;
  }

  public static Location getBlockCenter(Location location) {
    return location.clone().add(0.5, 0, 0.5);
  }

  public static boolean checkIsInGameInstance(Player player) {
    if(!ArenaRegistry.isInArena(player)) {
      player.sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage("Commands.Not-Playing", player));
      return false;
    }
    return true;
  }

  public static boolean hasPermission(CommandSender sender, String perm) {
    if(sender.hasPermission(perm)) {
      return true;
    }
    sender.sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage("Commands.No-Permission"));
    return false;
  }

  public static Vector rotateAroundAxisX(Vector v, double angle) {
    angle = Math.toRadians(angle);
    double cos = Math.cos(angle),
      sin = Math.sin(angle),
      y = v.getY() * cos - v.getZ() * sin,
      z = v.getY() * sin + v.getZ() * cos;
    return v.setY(y).setZ(z);
  }

  public static Vector rotateAroundAxisY(Vector v, double angle) {
    angle = -angle;
    angle = Math.toRadians(angle);
    double cos = Math.cos(angle),
      sin = Math.sin(angle),
      x = v.getX() * cos + v.getZ() * sin,
      z = v.getX() * -sin + v.getZ() * cos;
    return v.setX(x).setZ(z);
  }

  public static String matchColorRegex(String s) {
    String regex = "&?#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})";
    Matcher matcher = Pattern.compile(regex).matcher(s);
    while(matcher.find()) {
      String group = matcher.group(0);
      String group2 = matcher.group(1);

      try {
        s = s.replace(group, net.md_5.bungee.api.ChatColor.of("#" + group2) + "");
      } catch(Exception e) {
        Debugger.debug("Bad hex color match: " + group);
      }
    }

    return s;
  }

  public static List<String> splitString(String string, int max) {
    List<String> matchList = new ArrayList<>();
    Pattern regex = Pattern.compile(".{1," + max + "}(?:\\s|$)", Pattern.DOTALL);
    Matcher regexMatcher = regex.matcher(string);
    while(regexMatcher.find()) {
      matchList.add(plugin.getChatManager().colorRawMessage("&7") + regexMatcher.group());
    }
    return matchList;
  }

  public static ItemStack getPotion(PotionType type, int tier, boolean splash) {
    ItemStack potion = new ItemStack(!splash ? Material.POTION : Material.SPLASH_POTION, 1);
    PotionMeta meta = (PotionMeta) potion.getItemMeta();
    meta.setBasePotionData(new PotionData(type, false, tier >= 2 && !splash));
    potion.setItemMeta(meta);
    return potion;
  }

  /**
   * @param s string to check whether is integer number
   * @return true if it is, false otherwise, like 12a, 12.03 33333333333333 etc.
   */
  public static boolean isInteger(String s) {
    try {
      Integer.parseInt(s);
      return true;
    } catch(NumberFormatException ex) {
      return false;
    }
  }

}
