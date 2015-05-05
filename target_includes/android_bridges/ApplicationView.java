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
     * Initialize activity
     * @param activity The MainActivity parent (aka "Main function")
     */
	public ApplicationView(android.app.Activity context) {
		parentContext = context;
		containedViews = new ArrayList<View>();
	}
	
    /**
    * Creates a new button element
    * @return The created button element
    */
	public Button createButton() {
		return new Button(parentContext);
	}
	
    /**
    * Creates a new text field element
    * @return The created text field element
    */
	public Textfield createTextfield() {
		return new Textfield(parentContext);
	}

	/**
	 * Adds a text field to this activity
	 * @param textfield The text field to add to this activity
	 */
	public void addTextfield(Textfield textfield) {
		containedViews.add(textfield.getWrappedElement());
		parentContext.addContentView(textfield.getWrappedElement(), textfield.getWrappedElement().getLayoutParams());
	}
	
	/**
	 * Adds a button to this activity
	 * @param button The button to add to this activity
	 */
	public void addButton(Button button) {
		containedViews.add(button.getWrappedElement());
		parentContext.addContentView(button.getWrappedElement(), button.getWrappedElement().getLayoutParams());
	}
}