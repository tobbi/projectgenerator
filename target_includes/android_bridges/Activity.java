package com.fhflensburg.tobiasmarkus.androidBridge;

import java.util.ArrayList;

import android.view.View;

public class Activity {
	
	/**
	 * The Android activity
	 */
	private android.app.Activity innerActivity = null;
	
    /**
     * Array list containing all subviews of this activity
     */
	private ArrayList<View> containedViews;
	
    /**
     * Initialize activity
     * @param activity The MainActivity parent (aka "Main function")
     */
	public Activity(android.app.Activity context) {
		innerActivity = context;
		containedViews = new ArrayList<View>();
	}
	
    /**
    * Creates a new button element
    * @return The created button element
    */
	public Button createButton() {
		return new Button(innerActivity);
	}
	
    /**
    * Creates a new text field element
    * @return The created text field element
    */
	public Textfield createTextfield() {
		return new Textfield(innerActivity);
	}

	/**
	 * Adds a text field to this activity
	 * @param textfield The text field to add to this activity
	 */
	public void addElement(Textfield textfield) {
		containedViews.add(textfield.getWrappedElement());
		innerActivity.addContentView(textfield.getWrappedElement(), textfield.getWrappedElement().getLayoutParams());
	}
	
	/**
	 * Adds a button to this activity
	 * @param button The button to add to this activity
	 */
	public void addElement(Button button) {
		containedViews.add(button.getWrappedElement());
		innerActivity.addContentView(button.getWrappedElement(), button.getWrappedElement().getLayoutParams());
	}
}