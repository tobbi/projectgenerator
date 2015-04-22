package com.example.taschenrechner;

import java.util.ArrayList;

import android.view.View;
import android.widget.LinearLayout;

public class Activity {
	
	/**
	 * The android activity
	 */
	private android.app.Activity innerActivity = new android.app.Activity();
	
	private ArrayList<View> containedViews;
	
	public Activity() {
	}

	/**
	 * Adds a text field to this activity
	 * @param textfield The text field to add to this activity
	 */
	public void addElement(Textfield textfield) {
		containedViews.add(textfield.getRawElement());
		innerActivity.addContentView(textfield.getRawElement(), textfield.getRawElement().getLayoutParams());
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