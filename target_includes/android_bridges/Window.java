package com.fhflensburg.tobiasmarkus.androidBridge;

public class Window {
	
	/**
	 * The android activity
	 */
	private android.app.Activity innerActivity = null;
	
	public Window(android.app.Activity activity) {
		innerActivity = activity;
	}
	
	public android.app.Activity getRawElement() {
		return innerActivity;
	}
	
	public Activity createActivity() {
		return new Activity(innerActivity);
	}
}