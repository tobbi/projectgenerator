package editorMain.guitypes;

import java.awt.Color;

import javax.json.stream.JsonParser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import editorMain.dataTypes.AdvancedPoint;

public abstract class BaseGUIType extends GUIDElement {

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

	@ExposedMember(methodName="getPositionX", returnType="int")
	public int getPositionX()
	{
		return m_pPositionX;
	}

	@ExposedMember(methodName="getPositionY", returnType="int")	
	public int getPositionY()
	{
		return m_pPositionY;
	}
	
	/**
	 * Sets the position X of this element relative to its parent
	 * @return
	 */
	@ExposedMember(methodName="setPositionX", returnType="void")
	public void setPositionX(int x)
	{
		m_pPositionX = x;
	}
	
	/**
	 * Sets the position Y of this element relative to its parent
	 * @return
	 */	
	@ExposedMember(methodName="setPositionY", returnType="void")
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
	@ExposedMember(methodName = "setWidth", returnType = "void")
	public void setWidth(int width)
	{
		m_pWidth = width;
	}
	
	/**
	 * Sets the position Y of this element relative to its parent
	 * @return
	 */	
	@ExposedMember(methodName = "setHeight", returnType = "void")
	public void setHeight(int height)
	{
		m_pHeight = height;
	}
	
	/**
	 * Gets the position X of this element relative to its parent
	 * @return
	 */	
	@ExposedMember(methodName = "getWidth", returnType = "int")
	public int getWidth(int width)
	{
		return m_pWidth;
	}
	
	/**
	 * Gets the position Y of this element relative to its parent
	 * @return
	 */	
	@ExposedMember(methodName = "getHeight", returnType = "int")
	public int getHeight(int height)
	{
		return m_pHeight;
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

	/**
	 * Returns the Type of this element
	 * @return
	 */
	public String getType() {
		return m_pType;
	}

	/**
	 * Sets the type of this element
	 * @param type The type of this element as a string
	 */
	public void setType(String type) {
		this.m_pType = type;
	}
	
	/**
	 * The element name in Android
	 */
	private String m_pAndroidElementName;
	
	/**
	 * The element name in iOS
	 */
	private String m_pIOSElementName;
	
	/**
	 * Returns this element's readable name
	 * @return This element's readable name
	 */
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
	
	@ExposedMember(methodName = "getBackgroundColor", returnType = "String")
	public String getBackgroundColorString() {
		return backgroundColor.toString();
	}
	
	public Element getIOSBackgroundColorElement() {
		return createIOSColorElement("backgroundColor", this.getBackgroundColorCalibrated());
	}
	
	private Element createIOSColorElement(String key, CalibratedRgb color) {
		Document document = this.getDomWriter().getCurrentDocument();
		Element element = document.createElement("color");
		element.setAttribute("key", key);
		element.setAttribute("red", String.valueOf(color.r));
		element.setAttribute("green", String.valueOf(color.g));
		element.setAttribute("blue", String.valueOf(color.b));
		element.setAttribute("alpha", String.valueOf(color.a));
		element.setAttribute("colorSpace", "calibratedRGB");
		return element;
	}
	
	private Element createIOSFrameElement()
	{
		Document document = this.getDomWriter().getCurrentDocument();
		Element element = document.createElement("rect");
		element.setAttribute("key", "frame");
		element.setAttribute("x", String.valueOf(this.getPosition().getX()));
		element.setAttribute("y", String.valueOf(this.getPosition().getY()));
		element.setAttribute("width", String.valueOf(this.getSize().getX()));
		element.setAttribute("height", String.valueOf(this.getSize().getY()));
		return element;
	}
	
	public Color getTextColor() {
		return textColor;
	}
	
	@ExposedMember(methodName = "getTextColor", returnType = "String")
	public String getTextColorString() {
		return textColor.toString();
	}
	
	public Element getIOSTextColorElement() {
		return createIOSColorElement("titleColor", this.getTextColorCalibrated());
	}
	
	public Element getIOSFrameElement() {
		return createIOSFrameElement();
	}
	
	public CalibratedRgb getTextColorCalibrated() {
		return toCalibratedRgb(textColor);
	}
	
	@ExposedMember(methodName = "setTextColor", returnType = "void")
	public void setTextColor(String textColor) {
		this.textColor = Color.decode(textColor);
	}
	
	public CalibratedRgb getBackgroundColorCalibrated() {
		return toCalibratedRgb(this.backgroundColor);
	}

	@ExposedMember(methodName = "setBackgroundColor", returnType = "String")
	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = Color.decode(backgroundColor);
	}
	
	public String getAndroidNamespace() {
		return "http://schemas.android.com/apk/res/android";
	}
	
	public String getAndroidToolsNamespace() {
		return "http://schemas.android.com/tools";
	}
	
	/**
	 * Handles this element JSOn parsing
	 * @param parser Parser instance
	 * @param event Current event
	 * @param key Current key
	 */
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

	public String getAndroidElementName() {
		return m_pAndroidElementName;
	}

	public void setAndroidElementName(String androidElementName) {
		this.m_pAndroidElementName = androidElementName;
	}

	public String getIOSElementName() {
		return m_pIOSElementName;
	}

	public void setIOSElementName(String iosElementName) {
		this.m_pIOSElementName = iosElementName;
	}
	
}
