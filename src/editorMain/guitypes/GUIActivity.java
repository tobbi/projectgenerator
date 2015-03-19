package editorMain.guitypes;

import java.io.File;
import java.util.ArrayList;

import javax.json.stream.JsonParser;
import javax.json.stream.JsonParser.Event;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class GUIActivity extends BaseGUIType {
	
	public String m_pProjectPath;
	
	public GUIActivity(String projectPath) {
		m_pProjectPath = projectPath;
	}
	
	/**
	 * Contains all GUI elements of the current Activity
	 */
	public ArrayList<GUIElement> m_pElements = new ArrayList<GUIElement>();
	
	/**
	 * Returns an element by its ID
	 */
	public GUIElement getElementById(String id)
	{
		for(GUIElement element: m_pElements)
		{
			if(element.getId().equals(id))
				return element;
		}
		return null;
	}

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
	
	/**
	 * String to the source file that interacts with this activity
	 * @return
	 */
	public String sourceFilePath;
	
	/**
	 * Gets the source file path for the current activity
	 * @return
	 */
	public String getSourceFilePath() {
		return sourceFilePath;
	}
	
	/**
	 * Sets the source file path for the current activity
	 * @param sourceFilePath The path of the source file to generate
	 */
	public void setSourceFilePath(String sourceFilePath) {
		File sourceFile = new File(m_pProjectPath + File.separator + sourceFilePath);
		if(!sourceFile.exists())
		{
			System.out.printf("Specified source file %s does not exist. Ignoring.",
							  sourceFile.getAbsolutePath());
			return;
		}
		this.sourceFilePath = sourceFilePath;
	}

	/**
	 * Generates an IOS XML element out of the information that we got
	 * @return
	 */
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
	
	/**
	 * Creates an Android XML element out of the information that we got
	 * @return
	 */
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

	@Override
	public void handleJsonEvent(JsonParser parser, Event event, String key) {

		if(event == JsonParser.Event.VALUE_STRING)
		{
			String strValue = parser.getString();
			if(key.equals("source_file"))
			{
				this.setSourceFilePath(strValue);
			}
		}
		super.handleJsonEvent(parser, event, key);
	}
	
}
