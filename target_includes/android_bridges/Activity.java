package com.fhflensburg.tobiasmarkus.androidBridge;

import java.util.ArrayList;

import android.view.View;

public class Activity {
	
	/**
	 * The android activity
	 */
	private android.app.Activity innerActivity = null;
	
	private ArrayList<View> containedViews;
	
	public Activity(android.app.Activity activity) {
		innerActivity = activity;
	}

	/**
	 * Adds a text field to this activity
	 * @param textfield The text field to add to this activity
	 */
	public void addElement(Textfield textfield) {
		containedViews.add(textfield.getRawElement());
		innerActivity.addContentView(textfield.getRawElement(), textfield.getRawElement().getLayoutParams());
	}
	
	public Button createButton() {
		return new Button(innerActivity);
	}
	
	public Textfield createTextfield() {
		return new Textfield(innerActivity);
	}
	
	/**
	 * Adds a button to this activity
	 * @param button The button to add to this activity
	 */
	public void addElement(Button button) {
		containedViews.add(button.getRawElement());
		innerActivity.addContentView(button.getRawElement(), button.getRawElement().getLayoutParams());
	}
}