package editorMain;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import editorMain.guitypes.BaseGUIType;
import editorMain.guitypes.ButtonElement;
import editorMain.guitypes.GUIActivity;
import editorMain.guitypes.GUIElement;
import editorMain.guitypes.MobileApplication;

import javax.json.*;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParsingException;
import javax.swing.JOptionPane;

import java.util.Stack;

public class JSONParserClass {
	
	private FileInputStream m_pFileInputStream;
    private MainDialog m_pParentDialog;
    private JsonParser m_pJsonParser;
    
    private MobileApplication m_pApplication;
    private Stack<String> m_pElementStack = new Stack<String>();
    private String m_pCurrentKeyName;
    private BaseGUIType m_pCurrentElement;

	public JSONParserClass(MainDialog parent) {
        m_pParentDialog = parent;
	}
	
	private void showMessageBox(String message) {
		JOptionPane.showMessageDialog(m_pParentDialog, message);
	}

    /**
     * Parse file and call createTreeFromActivityList
     * @param filename Name of the file to parse
     * @return
     */
	public void parse(String filename) {
		try {
			m_pFileInputStream  = new FileInputStream(filename);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		m_pJsonParser = Json.createParser(m_pFileInputStream);

		while(m_pJsonParser.hasNext())
		{
			JsonParser.Event current = null;
			try {
				current = m_pJsonParser.next();
			}
			catch(JsonParsingException e)
			{
				showMessageBox("Nicht wohlgeformte JSON-Datei. Fehlermeldung:\r\n"
						+ e.getMessage());
			}
			if(current == JsonParser.Event.KEY_NAME)
			{
				m_pCurrentKeyName = m_pJsonParser.getString();
				if(m_pCurrentKeyName.equalsIgnoreCase("_activities"))
				{
					m_pApplication.initializeActivities();
				}
				m_pElementStack.push(m_pCurrentKeyName);
			}
			
			if(m_pElementStack.empty())
				continue;

			if(current == JsonParser.Event.START_OBJECT)
			{
				String lastElement = m_pElementStack.lastElement();

				// Sind wir im Element, wird das Event von den Subklassen verarbeitet
				if(lastElement.equals("element"))
				{
					m_pCurrentElement.handleJsonEvent(m_pJsonParser, current, m_pCurrentKeyName);
				}

				if(lastElement.equals("_application"))
				{
					m_pApplication = new MobileApplication();
					m_pCurrentElement = m_pApplication;
					m_pElementStack.push("application");
				}
				if(lastElement.equals("_activities"))
				{
					m_pCurrentElement = new GUIActivity();
					m_pApplication.addActivity((GUIActivity)m_pCurrentElement);
					m_pElementStack.push("activity");
				}
				/*if(lastElement.equals("_elements"))
				{
					m_pCurrentElement = new GUIElement();
					m_pElementStack.push("element");
				}*/
				if(lastElement.equals("buttons"))
				{
					m_pCurrentElement = new ButtonElement();
					m_pElementStack.push("element");
				}
				if(lastElement.equals("textfields"))
				{
					m_pCurrentElement = new GUIElement();
					m_pElementStack.push("element");
				}
				
			}
			if(current == JsonParser.Event.END_OBJECT)
			{
				if(m_pElementStack.lastElement().equals("element"))
				{
					GUIElement currentElement = (GUIElement)m_pCurrentElement;

					// Add element to activity
					if(currentElement.getName() == null)
					{
						currentElement.setName(m_pCurrentElement.getId());
					}

					// Prepare an outlet from the information that we got:
					currentElement.createOutlet();

					m_pApplication.getLastActivity().addGUIElement(currentElement);
				}
				
				m_pElementStack.pop();
			}
			if(current == JsonParser.Event.START_ARRAY)
			{
				if(m_pElementStack.isEmpty())
					  continue;

				
				
				m_pElementStack.push(m_pCurrentKeyName);
			}
			if(current == JsonParser.Event.END_ARRAY)
			{
				m_pElementStack.pop();
			}

			// Values are handled by subclasses
			if(current == JsonParser.Event.VALUE_STRING || 
			   current == JsonParser.Event.VALUE_NUMBER ||
			   current == JsonParser.Event.VALUE_FALSE  ||
			   current == JsonParser.Event.VALUE_TRUE ||
			   current == JsonParser.Event.VALUE_NULL)
			{	
				m_pCurrentElement.handleJsonEvent(m_pJsonParser, current, m_pCurrentKeyName);
				m_pElementStack.pop();
			}
		}
		
		m_pParentDialog.createTreeFromActivityList(m_pApplication);
	}
}
