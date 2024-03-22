package xyz.nifti.gui.menu;

import xyz.nifti.gui.MenuAPI;
import xyz.nifti.gui.SGMenu;
import xyz.nifti.gui.buttons.SGButton;
import xyz.nifti.gui.pagination.SGPaginationButtonBuilder;
import xyz.nifti.gui.pagination.SGPaginationButtonType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class SGMenuListener implements Listener {

    private final JavaPlugin owner;
    private final MenuAPI menuAPI;

    public SGMenuListener(JavaPlugin owner, MenuAPI menuAPI) {
        this.owner = owner;
        this.menuAPI = menuAPI;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        // Determine if the inventory was a SpiGUI.
        if (event.getInventory().getHolder() != null
            && event.getInventory().getHolder() instanceof SGMenu) {

            // Get the instance of the SpiGUI that was clicked.
            SGMenu clickedGui = (SGMenu) event.getInventory().getHolder();

            // Check if the GUI is owner by the current plugin
            // (if not, it'll be deferred to the SGMenuListener registered
            // by that plugin that does own the GUI.)
            if (!clickedGui.getOwner().equals(owner)) return;

            // If the default action is to cancel the event (block default interactions)
            // we'll do that now.
            // The inventory's value is checked first, so it can be overridden on a
            // per-inventory basis. If the inventory's value is null, the plugin's
            // default value is checked.
            if (clickedGui.areDefaultInteractionsBlocked() != null) {
                event.setCancelled(clickedGui.areDefaultInteractionsBlocked());
            } else {
                // Note that this can be overridden by a call to #setCancelled(false) in
                // the button's event handler.
                if (menuAPI.areDefaultInteractionsBlocked())
                    event.setCancelled(true);
            }

            // If the slot is on the pagination row, get the appropriate pagination handler.
            if (menuAPI.isAutomaticPaginationEnabled() && event.getRawSlot() > clickedGui.getPageSize()) {
                int offset = event.getRawSlot() - clickedGui.getPageSize();
                SGPaginationButtonBuilder paginationButtonBuilder = menuAPI.getDefaultPaginationButtonBuilder();

                if (clickedGui.getPaginationButtonBuilder() != null) {
                    paginationButtonBuilder = clickedGui.getPaginationButtonBuilder();
                }

                SGPaginationButtonType buttonType = SGPaginationButtonType.forSlot(offset);
                SGButton paginationButton = paginationButtonBuilder.buildPaginationButton(buttonType, clickedGui);
                if (paginationButton != null) paginationButton.getListener().onClick(event);
                return;
            }
            
            // If the slot is a stickied slot, get the button from page 0.
            if (clickedGui.isStickiedSlot(event.getRawSlot())) {
                SGButton button = clickedGui.getButton(0, event.getRawSlot());
                if (button != null && button.getListener() != null) button.getListener().onClick(event);
            } else {
                // Otherwise, get the button normally.
                SGButton button = clickedGui.getButton(clickedGui.getCurrentPage(), event.getRawSlot());
                if (button != null && button.getListener() != null)
                    button.getListener().onClick(event);
            }

            if (clickedGui.getOnClick() != null)
                clickedGui.getOnClick().onClick(event);

        }

    }

    @EventHandler
    public void onInventoryMove(InventoryDragEvent event) {
        // Determine if the inventory was a SpiGUI.
        if (event.getInventory().getHolder() != null
                && event.getInventory().getHolder() instanceof SGMenu) {

            // Get the instance of the SpiGUI that was clicked.
            SGMenu clickedGui = (SGMenu) event.getInventory().getHolder();

            // Check if the GUI is owner by the current plugin
            // (if not, it'll be deferred to the SGMenuListener registered
            // by that plugin that does own the GUI.)
            if (!clickedGui.getOwner().equals(owner)) return;

            // If the default action is to cancel the event (block default interactions)
            // we'll do that now.
            // The inventory's value is checked first, so it can be overridden on a
            // per-inventory basis. If the inventory's value is null, the plugin's
            // default value is checked.
            if (clickedGui.areDefaultInteractionsBlocked() != null) {
                event.setCancelled(clickedGui.areDefaultInteractionsBlocked());
            } else {
                // Note that this can be overridden by a call to #setCancelled(false) in
                // the button's event handler.
                if (menuAPI.areDefaultInteractionsBlocked())
                    event.setCancelled(true);
            }

            if (clickedGui.getOnDragItem() != null)
                clickedGui.getOnDragItem().onDrag(event);

        }

    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        // Determine if the inventory was a SpiGUI.
        if (event.getInventory().getHolder() != null && event.getInventory().getHolder() instanceof SGMenu) {

            // Get the instance of the SpiGUI that was clicked.
            SGMenu clickedGui = (SGMenu) event.getInventory().getHolder();

            // Check if the GUI is owner by the current plugin
            // (if not, it'll be deferred to the SGMenuListener registered
            // by that plugin that does own the GUI.)
            if (!clickedGui.getOwner().equals(owner)) return;

            // If all the above is true and the inventory's onClose is not null,
            // call it.
            if (clickedGui.getOnClose() != null)
                clickedGui.getOnClose().onClose(event, clickedGui.getCloseStatus());

        }

    }

}
