package editorMain.guitypes;

import java.awt.Color;

import javax.json.stream.JsonParser;

import editorMain.dataTypes.AdvancedPoint;

public abstract class BaseGUIType extends GUIDElement {
	
	public BaseGUIType()
	{
	}

	/**
	 * The name of this GUI element
	 */
	private String m_pName = "";
	
	/**
	 * Gets the name of this GUI element
	 * @return public readable name of this element (String)
	 */
	public String getName()
	{
		return m_pName;
	}
	
	/***
	 * Sets the name of this GUI element
	 * @param name Name of this GUI element
	 */
	public void setName(String name)
	{
		m_pName = name;
	}
	
	/**
	 * The position of this element relative to its parent
	 */
	private int m_pPositionX = 0;
	private int m_pPositionY = 0;
	
	/**
	 * Gets the position of this element relative to its parent
	 * @return
	 */
	public AdvancedPoint getPosition()
	{
		return new AdvancedPoint(m_pPositionX, m_pPositionY);
	}
	
	/**
	 * Sets the position X of this element relative to its parent
	 * @return
	 */	
	public void setPositionX(int x)
	{
		m_pPositionX = x;
	}
	
	/**
	 * Sets the position Y of this element relative to its parent
	 * @return
	 */	
	public void setPositionY(int y)
	{
		m_pPositionY = y;
	}
	
	/**
	 * The width of this element
	 */
	private int m_pWidth = 0;
	private int m_pHeight = 0;
	
	/**
	 * Gets the position of this element relative to its parent
	 * @return
	 */
	public AdvancedPoint getSize()
	{
		return new AdvancedPoint(m_pWidth, m_pHeight);
	}
	
	/**
	 * Sets the position X of this element relative to its parent
	 * @return
	 */	
	public void setWidth(int width)
	{
		m_pWidth = width;
	}
	
	/**
	 * Sets the position Y of this element relative to its parent
	 * @return
	 */	
	public void setHeight(int height)
	{
		m_pHeight = height;
	}
	
	/**
	 * The ID of this GUI element
	 */
	private String m_pId = "< no ID set>";
	
	/**
	 * Gets the ID of this GUI element
	 * @return public readable ID of this element (String)
	 */
	public String getId()
	{
		return m_pId;
	}
	
	/***
	 * Sets the name of this GUI element
	 * @param id ID of this GUI element
	 */
	public void setId(String id)
	{
		m_pId = id;
	}
	
	private String m_pType;

	public String getType() {
		return m_pType;
	}

	public void setType(String type) {
		this.m_pType = type;
	}
	
	public String getReadableName()
	{
		String name = "";

		if(this.getType() != null)
		{
			name += this.getType();
			name += "_";
		}
		if(this.getName() != null && !this.getName().isEmpty())
		{
			name += this.getName();
		}
		else if(this.getId() != null && !this.getId().isEmpty())
		{
			name += this.getId();
		}
		else
		{
			name += this.getGeneratedID();
		}
		
		return name;
	}
	
	
	/**
	 * Color in DeviceRGB specification
	 * @author tobiasmarkus
	 *
	 */
	class CalibratedRgb {
		float r, g, b, a;
	}
	
	private CalibratedRgb toCalibratedRgb(Color color)
	{
		CalibratedRgb d = new CalibratedRgb();
		d.r = color.getRed() / 255;
		d.g = color.getGreen() / 255;
		d.b = color.getBlue() / 255;
		d.a = color.getAlpha() / 255;

		return d;
	}
	
	private Color backgroundColor;
	private Color textColor;

	public Color getBackgroundColor() {
		return backgroundColor;
	}
	
	public Color getTextColor() {
		return textColor;
	}
	
	public CalibratedRgb getTextColorCalibrated() {
		return toCalibratedRgb(textColor);
	}
	
	public void setTextColor(String textColor) {
		this.textColor = Color.decode(textColor);
	}
	
	public CalibratedRgb getBackgroundColorCalibrated() {
		return toCalibratedRgb(this.backgroundColor);
	}

	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = Color.decode(backgroundColor);
	}
	
	public String getAndroidNamespace() {
		return "http://schemas.android.com/apk/res/android";
	}
	
	public String getAndroidToolsNamespace() {
		return "http://schemas.android.com/tools";
	}
	
	public void handleJsonEvent(JsonParser parser, JsonParser.Event event, String key) {
		if(event == JsonParser.Event.VALUE_STRING)
		{
			String strValue = parser.getString();
			if(key.equals("name"))
			{
				this.setName(strValue);
			}
			if(key.equals("id"))
			{
				this.setId(strValue);
			}
			if(key.equals("type"))
			{
				this.setType(strValue);
			}
			if(key.equals("backgroundColor"))
			{
				this.setBackgroundColor(strValue);
			}
			if(key.equals("textColor"))
			{
				this.setTextColor(strValue);
			}
		}
		if(event == JsonParser.Event.VALUE_NUMBER)
		{
			int intValue = parser.getInt();
			if(key.equals("posX"))
				this.setPositionX(intValue);
			if(key.equals("posY"))
				this.setPositionY(intValue);
			if(key.equals("width"))
				this.setWidth(intValue);
			if(key.equals("height"))
				this.setHeight(intValue);
		}
	}
	
}
