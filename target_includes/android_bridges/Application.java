package de.fhflensburg.tobiasmarkus.androidBridge;

public class Application {
	
	/**
	 * The android activity
	 */
	private android.app.Activity innerActivity = null;
	
	/**
	 * The current "bridge" application view
	 */
	private ApplicationView applicationView = null;
	
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
     * @return The created application view
     */
	public ApplicationView createApplicationView() {
		applicationView = new ApplicationView(innerActivity);
		return applicationView;
	}
	
	/**
	 * Gets the current application view inside this window
	 * @return The application view inside this window
	 */
	public ApplicationView getApplicationView() {
		return applicationView;
	}
}