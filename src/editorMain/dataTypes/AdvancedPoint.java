package editorMain.dataTypes;

import java.awt.Point;

public class AdvancedPoint extends Point {
	
	/**
	 * Serial version ID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * inherited constructors of Point
	 */
	public AdvancedPoint() {
		super();
	}

	public AdvancedPoint(int x, int y) {
		super(x, y);
	}

	public AdvancedPoint(Point p) {
		super(p);
	}

	/**
	 * Returns the X value in device points (dp)
	 * (for Android compatibility)
	 * @return The point's X value in dp
	 */
	public String getDpX() {
		return String.valueOf(this.getX()) + "dp";
	}
	
	/**
	 * Returns the Y value in device points (dp)
	 * (for Android compatibility)
	 * @return The point's Y value in dp
	 */
	public String getDpY() {
		return String.valueOf(this.getY() + "dp");
	}
}
