package editorMain.guitypes;

import org.w3c.dom.Element;

import editorMain.JavaCodeParser;

public class TextfieldElement extends GUIElement {

	public TextfieldElement() {
		super();
		this.setType("textfield");
		this.setIOSElementName("textField");
		this.setAndroidElementName("EditText");
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public Element toIOSXMLElement() {
		Element element = super.toIOSXMLElement();
		
		// Set textfield text
		element.setAttribute("text", this.getLabel());
		
		return element;
	}
}
