package editorMain.guitypes;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import editorMain.dataTypes.DOMWriter;

public class MobileApplication extends BaseGUIType {
	public ArrayList<GUIActivity> m_pActivities;
	
	private ResponderObject m_pFirstResponder = new ResponderObject();
	
	public void initializeActivities()
	{
		m_pActivities = new ArrayList<GUIActivity>();
	}
	
	public void addActivity(GUIActivity activity)
	{
		this.m_pActivities.add(activity);
	}
	
	public ArrayList<GUIActivity> getActivities()
	{
		return m_pActivities;
	}
	
	public GUIActivity getLastActivity()
	{
		return m_pActivities.get(m_pActivities.size() - 1);
	}
	
	public String toIOSXMLString() {
		
		Document document = this.getDomWriter().getCurrentDocument();
		
		Element documentElement = document.createElement("document");
		documentElement.setAttribute("type", 					"com.apple.InterfaceBuilder3.CocoaTouch.Storyboard.XIB");
		documentElement.setAttribute("version", 				"3.0");
		documentElement.setAttribute("toolsVersion", 			"6254");
		documentElement.setAttribute("systemVersion", 			"14C109");
		documentElement.setAttribute("targetRuntime", 			"iOS.CocoaTouch");
		documentElement.setAttribute("propertyAccessControl", 	"none");
		documentElement.setAttribute("useAutolayout", 			"YES");
		documentElement.setAttribute("useTraitCollections", 	"YES");
		documentElement.setAttribute("initialViewController", 	this.getGeneratedID());
		
		Element dependencies = document.createElement("dependencies");
		Element plugin = document.createElement("plugIn");
		plugin.setAttribute("identifier", "com.apple.InterfaceBuilder.IBCocoaTouchPlugin");
		plugin.setAttribute("version", 	  "6247");
		dependencies.appendChild(plugin);
		documentElement.appendChild(dependencies);
		
		Element scenes = document.createElement("scenes");
		Element scene = document.createElement("scene");
		scene.setAttribute("sceneID", this.getGeneratedID());

		Element objects = document.createElement("objects");
		for(GUIActivity activity: m_pActivities)
		{
			objects.appendChild(activity.toIOSXMLElement());
		}
		objects.appendChild(m_pFirstResponder.toIOSXMLElement());

		scene.appendChild(objects);
		scenes.appendChild(scene);
		documentElement.appendChild(scenes);

		return this.getDomWriter().getStringFromElement(documentElement, false);
	}
	
	/**
	 * Returns an ArrayList of all Android XML files
	 * @return
	 */
	public ArrayList<String> toAndroidXMLString()
	{
		ArrayList<String> temp = new ArrayList<String>();
		DOMWriter writer = this.getDomWriter();
		for(GUIActivity activity: m_pActivities)
		{
			temp.add(writer.getStringFromElement(activity.toAndroidXMLElement(), true));
		}
		
		return temp;
	}
}
