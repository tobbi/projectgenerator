package editorMain.guitypes;

import org.w3c.dom.Element;

public class ButtonElement extends GUIElement {

	public ButtonElement() {
		super();
	}

	@Override
	public Element toIOSXMLElement() {
		Element el = super.toIOSXMLElement();

		// Enhance this button by setting the type to roundedRect:
		el.setAttribute("buttonType", "roundedRect");
		
		return el;
	}
}
