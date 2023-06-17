package waterpunch.lectern.plugin.event;

import com.google.common.collect.Sets;
import java.util.HashSet;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;

public class Event implements Listener {

  private static final HashSet<Material> BOOK_STORAGE_TYPES = Sets.newHashSet(
    Material.CHEST,
    Material.TRAPPED_CHEST,
    Material.BARREL
  );

  public Event(Plugin plugin) {
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler
  public void SignChangeEvent(SignChangeEvent e) {
    if (
      e.getLine(0).equals("[Lectern]") ||
      e.getLine(0).equals("[lectern]") ||
      e.getLine(0).equals("[lec]") ||
      e.getLine(0).equals("[Lec]")
    ) e.setLine(0, "[Lectern]");
  }

  public static boolean isNotBookStorageType(Material type) {
    return !BOOK_STORAGE_TYPES.contains(type);
  }

  @EventHandler(ignoreCancelled = true)
  public void onEnSignClick(PlayerInteractEvent e) {
    if (e.getPlayer().isSneaking()) return;
    if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;

    if (e.getClickedBlock().getType() == Material.ENCHANTING_TABLE) {
      for (BlockFace face : BlockFace.values()) {
        Block relative = e.getClickedBlock().getRelative(face);
        if (!relative.getType().toString().endsWith("_WALL_SIGN")) continue;
        if (
          face != ((Directional) relative.getBlockData()).getFacing()
        ) continue;

        Sign signboard = (Sign) relative.getState();
        if (
          signboard.getLine(0).equals("[Lectern]") ||
          signboard.getLine(0).equals("§1§l[Lectern]")
        ) {
          e.setCancelled(true);
          e.getPlayer().closeInventory();
          waterpunch.lectern.plugin.main.Main.onlecclick(
            e.getPlayer(),
            e.getClickedBlock().getLocation()
          );
          break;
        }

        if (
          isNotBookStorageType(
            e.getClickedBlock().getRelative(BlockFace.DOWN).getType()
          )
        ) return;

        for (BlockFace face1 : BlockFace.values()) {
          Block relative1 = e.getClickedBlock().getRelative(face1);
          if (!relative1.getType().toString().endsWith("_WALL_SIGN")) continue;
          if (
            face1 != ((Directional) relative1.getBlockData()).getFacing()
          ) continue;

          Sign signboard1 = (Sign) relative1.getState();
          if (
            signboard1.getLine(0).equals("[Lectern]") ||
            signboard1.getLine(0).equals("§1§l[Lectern]")
          ) {
            e.setCancelled(true);
            e.getPlayer().closeInventory();
            waterpunch.lectern.plugin.main.Main.onlecclick(
              e.getPlayer(),
              e.getClickedBlock().getLocation()
            );
            break;
          }
        }
      }
    }

    if (!(e.getClickedBlock().getState() instanceof Sign)) return;
    if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
    if (!e.hasBlock()) return;

    Sign signboard = (Sign) e.getClickedBlock().getState();

    if (
      signboard.getLine(0).equals("[Lectern]") ||
      signboard.getLine(0).equals("§1§l[Lectern]")
    ) {
      Location loc = e.getClickedBlock().getLocation();

      switch (e.getBlockFace().toString()) {
        case "NORTH":
          loc.setZ(loc.getZ() + 1);
          break;
        case "SOUTH":
          loc.setZ(loc.getZ() - 1);
          break;
        case "EAST":
          loc.setX(loc.getX() - 1);
          break;
        case "WEST":
          loc.setX(loc.getX() + 1);
          break;
      }
      if (!(loc.getBlock().getType() == Material.ENCHANTING_TABLE)) return;
      e.getPlayer().closeInventory();

      waterpunch.lectern.plugin.main.Main.onlecclick(e.getPlayer(), loc);
    }
  }

  @EventHandler
  public void onInventoryClickEvent(InventoryClickEvent e) {
    int size = 26;
    Player player = (Player) e.getWhoClicked();
    if (
      player.getOpenInventory().getTitle().equals("Lectern") ||
      player.getOpenInventory().getTitle().equals("Lectern 54")
    ) {
      e.setCancelled(true);
      if (player.getOpenInventory().getTitle().equals("Lectern 54")) size = 54;
      if (
        e.getRawSlot() > size ||
        e.getRawSlot() == -999 ||
        e.getInventory().getItem(e.getRawSlot()) == null
      ) return;
      player.openBook(e.getInventory().getItem(e.getRawSlot()));
    }
  }
}
