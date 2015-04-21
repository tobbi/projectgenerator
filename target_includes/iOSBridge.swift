/**
* Class that represents a button
* @author tobiasmarkus
*
*/

import UIKit

public class Button: UIButton {
    
    /**
     * Label of this button
     */
    var label: String;
    
    /**
    * Public constructor of class "Button"
    */
    public init()
    {
    }
    
    /**
     * Returns the label for this button
     * @return String The label for this element
     */
    public class func getLabel() -> String
    {
      return this.label;
    }

    /**
    * Sets the label for this button
    * @param String The label to set
    */
    public class func setLabel(label: String)
    {
      this.label = label;
    }
}

/**
* Class that represents a text field.
* @author tobiasmarkus
*/
class Textfield {
    
    /**
     *  The text of this text field
     */
    var text: String;

    /**
    * Public constructor of class text field
    */
    public init()
    {
    }
    
    /**
     * Sets the text for this text field
     * @param text String The text for this text field
     */
    public class func setText(text: String)
    {
      this.text = text;
    }
    
    /**
     * Returns the text for this text field
     * @return String The text for this text field
     */
    public class func getText() -> String
    {
        return self.text;
    }
}