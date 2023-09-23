package plugily.projects.thebridge.kits;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import plugily.projects.minigamesbox.classic.PluginMain;
import plugily.projects.minigamesbox.classic.kits.basekits.Kit;
import plugily.projects.minigamesbox.classic.utils.version.xseries.XItemStack;
import plugily.projects.thebridge.kits.base.FreeKit;
import plugily.projects.thebridge.kits.base.LevelKit;
import plugily.projects.thebridge.kits.base.PremiumKit;

import java.util.HashMap;
import java.util.Objects;
import java.util.logging.Level;

public class KitRegistry extends plugily.projects.minigamesbox.classic.kits.KitRegistry {
  public KitRegistry(PluginMain plugin) {
    super(plugin);
  }

  @Override
  public void registerKits() {
    for (String key : kitsConfig.getKeys(false)) {
      if (!Objects.equals(key, "Do-Not-Edit")) {
        loadKitConfig(key);
      }
    }
  }

  @Override
  public void loadKitConfig(String kit_key) {
    ConfigurationSection configurationSection = kitsConfig.getConfigurationSection(kit_key);

    if (configurationSection == null) {
      plugin.getDebugger().debug(Level.SEVERE, "Kit " + kit_key + " does not have any configuration.");
      plugin.getDebugger().debug(Level.SEVERE, "Kit" + kit_key + " will not be loaded.");
      return;
    }

    String kit_name;
    if (configurationSection.getString("name") == null) {
      kit_name = kit_key;
    }
    else {
      kit_name = configurationSection.getString("name");
    }

    if (configurationSection.getConfigurationSection("display_item") == null) {
      configurationSection.set("display_item", XItemStack.serialize(new ItemStack(Material.BEDROCK)));
    }

    ItemStack itemStack = XItemStack.deserialize(Objects.requireNonNull(configurationSection.getConfigurationSection("display_item")));

    if(!configurationSection.getBoolean("enabled", false)) {
      plugin.getDebugger().debug("Kit " + kit_key + " is disabled by kits.yml");
      return;
    }

    String kitType = configurationSection.getString("kit_type");

    if (kitType == null) {
      plugin.getDebugger().debug(Level.SEVERE, "Kit " + kit_key + " kit_type is null.");
      plugin.getDebugger().debug(Level.SEVERE, "Kit" + kit_key + " will not be loaded.");
      return;
    }

    Kit kit;

    switch (kitType) {
      case "free": {
        kit = new FreeKit(kit_key, kit_name, itemStack);
        break;
      }
      case "level": {
        kit = new LevelKit(kit_key, kit_name, itemStack);
        ((LevelKit) kit).setLevel(configurationSection.getInt("required-level"));
        break;
      }
      case "premium": {
        kit = new PremiumKit(kit_key, kit_name, itemStack);
        break;
      }
      default: {
        plugin.getDebugger().debug(Level.SEVERE, "Kit " + kit_key + " kit_type is not recognised.");
        plugin.getDebugger().debug(Level.SEVERE, "Kit" + kit_key + " will not be loaded.");
        return;
      }
    }

    if (configurationSection.getString("unlockedOnDefault") == null) {
      plugin.getDebugger().debug(Level.SEVERE, "Kit " + kit_key + " does not have an unlockedOnDefault configuration.");
      plugin.getDebugger().debug(Level.SEVERE, "Kit" + kit_key + " will not be loaded.");
      return;
    }
    kit.setUnlockedOnDefault(configurationSection.getBoolean("unlockedOnDefault"));

    HashMap<ItemStack, Integer> kitItems = new HashMap<>();

    ConfigurationSection inventoryConfigurationSection = configurationSection.getConfigurationSection("inventory");
    if (inventoryConfigurationSection != null) {
      inventoryConfigurationSection.getKeys(false).forEach((k) -> {

        ConfigurationSection itemConfigurationSection = inventoryConfigurationSection.getConfigurationSection(k);
        assert itemConfigurationSection != null;

        ConfigurationSection itemStackConfigurationSection = itemConfigurationSection.getConfigurationSection("item");
        assert itemStackConfigurationSection != null;
        ItemStack item = XItemStack.deserialize(itemStackConfigurationSection);
        Integer slot = itemConfigurationSection.getInt("slot");

        kitItems.put(item, slot);
      });
      kit.setKitItems(kitItems);
    }
    else {
      plugin.getDebugger().debug(Level.SEVERE, "The kit " + kit.getKey() + " does not have an inventory configuration section.");
      plugin.getDebugger().debug(Level.SEVERE, "The kit " + kit.getKey() + " will not give any inventory items.");
    }


    ConfigurationSection armourConfigurationSection = configurationSection.getConfigurationSection("armour");
    if (armourConfigurationSection != null) {

      ConfigurationSection helmetConfigurationSection = armourConfigurationSection.getConfigurationSection("helmet");
      if (helmetConfigurationSection != null) {
        kit.setKitHelmet(XItemStack.deserialize(helmetConfigurationSection));
      }

      ConfigurationSection chestplateConfigurationSection = armourConfigurationSection.getConfigurationSection("chestplate");
      if (chestplateConfigurationSection != null) {
        kit.setKitChestplate(XItemStack.deserialize(chestplateConfigurationSection));
      }

      ConfigurationSection leggingsConfigurationSection = armourConfigurationSection.getConfigurationSection("leggings");
      if (leggingsConfigurationSection != null) {
        kit.setKitLeggings(XItemStack.deserialize(leggingsConfigurationSection));
      }

      ConfigurationSection bootsConfigurationSection = armourConfigurationSection.getConfigurationSection("boots");
      if (bootsConfigurationSection != null) {
        kit.setKitBoots(XItemStack.deserialize(bootsConfigurationSection));
      }
    }
    else {
      plugin.getDebugger().debug(Level.SEVERE, "The kit " + kit.getKey() + " does not have an armour configuration section.");
      plugin.getDebugger().debug(Level.SEVERE, "The kit " + kit.getKey() + " will not give any armour items.");
    }

    if (Objects.equals(configurationSection.getBoolean("default_kit"), true)) {
      this.setDefaultKit(kit);
    }

    kits.add(kit);


  }
}
