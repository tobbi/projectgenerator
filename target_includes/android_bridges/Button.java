package com.example.taschenrechner;
/**
 * Class that represents a button
 * @author tobiasmarkus
 *
 */
public class Button {
	
	/**
	 * Private button instance
	 */
	private android.widget.Button button;

	/**
	 * Public constructor of class Button
	 */
	public Button()
	{
		button = new android.widget.Button(null);
	}
	
	/**
	 * Liefert die Beschriftung dieses Buttons zurück
	 * @return String Die Beschriftung dieses Buttons
	 */
	public String getLabel()
	{
		return button.getText().toString();
	}
	
	/**
	 * Sets the label for this button
	 * @param label The label for this button
	 */
	public void setLabel(String label)
	{
		button.setText(label);
	}
}