import android.widget.Button;

/**
 * Class that represents a button
 * @author tobiasmarkus
 *
 */
public class Button extends android.widget.Button {
	
	/**
	 * The label of this button
	 */
	private String label;

	/**
	 * Public constructor of class Button
	 */
	public Button()
	{
	}
	
	/**
	 * Liefert die Beschriftung dieses Buttons zurück
	 * @return String Die Beschriftung dieses Buttons
	 */
	public String getLabel()
	{
		return label;
	}
	
	/**
	 * Sets the label for this button
	 * @param label The label for this button
	 */
	public void setLabel(String label)
	{
		this.label = label;
	}
}