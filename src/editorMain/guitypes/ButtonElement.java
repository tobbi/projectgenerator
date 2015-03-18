package editorMain.guitypes;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ButtonElement extends GUIElement {

	public ButtonElement() {
		super();
		this.setType("button");
		this.setAndroidElementName("Button");
		this.setIOSElementName("button");
	}

	@Override
	public Element toIOSXMLElement() {
		Element el = super.toIOSXMLElement();
		Document document = el.getOwnerDocument();

		// Enhance this button by setting the type to roundedRect:
		el.setAttribute("buttonType", "roundedRect");
		
		// Set information about key label
		Element stateElement = document.createElement("state");
		stateElement.setAttribute("key", "normal");
		stateElement.setAttribute("title", this.getLabel());
		
		// Set button text color
		if(this.getTextColor() != null)
		{
			stateElement.appendChild(this.getIOSTextColorElement());
		}
		el.appendChild(stateElement);
		
		return el;
	}
}
