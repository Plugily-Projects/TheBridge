package plugily.projects.thebridge.handlers.setup;

import plugily.projects.minigamesbox.classic.handlers.setup.categories.PluginCountableCategory;
import plugily.projects.minigamesbox.classic.handlers.setup.items.category.CountItem;
import plugily.projects.minigamesbox.classic.handlers.setup.items.category.MultiLocationItem;
import plugily.projects.minigamesbox.classic.utils.helper.ItemBuilder;
import plugily.projects.minigamesbox.classic.utils.serialization.LocationSerializer;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XMaterial;
import plugily.projects.minigamesbox.inventory.normal.NormalFastInv;


/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 05.09.2022
 */
public class CountableCategory extends PluginCountableCategory {
  @Override
  public void addItems(NormalFastInv gui) {
    super.addItems(gui);

    CountItem maximumSize = new CountItem(getSetupInventory(), new ItemBuilder(XMaterial.REDSTONE.parseMaterial()), "Maximum Players Per Base", "How many players one base can hold", "maximumsize");
    gui.setItem((getInventoryLine() * 9) + 3, maximumSize);
    getItemList().add(maximumSize);

    CountItem modeValue = new CountItem(getSetupInventory(), new ItemBuilder(XMaterial.REDSTONE_TORCH.parseMaterial()), "Mode Value", "How many points until the mode choose winner", "modevalue");
    gui.setItem((getInventoryLine() * 9) + 4, modeValue);
    getItemList().add(modeValue);

    CountItem resetBlocks = new CountItem(getSetupInventory(), new ItemBuilder(XMaterial.REDSTONE_LAMP.parseMaterial()), "reset blocks round", "After how many rounds should we reset blocks? \n Disable by setting the value to 0 in arenas.yml!", "resetblocks");
    gui.setItem((getInventoryLine() * 9) + 5, resetBlocks);
    getItemList().add(resetBlocks);
  }

}