package editorMain.guitypes;

import java.awt.Color;

import javax.json.stream.JsonParser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import editorMain.JavaCodeParser;

public class GUIElement extends BaseGUIType {
	
	public GUIElement() {
	}
	
	private String m_pLabel;

	@ExposedMember(methodName = "getLabel", returnType = "string")
	public String getLabel() {
		return m_pLabel;
	}

	@ExposedMember(methodName = "setLabel", returnType = "void")
	public void setLabel(String label) {
		this.m_pLabel = label;
	}
	
	private GUIOutlet currentOutlet;

	public GUIOutlet getCurrentOutlet() {
		return currentOutlet;
	}

	public void createOutlet() {
		this.currentOutlet = new GUIOutlet(this.getReadableName(), this);
	}
	
	/**
	 * Converts a boolean value into its string representation
	 * @param value The boolean value to convert
	 * @return iOS-String representation of the value
	 */
	public String boolToIOSBool(Boolean value) {
		if(value)
		  return "YES";

		return "NO";
	}
	
	public Element toIOSXMLElement() {
		Document document = this.getDomWriter().getCurrentDocument();
		
		Element typeElement = document.createElement(getIOSElementName());
		typeElement.setAttribute("opaque", boolToIOSBool(false));
		typeElement.setAttribute("contentMode", "scaleToFill");
		typeElement.setAttribute("fixedFrame", boolToIOSBool(true));
		typeElement.setAttribute("contentHorizontalAlignment", "center");
		typeElement.setAttribute("lineBreakMode", "middleTruncation");
		typeElement.setAttribute("translatesAutoresizingMaskIntoConstraints", boolToIOSBool(false));
		typeElement.setAttribute("id", this.getGeneratedID());
		

		typeElement.appendChild(this.getIOSFrameElement());
		
		if(this.getBackgroundColor() != null)
		{	
			typeElement.appendChild(this.getIOSBackgroundColorElement());
		}

		return typeElement;
	}
	
	public Element toAndroidElement()
	{
		Document document = this.getDomWriter().getCurrentDocument();
		Element element = document.createElement(getAndroidElementName());
		element.setAttribute("android:id", "@+id/" + this.getReadableName());
		element.setAttribute("android:text", this.getLabel());
		element.setAttribute("android:layout_width", "wrap_content");
		element.setAttribute("android:layout_height", "wrap_content");
		
		element.setAttribute("android:layout_alignParentLeft", "true");
		element.setAttribute("android:layout_alignParentTop",  "true");
		element.setAttribute("android:layout_marginLeft", this.getPosition().getDpX());
		element.setAttribute("android:layout_marginTop",  this.getPosition().getDpY());
		element.setAttribute("android:minWidth", this.getSize().getDpX());
		element.setAttribute("android:minHeight", this.getSize().getDpY());
		
		if(this.getBackgroundColor() != null)
		{
			Color bgcolor = this.getBackgroundColor();
			
			// Source http://stackoverflow.com/questions/3607858/how-to-convert-a-rgb-color-value-to-an-hexadecimal-value-in-java
			String colorHexValue = String.format("#%02X%02X%02X", bgcolor.getRed(), bgcolor.getGreen(), bgcolor.getBlue());
			element.setAttribute("android:background", colorHexValue);
		}
		
		if(this.getTextColor() != null)
		{
			Color textColor = this.getTextColor();
			
			String colorHexValue = String.format("#%02X%02X%02X", textColor.getRed(), textColor.getGreen(), textColor.getBlue());
			element.setAttribute("android:textColor", colorHexValue);
		}
		
	    /*<Button
        android:id="@+id/Button01"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_alignBaseline="@+id/button1"
        android:layout_alignBottom="@+id/button1"
        android:layout_toRightOf="@+id/button1"
        android:text="2" />*/
		
		return element;
	}
	
	@Override
	public void handleJsonEvent(JsonParser parser, JsonParser.Event event, String key) {
		super.handleJsonEvent(parser, event, key);
		
		if(event == JsonParser.Event.VALUE_STRING)
		{
			String strValue = parser.getString();	
			if(key.equals("label"))
			{
				this.setLabel(strValue);
			}
		}
	}
	
}
