package de.fhflensburg.tobiasmarkus.androidBridge;

import java.util.ArrayList;

import android.view.View;

public class ApplicationView {
	
	/**
	 * The Android activity
	 */
	private android.app.Activity parentContext = null;
	
    /**
     * Array list containing all subviews of this activity
     */
	private ArrayList<View> containedViews;
	
	/**
	 * Width of the parent activity
	 */
	private int width;
	
	/**
	 * Height of the parent activity
	 */
	private int height;
	
    /**
     * Initialize application view
     * @param activity The MainActivity parent (aka "Main function")
     */
	public ApplicationView(android.app.Activity context, int width, int height) {
		parentContext = context;
		containedViews = new ArrayList<View>();

		this.width = width;
		this.height = height;
	}
	
	public int getWidth()
	{
		return width;
	}
	
	public int getHeight()
	{
		return height;
	}
	
	public android.app.Activity getParentContext()
	{
		return parentContext;
	}
	
    /**
    * Creates a new button element
    * @return The created button element
    */
	public Button createButton() {
		return new Button(this);
	}
	
    /**
    * Creates a new text field element
    * @return The created text field element
    */
	public Textfield createTextfield() {
		return new Textfield(this);
	}

	/**
	 * Adds a text field to this application view
	 * @param textfield The text field to add to this application view
	 */
	public void addTextfield(Textfield textfield) {
		containedViews.add(textfield.getWrappedElement());
		parentContext.addContentView(textfield.getWrappedElement(), textfield.getWrappedElement().getLayoutParams());
	}
	
	/**
	 * Adds a button to this application view
	 * @param button The button to add to this application view
	 */
	public void addButton(Button button) {
		containedViews.add(button.getWrappedElement());
		parentContext.addContentView(button.getWrappedElement(), button.getWrappedElement().getLayoutParams());
	}
}