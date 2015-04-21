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

/**
 * Class that represents a text field.
 * @author tobiasmarkus
 */
public class Textfield extends android.widget.EditText {
	
	/**
	 * The text of this text field
	 */
	private String text;
	
	/**
	 * Public constructor of class Textfield
	 */
	public Textfield()
	{
		super();
	}
	
	/**
	 * Sets the text for this element
	 * @param text The text to set for this element
	 */
	public void setText(String text)
	{
		this.text = text;
	}
	
	/**
	 * Gets the text for this element
	 * @return String The text for this element
	 */
	public String getText()
	{
		return this.text;
	}
}