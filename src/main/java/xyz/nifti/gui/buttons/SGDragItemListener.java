package xyz.nifti.gui.buttons;

import org.bukkit.event.inventory.InventoryDragEvent;

public interface SGDragItemListener {

    /**
     * The event handler that should be executed when an SGButton is dragged.
     * This is intended to implemented by lambda when you create an SGButton.
     *
     * @param event The Bukkit/Spigot API's {@link InventoryDragEvent}.
     */
    void onDrag(InventoryDragEvent event);
}
