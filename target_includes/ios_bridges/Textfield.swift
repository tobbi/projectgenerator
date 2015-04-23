//
//  Button.swift
//  MobileApplicationsSwiftAufgabe1
//
//  Created by Tobias Markus on 21.04.15.
//  Copyright (c) 2015 Tobias Markus. All rights reserved.
//

import Foundation
import UIKit

/**
* Class that represents a text field.
* @author tobiasmarkus
*/
public class Textfield {
    
    private var innerTextField: UITextField;
    private var x: CGFloat = 0, y: CGFloat = 0;
    private var parentContext: UIViewController;
    
    /**
    * Public constructor of class text field
    */
    public init(context: UIViewController!)
    {
        self.parentContext = context;
        
        // Initialize text field
        self.innerTextField = UITextField(frame: CGRect(x:0, y: 0, width: 100, height: 100));

        // Set the look of this element
        self.innerTextField.layer.borderColor = UIColor.lightGrayColor().CGColor;
        self.innerTextField.layer.cornerRadius = 5;
        self.innerTextField.layer.borderWidth = 0.5;
    }
    
    /**
    * Sets the text for this text field
    * @param text String The text for this text field
    */
    public func setText(text: String)
    {
        innerTextField.text = text;
    }
    
    /**
    * Returns the text for this text field
    * @return String The text for this text field
    */
    public func getText() -> String
    {
        return innerTextField.text;
    }
    
    /**
     * Adds text to this text field
     * @text The text to add to this text field
     */
    public func addText(text: String)
    {
        innerTextField.text! += text;
    }
    
    /**
     * Removes all text from this text field
     */
    public func clear()
    {
        innerTextField.text! = "";
    }
    
    /**
    * Sets the size of this element
    * @param width The width of this element
    * @param height The height of this element
    */
    public func setSize(width: CGFloat, height: CGFloat)
    {
        self.innerTextField.frame = CGRectMake(self.x, self.y, width, height);
    }
    
    /**
    * Sets the position of this element
    * @param x The x position of this element
    * @param y The y position of this element
    */
    public func setPosition(x: CGFloat, y: CGFloat)
    {
        self.innerTextField.frame = CGRectMake(x, y, self.innerTextField.frame.width, self.innerTextField.frame.height);
    }
    
    /**
    * Gets the wrapped element
    */
    public func getRawElement() -> UITextField
    {
        return innerTextField;
    }
    
    /**
     * Adds this text field to its parent activity
     */
    public func addToActivity()
    {
        parentContext.view.addSubview(getRawElement());
    }
}