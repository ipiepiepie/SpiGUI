package xyz.nifti.gui.buttons;

import xyz.nifti.gui.menu.CloseStatus;
import org.bukkit.event.inventory.InventoryCloseEvent;

public interface SGCloseListener {

    /**
     * The event handler that should be executed when an SGMenu is closed.
     * This is intended to implemented by lambda when you create an SGMenu.
     *
     * @param event The Bukkit/Spigot API's {@link InventoryCloseEvent}.
     * @param status status of closed menu
     */
    void onClose(InventoryCloseEvent event, CloseStatus status);
}
