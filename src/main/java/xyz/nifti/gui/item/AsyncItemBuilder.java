package xyz.nifti.gui.item;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Asynchronous realisation of {@link ItemBuilder}.
 *
 * @author ipiepiepie
 * @version 1.2.0
 * @see ItemBuilder
 */
public class AsyncItemBuilder {
    private Material material;
    private int amount = 1;
    private OfflinePlayer skullOwner;

    private String name;
    private List<String> lore = new ArrayList<>();

    private short durability;
    private Integer customModelData;
    private final List<ItemFlag> flags = new ArrayList<>();
    private Map<Enchantment, Integer> enchants = new HashMap<>();


    /* CONSTRUCT */

    /**
     * Creates an {@link ItemStack} and {@link AsyncItemBuilder} wrapper for a new stack with the
     * given type.
     *
     * @param material The {@link Material} to use when creating the stack.
     */
    public AsyncItemBuilder (Material material) {
        this.material = material;
    }

    /* MANIPULATE / READ */

    /**
     * Sets the type ({@link Material}) of the ItemStack.
     *
     * @param material The {@link Material} of the stack.
     * @return The {@link AsyncItemBuilder} instance.
     */
    public AsyncItemBuilder type(Material material) {
        this.material = material;
        return this;
    }

    /**
     * Returns the type ({@link Material}) of the ItemStack.
     *
     * @return The {@link Material} of the stack.
     */
    public Material getType() {
        return this.material;
    }

    /**
     * Sets the display name of the item.
     * Color codes using the ampersand (&amp;) are translated, if you want to avoid this,
     * you should wrap your name argument with a {@link ChatColor#stripColor(String)} call.
     *
     * @param name The desired display name of the item stack.
     * @return The {@link AsyncItemBuilder} instance.
     */
    public AsyncItemBuilder name(String name) {
        this.name = ChatColor.translateAlternateColorCodes('&', name);
        return this;
    }

    /**
     * Returns either the display name of the item, if it exists, or null if it doesn't.
     * <p>
     * You should note that this method fetches the name directly from the stack's {@link ItemMeta},
     * so you should take extra care when comparing names with color codes - particularly if you used the
     * {@link #name(String)} method as they will be in their translated sectional symbol (§) form,
     * rather than their 'coded' form (&amp;).
     * <p>
     * For example, if you used {@link #name(String)} to set the name to '&amp;cMy Item', the output of this
     * method would be '§cMy Item'
     *
     * @return The item's display name as returned from its {@link ItemMeta}.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the amount of items in the {@link ItemStack}.
     *
     * @param amount The new amount.
     * @return The {@link AsyncItemBuilder} instance.
     */
    public AsyncItemBuilder amount(int amount) {
        this.amount = amount;
        return this;
    }

    /**
     * Returns the amount of items in the {@link ItemStack}.
     *
     * @return The amount of items in the stack.
     */
    public int getAmount() {
        return this.amount;
    }

    /**
     * Sets the lore of the item. This method is a var-args alias for the
     * {@link #lore(List)} method.
     *
     * @param lore The desired lore of the item, with each line as a separate string.
     * @return The {@link AsyncItemBuilder} instance.
     */
    public AsyncItemBuilder lore(String... lore) {
        return lore(Arrays.asList(lore));
    }

    /**
     * Sets the lore of the item.
     * As with {@link #name(String)}, color codes will be replaced. Each string represents
     * a line of the lore.
     * <p>
     * Lines will not be automatically wrapped or truncated, so it is recommended you take
     * some consideration into how the item will be rendered with the lore.
     *
     * @param lore The desired lore of the item, with each line as a separate string.
     * @return The {@link AsyncItemBuilder} instance.
     */
    public AsyncItemBuilder lore(List<String> lore) {
        lore.replaceAll(textToTranslate -> ChatColor.translateAlternateColorCodes('&', textToTranslate));

        this.lore = lore;
        return this;
    }

    /**
     * Sets the lore of the item. <br>
     * As with {@link #name(String)}, color codes will be replaced. Each string represents
     * a line of the lore. <br>
     * Also replaces {@code placeholders}
     * <p>
     * Lines will not be automatically wrapped or truncated, so it is recommended you take
     * some consideration into how the item will be rendered with the lore.
     *
     * @param lore The desired lore of the item, with each line as a separate string.
     * @param placeholders placeholders to replace
     * @return The {@link ItemBuilder} instance.
     */
    public AsyncItemBuilder lore(List<String> lore, Map<String, String> placeholders) {
        lore.replaceAll(textToTranslate -> ChatColor.translateAlternateColorCodes('&', textToTranslate));

        placeholders.forEach((key, value) -> lore.replaceAll(line -> line.replaceAll(key, value)));

        this.lore = lore;

        return this;
    }

    /**
     * Gets the lore of the item as a list of strings. Each string represents a line of the
     * item's lore in-game.
     * <p>
     * As with {@link #name(String)} it should be note that color-coded lore lines will
     * be returned with the colors codes already translated.
     *
     * @return The lore of the item.
     */
    public List<String> getLore() {
        return this.lore;
    }

    /**
     * An alias for {@link #durability(short)} that takes an {@link ItemDataColor} as an
     * argument instead. This is to improve code readability when working with items such
     * as glass panes, where the data value represents a glass pane's color.
     * <p>
     * This method will still be functional for items where the data value does not represent
     * the item's color, however it will obviously be nonsensical.
     *
     * @param color The desired color of the item.
     * @return The {@link AsyncItemBuilder} instance.
     */
    public AsyncItemBuilder color(ItemDataColor color) {
        return durability(color.getValue());
    }

    /**
     * An alias for {@link #durability(short)}.
     *
     * @param data The desired data-value (durability) of the item.
     * @return The {@link AsyncItemBuilder} instance.
     */
    public AsyncItemBuilder data(short data) {
        return durability(data);
    }

