package xyz.nifti.gui.menu;

import org.bukkit.inventory.PlayerInventory;
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
        if (event.getInventory().getHolder() != null && event.getInventory().getHolder() instanceof SGMenu menu) {

            // Check if the GUI is owner by the current plugin
            // (if not, it'll be deferred to the SGMenuListener registered
            // by that plugin that does own the GUI.)
            if (!menu.getOwner().equals(owner)) return;

            // If the default action is to cancel the event (block default interactions)
            // we'll do that now.
            // The inventory's value is checked first, so it can be overridden on a
            // per-inventory basis. If the inventory's value is null, the plugin's
            // default value is checked.
            if (menu.areDefaultInteractionsBlocked() != null) {
                event.setCancelled(menu.areDefaultInteractionsBlocked());
            } else {
                // Note that this can be overridden by a call to #setCancelled(false) in
                // the button's event handler.
                if (menuAPI.areDefaultInteractionsBlocked())
                    event.setCancelled(true);
            }

            // If the slot is on the pagination row, get the appropriate pagination handler.
            if (menuAPI.isAutomaticPaginationEnabled() && event.getRawSlot() > menu.getPageSize()) {
                int offset = event.getRawSlot() - menu.getPageSize();
                SGPaginationButtonBuilder paginationButtonBuilder = menuAPI.getDefaultPaginationButtonBuilder();

                if (menu.getPaginationButtonBuilder() != null) {
                    paginationButtonBuilder = menu.getPaginationButtonBuilder();
                }

                SGPaginationButtonType buttonType = SGPaginationButtonType.forSlot(offset);
                SGButton paginationButton = paginationButtonBuilder.buildPaginationButton(buttonType, menu);
                if (paginationButton != null) paginationButton.getListener().onClick(event);
                return;
            }
            
            // If the slot is a stickied slot, get the button from page 0.
            if (menu.isStickiedSlot(event.getRawSlot())) {
                SGButton button = menu.getButton(0, event.getRawSlot());
                if (button != null && button.getListener() != null) button.getListener().onClick(event);
            } else {
                // Otherwise, get the button normally.
                SGButton button = menu.getButton(menu.getCurrentPage(), event.getRawSlot());
                if (button != null && button.getListener() != null)
                    button.getListener().onClick(event);
            }

            // if the slot is an editable slot, don't cancel current event
            if (menu.isEditableSlot(event.getRawSlot()))
                event.setCancelled(false);

            // cancel if shift click tries to put item into non-editable slot
            if (event.getClickedInventory() instanceof PlayerInventory && event.getClick().isShiftClick())
                event.setCancelled(!menu.isEditableSlot(menu.getFirstAvailableSlot()));

            // call custom click listener if it exists
            if (menu.getOnClick() != null)
                menu.getOnClick().onClick(event);

        }

    }

    @EventHandler
    public void onInventoryMove(InventoryDragEvent event) {
        // Determine if the inventory was a SpiGUI.
        if (event.getInventory().getHolder() != null && event.getInventory().getHolder() instanceof SGMenu menu) {

            // Check if the GUI is owner by the current plugin
            // (if not, it'll be deferred to the SGMenuListener registered
            // by that plugin that does own the GUI.)
            if (!menu.getOwner().equals(owner)) return;

            // If the default action is to cancel the event (block default interactions)
            // we'll do that now.
            // The inventory's value is checked first, so it can be overridden on a
            // per-inventory basis. If the inventory's value is null, the plugin's
            // default value is checked.
            if (menu.areDefaultInteractionsBlocked() != null) {
                event.setCancelled(menu.areDefaultInteractionsBlocked());
            } else {
                // Note that this can be overridden by a call to #setCancelled(false) in
                // the button's event handler.
                if (menuAPI.areDefaultInteractionsBlocked())
                    event.setCancelled(true);
            }

            // if each slot is an editable slot, don't cancel current event
            if (event.getRawSlots().stream().allMatch(menu::isEditableSlot))
                event.setCancelled(false);

            // call custom drag listener if it exists
            if (menu.getOnDragItem() != null)
                menu.getOnDragItem().onDrag(event);

        }

    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        // Determine if the inventory was a SpiGUI.
        if (event.getInventory().getHolder() != null && event.getInventory().getHolder() instanceof SGMenu menu) {

            // Check if the GUI is owner by the current plugin
            // (if not, it'll be deferred to the SGMenuListener registered
            // by that plugin that does own the GUI.)
            if (!menu.getOwner().equals(owner)) return;

            // If all the above is true and the inventory's onClose is not null,
            // call it.
            if (menu.getOnClose() != null)
                menu.getOnClose().onClose(event, menu.getCloseStatus());

        }

    }

}
