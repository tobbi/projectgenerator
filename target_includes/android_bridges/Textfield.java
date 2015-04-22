package com.example.taschenrechner;

import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup.LayoutParams;
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
	
	private final LayoutParams wrapContentParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	
	/**
	 * Public constructor of class Textfield
	 */
	public Textfield(Context context)
	{
		editText = new EditText(context);
		editText.setLayoutParams(wrapContentParams);
		editText.setBackgroundColor(Color.WHITE);
		editText.setTextColor(Color.BLACK);
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
	
	/**
	 * Sets the size of this element
	 * @param width The width of this element
	 * @param height The height of this element
	 */
	public void setSize(float width, float height)
	{
		editText.setWidth((int)width);
		editText.setHeight((int)height);
	}
	
	/**
	 * Sets the position of this element
	 * @param x The x position of this element
	 * @param y The y position of this element
	 */
	public void setPosition(float x, float y)
	{
		editText.setX(x);
		editText.setY(y);
	}
	
	/**
	 * Gets the wrapped element
	 * @return The element wrapped by this class
	 */
	public android.widget.EditText getRawElement()
	{
		return editText;
	}
	
	/**
	 * Adds this button to the specified activity.
	 * @param activity The activity to add the button to
	 */
	public void addToActivity(android.app.Activity activity) {
		activity.addContentView(this.getRawElement(), this.getRawElement().getLayoutParams());
	}
}