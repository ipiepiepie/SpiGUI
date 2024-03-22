package xyz.nifti.gui.buttons;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import xyz.nifti.gui.MenuAPI;

import java.util.concurrent.CompletableFuture;

/**
 * An SGButton represents a clickable item in an SGMenu (GUI).
 * It consists of an icon ({@link ItemStack}) and a listener ({@link SGButton}).
 * <p>
 * When the icon is clicked in the SGMenu, the listener is called, thus allowing
 * for rudimentary menus to be built by displaying icons and overriding their behavior.
 * <p>
 * This somewhat resembles the point-and-click nature of Graphical User Interfaces (GUIs)
 * popularized by Operating Systems developed in the late 80s and 90s which is where the
 * name of the concept in Spigot plugins was derived.
 */
public class SGButton {
    private SGClickListener listener;
    private CompletableFuture<ItemStack> icon;

    /**
     * Creates an SGButton with the specified {@link ItemStack} as it's 'icon' in the inventory.
     *
     * @param icon The desired 'icon' for the SGButton.
     */
    public SGButton(ItemStack icon){
        this.icon = CompletableFuture.supplyAsync(() -> icon);
    }

    public SGButton(CompletableFuture<ItemStack> icon) {
        this.icon = icon;
    }

    public void put(Inventory inventory, int slot) {
        icon.thenAccept((item) -> Bukkit.getScheduler().runTask(MenuAPI.getPlugin(), () -> inventory.setItem(slot, item)));
    }

    /**
     * Sets the {@link SGClickListener} to be called when the button is clicked.
     * @param listener The listener to be called when the button is clicked.
     */
    public void setListener(SGClickListener listener) {
        this.listener = listener;
    }

    /**
     * A chainable alias of {@link #setListener(SGClickListener)}.
     *
     * @param listener The listener to be called when the button is clicked.
     * @return The {@link SGButton} the listener was applied to.
     */
    public SGButton withListener(SGClickListener listener) {
        this.listener = listener;
        return this;
    }

    /**
     * Returns the {@link SGClickListener} that is to be executed when the button
     * is clicked.<br>
     * This is typically intended for use by the API.
     *
     * @return The listener to be called when the button is clicked.
     */
    public SGClickListener getListener() {
        return listener;
    }

    /**
     * Returns the {@link ItemStack} that will be used as the SGButton's icon in the
     * SGMenu (GUI).
     *
     * @return The icon ({@link ItemStack}) that will be used to represent the button.
     */
    public ItemStack getIcon() {
        return icon.join();
    }

    /**
     * Changes the SGButton's icon.
     *
     * @param icon The icon ({@link ItemStack}) that will be used to represent the button.
     */
    public void setIcon(CompletableFuture<ItemStack> icon) {
        this.icon = icon;
    }

}
