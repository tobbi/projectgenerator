package editorMain;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import editorMain.guitypes.BaseGUIType;
import editorMain.guitypes.ButtonElement;
import editorMain.guitypes.GUIActivity;
import editorMain.guitypes.GUIElement;
import editorMain.guitypes.MobileApplication;
import editorMain.guitypes.TextfieldElement;

import javax.json.*;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParsingException;
import javax.swing.JOptionPane;

import java.util.Stack;

public class JSONParserClass {
	
	private FileInputStream m_pFileInputStream;
    private MainDialog m_pParentDialog;
    private JsonParser m_pJsonParser;
    private JsonParser.Event m_pCurrentJsonEvent;
    
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
			try {
				m_pCurrentJsonEvent = m_pJsonParser.next();
			}
			catch(JsonParsingException e)
			{
				showMessageBox("Nicht wohlgeformte JSON-Datei. Fehlermeldung:\r\n"
						+ e.getMessage());
			}
			if(m_pCurrentJsonEvent == JsonParser.Event.KEY_NAME)
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

			if(m_pCurrentJsonEvent == JsonParser.Event.START_OBJECT)
			{
				String lastElement = m_pElementStack.lastElement();

				// Sind wir im Element, wird das Event von den Subklassen verarbeitet
				if(lastElement.equals("element"))
				{
					m_pCurrentElement.handleJsonEvent(m_pJsonParser, m_pCurrentJsonEvent, m_pCurrentKeyName);
				}

				if(lastElement.equals("_application"))
				{
					m_pCurrentElement = m_pApplication = new MobileApplication();
					m_pElementStack.push("application");
				}
				if(lastElement.equals("_activities"))
				{
					m_pCurrentElement = new GUIActivity();
					m_pApplication.addActivity((GUIActivity)m_pCurrentElement);
					m_pElementStack.push("activity");
				}
				
				if(lastElement.equals("buttons"))
				{
					m_pCurrentElement = new ButtonElement();
					m_pElementStack.push("element");
				}
				if(lastElement.equals("textfields"))
				{
					m_pCurrentElement = new TextfieldElement();
					m_pElementStack.push("element");
				}
				
			}
			if(m_pCurrentJsonEvent == JsonParser.Event.END_OBJECT)
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
			if(m_pCurrentJsonEvent == JsonParser.Event.START_ARRAY)
			{
				if(m_pElementStack.isEmpty())
					  continue;

				
				
				m_pElementStack.push(m_pCurrentKeyName);
			}
			if(m_pCurrentJsonEvent == JsonParser.Event.END_ARRAY)
			{
				m_pElementStack.pop();
			}

			// Values are handled by subclasses
			if(m_pCurrentJsonEvent == JsonParser.Event.VALUE_STRING || 
			   m_pCurrentJsonEvent == JsonParser.Event.VALUE_NUMBER ||
			   m_pCurrentJsonEvent == JsonParser.Event.VALUE_FALSE  ||
			   m_pCurrentJsonEvent == JsonParser.Event.VALUE_TRUE ||
			   m_pCurrentJsonEvent == JsonParser.Event.VALUE_NULL)
			{	
				m_pCurrentElement.handleJsonEvent(m_pJsonParser, m_pCurrentJsonEvent, m_pCurrentKeyName);
				m_pElementStack.pop();
			}
		}
		
		m_pParentDialog.createTreeFromActivityList(m_pApplication);
	}
}
