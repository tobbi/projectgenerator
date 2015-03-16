package editorMain.guitypes;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class GUIOutlet extends GUIDElement {
	
	
	private String name;
	private GUIDElement target;

	public GUIOutlet(String name, GUIDElement target)
	{
		this.name = name;
		this.target = target;
	}
	
	public Element toIOSXMLElement()
	{
		Document document = this.getDomWriter().getCurrentDocument();
		Element outletElement = document.createElement("outlet");
		outletElement.setAttribute("property", name);
		outletElement.setAttribute("destination", target.getGeneratedID());
		outletElement.setAttribute("id", this.getGeneratedID());
		return outletElement;
	}
}
