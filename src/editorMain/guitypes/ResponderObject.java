package editorMain.guitypes;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ResponderObject extends GUIDElement {

	public ResponderObject() {
	}
	
	public Element toIOSXMLElement()
	{
		
		Document document = getDomWriter().getCurrentDocument();
		Element placeholder = document.createElement("placeholder");
		placeholder.setAttribute("placeholderIdentifier", "IBFirstResponder");
		placeholder.setAttribute("id", this.getGeneratedID());
		placeholder.setAttribute("sceneMemberID", "firstResponder");
		
		return placeholder;
	}
}
