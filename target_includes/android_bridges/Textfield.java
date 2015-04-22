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