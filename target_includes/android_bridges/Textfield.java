package com.example.taschenrechner;

import android.text.Editable;
import android.widget.EditText;

/**
 * Class that represents a text field.
 * @author tobiasmarkus
 */
public class Textfield {
	
	/**
	 * Private EditText instance
	 */
	private EditText editText;
	
	/**
	 * Public constructor of class Textfield
	 */
	public Textfield()
	{
		editText = new EditText(null);
	}
	
	/**
	 * Sets the text for this element
	 * @param text The text to set for this element
	 */
	public void setText(String text)
	{
		this.editText.setText(text);
	}
	
	/**
	 * Gets the text for this element
	 * @return String The text for this element
	 */
	public String getText()
	{
		return this.editText.getText().toString();
	}
}