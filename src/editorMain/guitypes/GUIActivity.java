package editorMain.guitypes;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class GUIActivity extends BaseGUIType {
	
	/**
	 * Contains all GUI elements of the current Activity
	 */
	public ArrayList<GUIElement> m_pElements = new ArrayList<GUIElement>();

	/**
	 * Adds a new Element to the Activity
	 * @param element The element to add
	 */
	public void addGUIElement(GUIElement element)
	{
		m_pElements.add(element);
	}
	
	public String getReadablePackageName() {
		return "com.example." + this.getReadableName().replace(" ", "_");
	}
	
	public Element toIOSXMLElement() {
		Document document = this.getDomWriter().getCurrentDocument();
		
		Element viewController = document.createElement("viewController");
		viewController.setAttribute("id", generateID());
		viewController.setAttribute("customClass", "viewController");
		viewController.setAttribute("customName", getName());
		viewController.setAttribute("customModuleProvider", "target");
		viewController.setAttribute("sceneMemberID", "viewController");
		
		Element view = document.createElement("view");
		view.setAttribute("key", "view");
		view.setAttribute("contentMode", "scaleToFill");
		view.setAttribute("id", getGeneratedID());
		
		Element rect = document.createElement("rect");
		rect.setAttribute("x", String.valueOf( getPosition().getX() ) );
		rect.setAttribute("y", String.valueOf( getPosition().getY() ) );
		rect.setAttribute("width", String.valueOf( getSize().getX() ) );
		rect.setAttribute("height", String.valueOf( getSize().getY() ) );
		
		Element subviews = document.createElement("subviews");
		Element connections = document.createElement("connections");
		for(GUIElement guiElement: this.m_pElements)
		{
			subviews.appendChild(guiElement.toIOSXMLElement());
			connections.appendChild(guiElement.getCurrentOutlet().toIOSXMLElement());
		}
		view.appendChild(subviews);
		viewController.appendChild(view);
		viewController.appendChild(connections);
		
		return viewController;
	}
	
	public Element toAndroidXMLElement() {
		Document document = this.getDomWriter().getCurrentDocument();
		
		Element relativeLayout = document.createElement("RelativeLayout");

		// Namespace declaration
		relativeLayout.setAttribute("xmlns:android", getAndroidNamespace());
		relativeLayout.setAttribute("xmlns:tools", getAndroidToolsNamespace());

		relativeLayout.setAttribute("android:layout_width",  this.getSize().getDpX());
		relativeLayout.setAttribute("android:layout_height", this.getSize().getDpY());
		relativeLayout.setAttribute("android:paddingTop",    "@dimen/activity_vertical_margin");
		relativeLayout.setAttribute("android:paddingBottom", "@dimen/activity_vertical_margin");
		relativeLayout.setAttribute("android:paddingLeft",   "@dimen/activity_horizontal_margin");
		relativeLayout.setAttribute("android:paddingRight",  "@dimen/activity_horizontal_margin");
		relativeLayout.setAttribute("tools:context",         getReadablePackageName());

		for(GUIElement guiElement: this.m_pElements)
		{
			relativeLayout.appendChild(guiElement.toAndroidElement());
		}
		
		return relativeLayout;
	}
	
}
