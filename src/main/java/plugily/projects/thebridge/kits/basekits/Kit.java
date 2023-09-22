package plugily.projects.thebridge.kits.basekits;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Kit extends plugily.projects.minigamesbox.classic.kits.basekits.Kit {

  @Override
  public boolean isUnlockedByPlayer(Player player) {
    return true;
  }

  @Override
  public ItemStack handleItem(Player player, ItemStack itemStack) {
    return KitUtil.handleItem(getPlugin(), player, itemStack);
  }
}
