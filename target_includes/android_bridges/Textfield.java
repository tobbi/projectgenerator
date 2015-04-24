package com.fhflensburg.tobiasmarkus.androidBridge;

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
	
	/**
	 * Layout parameters for wrap content elements
	 */
	//private final LayoutParams wrapContentParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	
	private Context parentContext = null;
	
	/**
	 * Public constructor of class Textfield
	 */
	public Textfield(Context context)
	{
		parentContext = context;
		editText = new EditText(context);
		//editText.setLayoutParams(wrapContentParams);
		editText.setBackgroundColor(Color.WHITE);
		editText.setTextColor(Color.BLACK);
		editText.setEms(12);
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
	 * Adds the specified text to the element
	 * @param text The text to add to the element
	 */
	public void addText(String text)
	{
		setText(getText() + text);
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
		editText.setLayoutParams(new LayoutParams((int)width, (int)height));
		//editText.setWidth((int)width);
		//editText.setHeight((int)height);
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
	//public void addToActivity() {
	//	((android.app.Activity)parentContext).addContentView(this.getRawElement(), this.getRawElement().getLayoutParams());
	//}
}