package editorMain.dataTypes;

import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class DOMWriter {
	private static DocumentBuilderFactory m_pDocBuilderFactory;
	private static DocumentBuilder m_pDocBuilder;
	private static Document m_pDocument;
	
	public DOMWriter()
	{
		m_pDocBuilderFactory = DocumentBuilderFactory.newInstance();
		try {
			m_pDocBuilder = m_pDocBuilderFactory.newDocumentBuilder();
			m_pDocument = m_pDocBuilder.newDocument();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Document getCurrentDocument() {
		return m_pDocument;
	}
	
	/**
	 * Converts an XML element into a string.
	 * @param element The element to convert
	 * @param omitXmlDeclaration Whether or not to add an XML declaration to the created String
	 * @return String that contains the XML tags that were found in this element.
	 */
	public String getStringFromElement(Element element, Boolean omitXmlDeclaration)
	{
	    try
	    {
	       DOMSource domSource = new DOMSource(element);
	       StringWriter writer = new StringWriter();
	       StreamResult result = new StreamResult(writer);
	       TransformerFactory tf = TransformerFactory.newInstance();
	       Transformer transformer = tf.newTransformer();
	       if(omitXmlDeclaration) {
	    	   transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
	       }
	       transformer.transform(domSource, result);
	       return writer.toString();
	    }
	    catch(TransformerException ex)
	    {
	       ex.printStackTrace();
	       return null;
	    }
	} 
}