    /**
     * Sets the durability (data value) of the item.
     *
     * @param durability The desired durability of the item.
     * @return The updated {@link AsyncItemBuilder} object.
     */
    public AsyncItemBuilder durability(short durability) {
        this.durability = durability;
        return this;
    }

    /**
     * Returns the durability or data value of the item.
     *
     * @return The durability of the item.
     */
    public short getDurability() {
        return this.durability;
    }

    /**
     * Essentially a proxy for {@link ItemDataColor#getByValue(short)}.
     * <p>
     * Similar to {@link #getDurability()} however it returns the value as an {@link ItemDataColor}
     * where it is applicable, or null where it isn't.
     *
     * @return The appropriate {@link ItemDataColor} of the item or null.
     */
    public ItemDataColor getColor() {
        return ItemDataColor.getByValue(getDurability());
    }

    /**
     * Adds the specified enchantment to the stack.
     * <p>
     * This method uses {@link ItemStack#addUnsafeEnchantment(Enchantment, int)} rather than {@link ItemStack#addEnchantment(Enchantment, int)}
     * to avoid the associated checks of whether level is within the range for the enchantment.
     *
     * @param enchantment The enchantment to apply to the item.
     * @param level The level of the enchantment to apply to the item.
     * @return The {@link AsyncItemBuilder} instance.
     */
    public AsyncItemBuilder enchant(Enchantment enchantment, int level) {
        this.enchants.put(enchantment, level);
        return this;
    }

    /**
     * Removes the specified enchantment from the stack.
     *
     * @param enchantment The enchantment to remove from the item.
     * @return The {@link AsyncItemBuilder} instance.
     */
    public AsyncItemBuilder unenchant(Enchantment enchantment) {
        this.enchants.remove(enchantment);
        return this;
    }

    /**
     * Get all {@link Enchantment Enchantmets} for current {@link AsyncItemBuilder}.
     *
     * @return {@link #enchants Map} of all enchantments
     */
    public Map<Enchantment, Integer> getEnchants() {
        return enchants;
    }

    /**
     * Set Custom Model Data ID <br>
     * Use null to remove Custom Model Data ID
     *
     * @param customModelData custom model data ID to set
     * @return The {@link AsyncItemBuilder} instance.
     */
    public AsyncItemBuilder customModelData(Integer customModelData) {
        this.customModelData = customModelData;
        return this;
    }

    /**
     * Get Custom Model Data ID
     *
     * @return CustomModelData of item.
     */
    public Integer getCustomModelData() {
        return this.customModelData;
    }

    /**
     * Accepts a variable number of {@link ItemFlag}s to apply to the stack.
     *
     * @param flag A variable-length argument containing the flags to be applied.
     * @return The {@link AsyncItemBuilder} instance.
     */
    public AsyncItemBuilder flag(ItemFlag... flag) {
        this.flags.addAll(Arrays.asList(flag));
        return this;
    }

    /**
     * Accepts a variable number of {@link ItemFlag}s to remove from the stack.
     *
     * @param flag A variable-length argument containing the flags to be removed.
     * @return The {@link AsyncItemBuilder} instance.
     */
    public AsyncItemBuilder deflag(ItemFlag... flag) {
        this.flags.removeAll(Arrays.asList(flag));
        return this;
    }

    public ItemFlag[] getFlags() {
        return flags.toArray(new ItemFlag[0]);
    }

    /**
     * If the item has {@link SkullMeta} (i.e. if the item is a skull), this can
     * be used to set the skull's owner (i.e. the player the skull represents.)
     * <p>
     * This also sets the skull's data value to 3 for 'player head', as setting
     * the skull's owner doesn't make much sense for the mob skulls.
     *
     * @param player The name of the player the skull item should resemble.
     * @return The {@link AsyncItemBuilder} instance.
     */
    public AsyncItemBuilder skullOwner(OfflinePlayer player) {
        if (getType().equals(Material.PLAYER_HEAD))
            this.skullOwner = player;
        return this;
    }

    /**
     * Get owner for skull.
     *
     * @return {@link OfflinePlayer skull owner} or {@code null}
     */
    public OfflinePlayer getSkullOwner() {
        return skullOwner;
    }

    /* BUILD */

    /**
     * An alias for {@link #get()}.
     * @return See {@link #get()}.
     */
    public CompletableFuture<ItemStack> build() {
        return get();
    }

    /**
     * Returns the {@link ItemStack} that the {@link AsyncItemBuilder} instance represents.
     * <p>
     * The modifications are performed as they are called, so this method simply returns
     * the class's private stack field.
     *
     * @return The manipulated ItemStack.
     */
    public CompletableFuture<ItemStack> get() {
        return CompletableFuture.supplyAsync(() -> {
            ItemStack item = new ItemStack(getType());
            ItemMeta meta = item.getItemMeta();

            if (getAmount() != 1) item.setAmount(getAmount());

            if (getDurability() != 0) item.setDurability(getDurability());

            if (meta != null) {
                if (getName() != null) meta.setDisplayName(getName());
                if (!getLore().isEmpty()) meta.setLore(getLore());
                if (getCustomModelData() != null) meta.setCustomModelData(getCustomModelData());

                meta.addItemFlags(getFlags());
                getEnchants().forEach((enchantment, level) -> meta.addEnchant(enchantment, level, true));

                if (getSkullOwner() != null) {
                    if (meta instanceof SkullMeta) {
                        SkullMeta skull = (SkullMeta) meta;
                        item.setDurability((byte) 3);
                        skull.setOwningPlayer(getSkullOwner());
                    }
                }
            }

            item.setItemMeta(meta);

            return item;
        });
    }
}
