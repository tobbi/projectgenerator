package de.fhflensburg.tobiasmarkus.androidBridge;

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
	
	private ApplicationView parentView = null;
	
	/**
	 * Public constructor of class Textfield
	 */
	public Textfield(ApplicationView parentView)
	{
		this.parentView = parentView; 
		parentContext = parentView.getParentContext();
		editText = new EditText(parentContext);
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
	 * Returns the number that might be contained in this text
	 * @return Double value of this text
	 */
	public Double getNumber()
	{
		return Double.parseDouble(this.getText());
	}
	
	/**
	 * Sets the passed number (parameter number) as text
	 * @param number The number to set as text
	 */
	public void setNumber(Double number)
	{
		setText(String.valueOf(number));
	}
	
	/**
	 * Sets the size of this element
	 * @param width The width of this element
	 * @param height The height of this element
	 */
	public void setSize(float width, float height)
	{
		int newWidth = (int)((parentView.getWidth() / 100) * width);
		int newHeight = (int)(parentView.getHeight() / 100 * height);
		editText.setLayoutParams(new LayoutParams(newWidth, newHeight));
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
		int newX = (int)((parentView.getWidth() / 100) * x);
		int newY = (int)(parentView.getHeight() / 100 * y);
		
		editText.setX(newX);
		editText.setY(newY);
	}
	
	/**
	 * Gets the wrapped element
	 * @return The element wrapped by this class
	 */
	public android.widget.EditText getWrappedElement()
	{
		return editText;
	}
	
	/**
	 * Adds this button to the application view.
	 */
	public void addToApplicationView() {
		((android.app.Activity)parentContext).addContentView(this.getWrappedElement(), this.getWrappedElement().getLayoutParams());
	}
}