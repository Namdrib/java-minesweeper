package minesweeper;

/**
 * A listener for changes to a place. Informed with the
 * <code>cellChanged()</code> function. This should be used with the GUI - each
 * CellListener object should represent a Cell in the GUI
 * 
 * @author Namdrib
 *
 */
public interface CellListener
{
	/**
	 * Called whenever the visible state of a Cell has changed. Examples
	 * include:
	 * <ul>
	 * <li>opened
	 * <li>flag state changed
	 * </ul>
	 */
	public void cellChanged();
}
