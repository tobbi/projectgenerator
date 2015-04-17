package editorMain.guitypes;

import javax.json.stream.JsonParser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class GUIElement extends BaseGUIType {
	
	public GUIElement() {
	}
	
	private String m_pLabel;

	public String getLabel() {
		return m_pLabel;
	}

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
			element.setAttribute("android:background", this.getBackgroundColorAsString());
		}
		
		if(this.getTextColor() != null)
		{
			element.setAttribute("android:textColor", this.getTextColorAsString());
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
