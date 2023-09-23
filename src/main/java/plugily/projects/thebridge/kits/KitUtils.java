package plugily.projects.thebridge.kits;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import plugily.projects.minigamesbox.classic.utils.misc.ColorUtil;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XMaterial;
import plugily.projects.thebridge.arena.Arena;

import java.util.Objects;
import java.util.Optional;

public class KitUtils {

  public static ItemStack handleItem(Arena arena, Player player, ItemStack itemStack) {

    if (arena == null) {
      return itemStack;
    }

    // Replaces white terracotta with coloured terracotta if the player is in a team
    if (itemStack.getType().equals(Material.RED_TERRACOTTA) || itemStack.getType().equals(Material.WHITE_TERRACOTTA)) {
      Optional<XMaterial> material = XMaterial.matchXMaterial(arena.getBase(player).getMaterialColor().toUpperCase() + "_TERRACOTTA");
      material.ifPresent(xMaterial -> itemStack.setType(Objects.requireNonNull(xMaterial.parseMaterial())));
      player.sendMessage("§aYour terracotta has been changed to §6" + arena.getBase(player).getMaterialColor().toUpperCase() + "_TERRACOTTA");
      return itemStack;
    }

    // Replaces leather armour with the coloured leather armour if the player is in a team
    if (itemStack.getType().equals(Material.LEATHER_HELMET) || itemStack.getType().equals(Material.LEATHER_CHESTPLATE) || itemStack.getType().equals(Material.LEATHER_LEGGINGS) || itemStack.getType().equals(Material.LEATHER_BOOTS)) {
      LeatherArmorMeta itemMeta = (LeatherArmorMeta) itemStack.getItemMeta();
      assert itemMeta != null;
      itemMeta.setColor(ColorUtil.fromChatColor(ChatColor.valueOf(arena.getBase(player).getColor().toUpperCase())));
      itemStack.setItemMeta(itemMeta);
      return itemStack;
    }

    return itemStack;
  }

}
