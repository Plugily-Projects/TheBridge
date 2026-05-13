package plugily.projects.thebridge.kits;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.utils.misc.ColorUtil;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XItemStack;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XMaterial;
import plugily.projects.thebridge.arena.Arena;

import java.util.Objects;
import java.util.Optional;

public class KitUtils {

  public static ItemStack handleItem(PluginMain plugin, Player player, ItemStack itemOriginal) {
    Arena arena = (Arena) plugin.getArenaRegistry().getArena(player);
    ItemStack itemStack = itemOriginal.clone();

    if(arena == null) {
      plugin.getDebugger().performance("Kit", "Handle item method called for player {1} item stack {2} but the arena was null.", player, itemStack);
      return itemStack;
    }
    plugin.getDebugger().performance("Kit", "Arena {0} Handle item method called for player {1} item stack {2}.", arena.getId(), player, itemStack);

    // Replaces white terracotta with coloured terracotta if the player is in a team
    if(XMaterial.matchXMaterial(plugin.getConfig().getString("Kit.Colored-Block-Replace-Material", "WHITE_WOOL")).orElse(XMaterial.WHITE_WOOL).isSimilar(itemStack)) {
      Optional<XMaterial> material = XMaterial.matchXMaterial(arena.getBase(player).getMaterialColor().toUpperCase() + "_" + plugin.getConfig().getString("Kit.Colored-Block-Material", "WOOL"));
      material.ifPresent(xMaterial -> {
        xMaterial.setType(itemStack);
      });
      plugin.getDebugger().performance("Kit", "Arena {0} Changing coloured block to {1}.", arena.getId(), arena.getBase(player).getMaterialColor().toUpperCase() + "_" + plugin.getConfig().getString("Kit.Colored-Block-Material", "WOOL"));
      return itemStack;
    }

    // Replaces leather armour with the coloured leather armour if the player is in a team
    if(XMaterial.LEATHER_HELMET.isSimilar(itemStack) || XMaterial.LEATHER_CHESTPLATE.isSimilar(itemStack) || XMaterial.LEATHER_LEGGINGS.isSimilar(itemStack) || XMaterial.LEATHER_BOOTS.isSimilar(itemStack)) {
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
