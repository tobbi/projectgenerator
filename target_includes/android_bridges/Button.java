package com.fhflensburg.tobiasmarkus.androidBridge;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
	
	private LayoutParams layoutParams = null;
	
	private Context parentContext = null;

	/**
	 * Public constructor of class Button
	 */
	public Button(Context context)
	{
		parentContext = context;
		button = new android.widget.Button(context);
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
	 * @param width The width of this element
	 * @param height The height of this element
	 */
	public void setSize(float width, float height)
	{
		button.setWidth((int)(width * 2));
		button.setHeight((int)(height * 2));
		layoutParams = new LayoutParams((int)width, (int) height);
		button.setLayoutParams(layoutParams);
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
	public android.widget.Button getWrappedElement() {
		return button;
	}
	
	/**
	 * Adds this button to the specified activity.
	 * @param activity The activity to add the button to
	 */
	public void addToActivity() {
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
				if(method != null)
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