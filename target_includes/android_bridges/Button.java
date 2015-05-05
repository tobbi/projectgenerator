package de.fhflensburg.tobiasmarkus.androidBridge;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;

import java.lang.reflect.InvocationTargetException;

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
	 * Private layout params instance
	 */
	private LayoutParams layoutParams = null;
	
	/**
	 * Parent context (activity?)
	 */
	private Context parentContext = null;
	
	/**
	 * Parent view
	 */
	private ApplicationView parentView;

	/**
	 * Public constructor of class Button
	 */
	public Button(ApplicationView parentView)
	{
		this.parentView = parentView;
		parentContext = parentView.getParentContext();
		button = new android.widget.Button(parentContext);
		button.setTextSize(12);
	}
	
	/**
	 * Returns the label of this button
	 * @return String The label of this button
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
	 * @param width The width of this element in percent
	 * @param height The height of this element in percent
	 */
	public void setSize(float width, float height)
	{
		int newWidth = (int)((parentView.getWidth() / 100) * width);
		int newHeight = (int)(parentView.getHeight() / 100 * height);
		button.setWidth(newWidth);
		button.setHeight(newHeight);
		layoutParams = new LayoutParams(newWidth, newHeight);
		button.setLayoutParams(layoutParams);
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
		
		button.setX(newX);
		button.setY(newY);
	}
	
	/**
	 * Gets the wrapped element
	 */
	public android.widget.Button getWrappedElement() {
		return button;
	}
	
	/**
	 * Adds this button to the specified activity.
	 * @param activity The activity to add the button to
	 */
	public void addToApplicationView() {
		((android.app.Activity)parentContext).addContentView(this.getWrappedElement(), this.getWrappedElement().getLayoutParams());
	}
	
	/**
	 * Adds an onClick listener to this element
	 * @param listener The listener to add to this element.
	 */
	public void addOnClickListener(final String methodName) {
		final Button _button = this;
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				java.lang.reflect.Method method = null;
				try {
				  method = parentContext.getClass().getMethod(methodName, Button.class);
				} catch (SecurityException e) {
				  // ...
				} catch (NoSuchMethodException e) {
				  // ...
					e.printStackTrace();
				}
				if(method == null)
					return;

				try {
					method.invoke(parentContext, _button);
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
}