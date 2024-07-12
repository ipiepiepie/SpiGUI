package xyz.nifti.gui.menu;

import org.bukkit.inventory.ItemStack;
import xyz.nifti.gui.SGMenu;
import xyz.nifti.gui.buttons.SGButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents Scheme for {@link SGMenu}. <br>
 * This util allows you to easily apply layouts to {@link SGMenu Menu} without having to think about slot ids. <br>
 * <p>
 * {@link ItemStack Items} in this layout represented by {@link #markers char markers}. One char in mask represents one marker. <br>
 * For empty menu fields, there is {@link #emptyMarker empty markers}. <br>
 * Empty marker represents char, which will be ignored on {@link #apply(SGMenu) mask applying}.
 *
 * @see SGMenu Menu
 *
 * @author ipiepiepie
 */
@SuppressWarnings("unused")
public class MenuScheme {
    private final List<char[]> mask = new ArrayList<>();
    private int page;
    private boolean stick = false;
    // markers //
    private final Map<Character, SGButton> markers = new HashMap<>();
    private Character editableMarker = null;
    private char emptyMarker = '0';
    
    
    /**
     * Create menu scheme for first page of {@link SGMenu}
     */
    public MenuScheme() {
        this(0);
    }
    
    /**
     * Create menu scheme for selected {@code page} of {@link SGMenu}
     *
     * @param page page to create scheme for
     */
    public MenuScheme(int page) {
        this.page = page;
    }
    
    /*==============================================================================================*/

    /**
     * Add row to {@link #mask}. <br>
     * Row is a string with size 9 (one GUI row).
     *
     * @param mask mask row for adding to {@link #mask}
     * @return MenuScheme builder
     */
    public MenuScheme mask(String mask) {
        if (mask.length() != 9)
            throw new IllegalArgumentException("Mask size must be 9!");

        char[] chars = mask.replaceAll(" ", "").toCharArray();

        getMask().add(chars);

        return this;
    }

    /**
     * Set {@link #emptyMarker Empty Marker} char.<br>
     * Empty marker chars will be ignored on {@link #apply(SGMenu) mask applying}.
     *
     * @param marker marker which represent empty marker
     * @return MenuScheme builder
     */
    public MenuScheme emptyMarker(char marker) {
        this.emptyMarker = marker;
        return this;
    }

    /**
     * Set {@link #getEditableMarker() Editable Marker} char.
     * <p>
     * Editable marker chars will be ued, to determine which slots can be {@link SGMenu#editableSlot(int) edited}.
     * @param marker marker, represents editable marker
     * @return MenuScheme builder
     */
    public MenuScheme editableMarker(char marker) {
        this.editableMarker = marker;
        return this;
    }

    /**
     * Add marker to {@link #markers markers List}. <br>
     * Links {@code marker char} with {@link SGButton}.
     *
     * @param marker marker char
     * @param item item, which linked to {@code marker}
     * @return MenuScheme builder
     * @see SGButton
     */
    public MenuScheme marker(char marker, SGButton item) {
        markers.put(marker, item);
        return this;
    }

    /**
     * Set current {@link #page}.
     *
     * @param page page to set
     * @return MenuScheme builder
     */
    public MenuScheme page(int page) {
        this.page = page;
        return this;
    }

    /**
     * Set if scheme should {@link #stick} slots.
     *
     * @param stick stick slots or not
     * @return MenuScheme builder
     */
    public MenuScheme sticky(boolean stick) {
        this.stick = stick;
        return this;
    }

    /**
     * Apply this menu pattern to {@link SGMenu menu}. <br>
     * Iterates over all mask markers and sets items to their positions.
     *
     * @param menu menu to apply mask for
     */
    public void apply(SGMenu menu) {
        // Iterate over all mask rows
        for (int row = 0; row < getMask().size(); row++) {
            char[] mask = getMask().get(row); // Get row from mask

            // Iterate over mask markers
            for (int pos = 0; pos < mask.length; pos++) {
                char marker = mask[pos]; // Get marker from mask

                // Skip this marker, if It's empty or not registered
                if (marker == getEmptyMarker() || !hasMarker(marker))
                    continue;

                int slot = row * 9 + pos;
                menu.setButton(slot + (getMask().size() * 9 * page), getMarker(marker));

                // set slot editable if needed
                if (hasEditableMarker() && marker == getEditableMarker())
                    menu.editableSlot(slot);

                // set slot sticky if needed
                if (isStick()) menu.stickSlot(slot);
            }
        }
    }


    /// MARKER METHODS ///

    public SGButton getMarker(char marker) {
        return markers.get(marker);
    }

    public boolean hasMarker(char marker) {
        return markers.containsKey(marker);
    }

    /// EMPTY MARKER METHODS ///

    public char getEmptyMarker() {
        return emptyMarker;
    }

    public void setEmptyMarker(char empty) {
        this.emptyMarker = empty;
    }

    /// EDITABLE MARKER METHODS ///

    public boolean hasEditableMarker() {
        return editableMarker != null;
    }

    public Character getEditableMarker() {
        return editableMarker;
    }

    public void setEditableMarker(Character editableMarker) {
        this.editableMarker = editableMarker;
    }


    /// MASK METHODS ///

    public List<char[]> getMask() {
        return mask;
    }

    /// PAGE METHODS ///

    public int getPage() {
        return page;
    }

    /// STICK METHODS ///

    public boolean isStick() {
        return stick;
    }
}
