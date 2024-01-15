package games.omg.menus;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import games.omg.Main;

import java.util.HashMap;

public class InventoryMenu implements Listener {

  final private static HashMap<Player, InventoryMenu> openMenus = new HashMap<>();

  //

  private InventoryClickHandler handler;
  private Inventory inventory;
  final private String name;

  public interface InventoryClickHandler {
    void inventoryInteractEvent(InventoryInteractEvent e);

    void create(Inventory i);

    void close(Inventory i);
  }

  //

  public InventoryMenu(Player p, String name, int size, String title, InventoryClickHandler handler) {
    openMenus.put(p, this);

    this.handler = handler;
    this.name = name;

    // TODO: change to a string
    inventory = Bukkit.createInventory(p, size, title);
    handler.create(inventory);
    p.openInventory(inventory);

    Main.register(this);
  }

  //

  public static InventoryMenu getInventory(Player p) {
    return openMenus.get(p);
  }

  public String getName() {
    return name;
  }

  public Inventory getInventory() {
    return inventory;
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onInventoryClose(InventoryCloseEvent event) {
    if (!event.getView().getTopInventory().equals(inventory))
      return;

    HandlerList.unregisterAll(this);

    handler.close(inventory);

    inventory = null;
    handler = null;

    openMenus.remove(event.getPlayer());
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onInventoryClick(InventoryClickEvent event) {
    onInventoryInteractEvent(event);
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onInventoryDrag(InventoryDragEvent event) {
    onInventoryInteractEvent(event);
  }

  public void onInventoryInteractEvent(InventoryInteractEvent event) {
    // Bukkit.broadcastMessage(event.getWhoClicked().getOpenInventory().getTopInventory().toString());
    // Bukkit.broadcastMessage(ChatColor.RED+"COMPARE");
    // Bukkit.broadcastMessage(inventory.toString());
    if (event.getWhoClicked().getOpenInventory().getTopInventory().equals(inventory)) {
      // Bukkit.broadcastMessage(ChatColor.GREEN + "Passed inventory check");
      handler.inventoryInteractEvent(event);
    }
  }

  public static void setCursor(InventoryInteractEvent e, ItemStack stack) {
    if (e instanceof InventoryClickEvent)
      ((InventoryClickEvent) e).setCursor(stack);
    if (e instanceof InventoryDragEvent)
      ((InventoryDragEvent) e).setCursor(stack);
  }

  public static Inventory getClickedInventory(InventoryInteractEvent e) {
    if (e instanceof InventoryClickEvent)
      return ((InventoryClickEvent) e).getClickedInventory();
    if (e instanceof InventoryDragEvent) {
      Integer rawSlot = getClickedRawSlot(e);
      if (rawSlot == null)
        return null;
      return rawSlot < e.getView().getTopInventory().getSize() ? e.getView().getTopInventory()
          : e.getView().getBottomInventory();
    }
    return null;
  }

  public static Integer getClickedSlot(InventoryInteractEvent e) {
    if (e instanceof InventoryClickEvent)
      return ((InventoryClickEvent) e).getSlot();
    if (e instanceof InventoryDragEvent) {
      InventoryDragEvent dragEvent = (InventoryDragEvent) e;
      if (dragEvent.getInventorySlots().size() == 0)
        return null;
      return dragEvent.getInventorySlots().iterator().next();
    }
    return null;
  }

  public static Integer getClickedRawSlot(InventoryInteractEvent e) {
    if (e instanceof InventoryClickEvent)
      return ((InventoryClickEvent) e).getRawSlot();
    if (e instanceof InventoryDragEvent) {
      InventoryDragEvent dragEvent = (InventoryDragEvent) e;
      if (dragEvent.getRawSlots().size() == 0)
        return null;
      return dragEvent.getRawSlots().iterator().next();
    }
    return null;
  }
}