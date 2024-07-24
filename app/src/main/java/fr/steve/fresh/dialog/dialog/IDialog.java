package fr.steve.fresh.dialog.dialog;

/**
 * Interface for dialogs that can be opened with a specific page.
 * <p>
 * This interface defines a method for opening a dialog and associating it with a page.
 * </p>
 *
 * @param <P> the type of page associated with the dialog.
 */
public interface IDialog<P> {

    /**
     * Opens the dialog with the specified page.
     *
     * @param page the page to be associated with the dialog.
     */
    void open(P page);
}
