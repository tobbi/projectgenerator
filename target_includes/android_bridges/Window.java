package com.fhflensburg.tobiasmarkus.androidBridge;

public class Window {
	
	/**
	 * The android activity
	 */
	private android.app.Activity innerActivity = null;
	
    /**
     * Initialize window
     * @param context The main Activity (aka main function) this window is wrapped in
     */
	public Window(android.app.Activity context) {
		innerActivity = context;
	}
	
    /**
     * Return the wrapped Activity instance
     * @return Activity The wrapped Activity instance
     */
	public android.app.Activity getRawElement() {
		return innerActivity;
	}

    /**
     * Create an activity inside this window
     * @return The created activity
     */
	public Activity createActivity() {
		return new Activity(innerActivity);
	}
}