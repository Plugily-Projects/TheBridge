package plugily.projects.thebridge.kits;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.utils.misc.ColorUtil;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XMaterial;
import plugily.projects.thebridge.arena.Arena;

import java.util.Objects;
import java.util.Optional;

public class KitUtils {

  public static ItemStack handleItem(PluginMain plugin, Player player, ItemStack itemOriginal) {
    Arena arena = (Arena) plugin.getArenaRegistry().getArena(player);
    ItemStack itemStack = itemOriginal.clone();

    if (arena == null) {
      plugin.getDebugger().performance("Kit", "Handle item method called for player {1} item stack {2} but the arena was null.", player, itemStack);
      return itemStack;
    }
    plugin.getDebugger().performance("Kit", "Arena {0} Handle item method called for player {1} item stack {2}.", arena.getId(), player, itemStack);

    // Replaces white terracotta with coloured terracotta if the player is in a team
    if (itemStack.getType().equals(XMaterial.RED_TERRACOTTA.parseMaterial()) || itemStack.getType().equals(XMaterial.WHITE_TERRACOTTA.parseMaterial())) {
      Optional<XMaterial> material = XMaterial.matchXMaterial(arena.getBase(player).getMaterialColor().toUpperCase() + "_TERRACOTTA");
      material.ifPresent(xMaterial -> itemStack.setType(Objects.requireNonNull(xMaterial.parseMaterial())));
      plugin.getDebugger().performance("Kit", "Arena {0} Changing terracotta to {1}.", arena.getId(), arena.getBase(player).getMaterialColor().toUpperCase() + "_TERRACOTTA");
      return itemStack;
    }

    // Replaces leather armour with the coloured leather armour if the player is in a team
    if (itemStack.getType().equals(XMaterial.LEATHER_HELMET.parseMaterial()) || itemStack.getType().equals(XMaterial.LEATHER_CHESTPLATE.parseMaterial()) || itemStack.getType().equals(XMaterial.LEATHER_LEGGINGS.parseMaterial()) || itemStack.getType().equals(XMaterial.LEATHER_BOOTS.parseMaterial())) {
      LeatherArmorMeta itemMeta = (LeatherArmorMeta) itemStack.getItemMeta();
      assert itemMeta != null;
      itemMeta.setColor(ColorUtil.fromChatColor(ChatColor.valueOf(arena.getBase(player).getColor().toUpperCase())));
      itemStack.setItemMeta(itemMeta);
      plugin.getDebugger().performance("Kit", "Arena {0} Changing armour to {1}.", arena.getId(), arena.getBase(player).getColor().toUpperCase());
      return itemStack;
    }

    return itemStack;
  }

}
