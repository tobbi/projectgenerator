package com.example.taschenrechner;

import android.content.Context;
import android.view.ViewGroup.LayoutParams;

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
	
	private final LayoutParams wrapContentParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

	/**
	 * Public constructor of class Button
	 */
	public Button(Context context)
	{
		button = new android.widget.Button(context);
		button.setLayoutParams(wrapContentParams);
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
	
	/**
	 * Sets the size of this element
	 * @param width The width of this element
	 * @param height The height of this element
	 */
	public void setSize(float width, float height)
	{
		button.setWidth((int)width);
		button.setHeight((int)height);
	}
	
	/**
	 * Sets the position of this element
	 * @param x The x position of this element
	 * @param y The y position of this element
	 */
	public void setPosition(float x, float y)
	{
		button.setX(x);
		button.setY(y);
	}
	
	/**
	 * Gets the wrapped element
	 */
	public android.widget.Button getRawElement() {
		return button;
	}
}