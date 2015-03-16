package editorMain.guitypes;

import editorMain.dataTypes.DOMWriter;

public class DOMWriteable {
    
	public DOMWriteable() {
		m_pDomWriter = new DOMWriter();
	}
	
	/**
	 * DOMWriter instance that allows you to write XML
	 */
    private final DOMWriter m_pDomWriter;
    
    public DOMWriter getDomWriter()
    {
    	return m_pDomWriter;
    }
	
}
