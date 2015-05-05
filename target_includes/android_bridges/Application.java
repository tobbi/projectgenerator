package de.fhflensburg.tobiasmarkus.androidBridge;

import android.graphics.Point;

public class Application {
	
	/**
	 * The android activity
	 */
	private android.app.Activity innerActivity = null;
	
	/**
	 * The current "bridge" activity
	 */
	private ApplicationView activity = null;
	
	/**
	 * The height and width of the created activity
	 */
	private int width = 0, height = 0;
	
    /**
     * Initialize window
     * @param context The main Activity (aka main function) this window is wrapped in
     */
	public Application(android.app.Activity context) {
		innerActivity = context;
		
		android.view.Display display = innerActivity.getWindowManager().getDefaultDisplay();
		
		// Display-Size-Variable anlegen:
		Point display_size = new Point();

		// Groesse des Bildschirms abholen:
		display.getSize(display_size);
		
		// Speichern der Groesse in den Variablen width und height:
		this.height = display_size.y;
		this.width = display_size.x;
	}
	
	/**
	 * Gets the height of this application
	 * @return Height of this application in pixels
	 */
	public int getHeight()
	{
		return height;
	}
	
	/**
	 * Gets the width of this application
	 * @return Width of this application in pixels
	 */
	public int getWidth()
	{
		return width;
	}
	
    /**
     * Return the wrapped Activity instance
     * @return Activity The wrapped Activity instance
     */
	public android.app.Activity getWrappedElement() {
		return innerActivity;
	}

    /**
     * Create an activity inside this window
     * @return The created activity
     */
	public ApplicationView createActivity() {
		activity = new ApplicationView(innerActivity);
		return activity;
	}
	
	/**
	 * Gets the current activity inside this window
	 * @return The activity inside this window
	 */
	public ApplicationView getActivity() {
		return activity;
	}
}