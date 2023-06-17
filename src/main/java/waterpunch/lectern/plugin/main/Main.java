package waterpunch.lectern.plugin.main;

import java.util.HashMap;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Barrel;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class Main {

  public static void onlecclick(Player player, Location loc) {
    List<Entity> nearbyEntites = (List<Entity>) loc
      .getWorld()
      .getNearbyEntities(loc, 2, 2, 2);
    // Item Frame
    for (Entity value : nearbyEntites) {
      if (value.getType().toString().endsWith("ITEM_FRAME")) {
        final ItemFrame frame = (ItemFrame) value;
        final ItemStack item = frame.getItem();
        if (item.getType() == Material.WRITTEN_BOOK) {
          player.openBook(item);
          return;
        }
      }
    }
    for (BlockFace face : new BlockFace[] {
      BlockFace.NORTH,
      BlockFace.SOUTH,
      BlockFace.EAST,
      BlockFace.WEST,
    }) {
      if (!(loc.getBlock().getRelative(face).getType() == Material.AIR)) {
        getinventory(loc.getBlock().getRelative(face).getLocation(), player);
      }
    }

    loc.setY(loc.getY() - 1);
    getinventory(loc, player);
  }

  public static void getinventory(Location loc, Player player) {
    int conut = -1, invsize = 27;
    Inventory cash = null, inv = null;
    Barrel barrel = null;
    Chest chest = null;

    inv = Bukkit.createInventory(null, invsize, "Lectern");

    if (
      loc.getBlock().getType() == Material.CHEST ||
      loc.getBlock().getType() == Material.TRAPPED_CHEST ||
      loc.getBlock().getType() == Material.BARREL
    ) {
      if (!(loc.getBlock().getType() == Material.BARREL)) {
        chest = ((Chest) loc.getBlock().getState());
        if (chest.getInventory().getHolder() instanceof DoubleChest) {
          chest = (Chest) loc.getBlock().getState();
          InventoryHolder holder = chest.getInventory().getHolder();
          DoubleChest doubleChest = ((DoubleChest) holder);

          cash = doubleChest.getInventory();
          invsize = 54;
        } else {
          chest = (Chest) loc.getBlock().getState();
          cash = chest.getBlockInventory();
        }
      } else {
        barrel = (Barrel) loc.getBlock().getState();
        cash = barrel.getInventory();
      }
      HashMap<Integer, ItemStack> items = new HashMap<>();
      for (int i = 0, size = invsize; i < size; ++i) {
        if (cash.getItem(i) == null) continue;
        if (!(cash.getItem(i).getType() == Material.WRITTEN_BOOK)) continue;
        conut++;
        items.put(conut, cash.getItem(i));
      }
      if (conut >= 27) {
        inv = Bukkit.createInventory(null, 54, "Lectern 54");
      }
      for (java.util.Map.Entry<Integer, ItemStack> item1 : items.entrySet()) {
        inv.setItem(item1.getKey(), item1.getValue());
      }

      if (conut == 0) {
        player.openBook(inv.getItem(0));
      } else if (conut == -1) {
        return;
      } else {
        player.openInventory(inv);
      }
    }
  }
}
