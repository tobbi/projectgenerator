package editorMain.guitypes;

import org.w3c.dom.Element;

public class ButtonElement extends GUIElement {

	public Element toIOSXMLElement() {
		Element el = super.toIOSXMLElement();
		
		Element buttonNode = (Element)el.getElementsByTagName("button").item(0);
		buttonNode.setAttribute("buttonType", "roundedRect");
		
		return el;
	}
}
