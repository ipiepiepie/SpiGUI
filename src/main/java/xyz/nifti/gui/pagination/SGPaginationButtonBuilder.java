package xyz.nifti.gui.pagination;

import xyz.nifti.gui.SGMenu;
import xyz.nifti.gui.buttons.SGButton;

public interface SGPaginationButtonBuilder {

    SGButton buildPaginationButton(SGPaginationButtonType type, SGMenu inventory);

}
