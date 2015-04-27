package de.fhflensburg.tobiasmarkus.androidBridge;

public class Application {
	
	/**
	 * The android activity
	 */
	private android.app.Activity innerActivity = null;
	
	/**
	 * The current "bridge" activity
	 */
	private Activity activity = null;
	
    /**
     * Initialize window
     * @param context The main Activity (aka main function) this window is wrapped in
     */
	public Application(android.app.Activity context) {
		innerActivity = context;
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
	public Activity createActivity() {
		activity = new Activity(innerActivity);
		return activity;
	}
	
	/**
	 * Gets the current activity inside this window
	 * @return The activity inside this window
	 */
	public Activity getActivity() {
		return activity;
	}
}