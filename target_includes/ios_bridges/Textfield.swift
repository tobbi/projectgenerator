//
//  Textfield.swift
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
    
    /**
     * Text field's X and Y coordinates
     */
    private var x: CGFloat = 0, y: CGFloat = 0;

    /**
     * Specifies the place where the textfield goes.
     */
    private var parentUIContext: UIViewController;
    
    /**
     * Specifies the place where the handlers are defined
     */
    private var parentEventContext: UIResponder;
    
    /**
    * Public constructor of class text field
    */
    public init(context: UIViewController!, eventContext: UIResponder!)
    {
        self.parentUIContext      = context;
        self.parentEventContext = eventContext;
        
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
     * Returns the number that might be contained in this text
     * @return Double value of this text
     */
    public func getNumber() -> Double
    {
        return (getText() as NSString).doubleValue;
    }
    
    /**
    * Sets the passed number (parameter number) as text
    * @param number The number to set as text
    */
    public func setNumber(number: Double)
    {
        setText("\(number)");
    }
    
    /**
    * Sets the passed number (parameter number) as text
    * @param number The number to set as text
    */
    public func setNumber(number: Int)
    {
        setText("\(number)");
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
        var screenWidth = (self.parentUIContext as! ApplicationView).getWidth();
        var screenHeight = (self.parentUIContext as! ApplicationView).getHeight();
        
        var newWidth = (screenWidth / 100) * width;
        var newHeight = (screenHeight / 100) * height;

        self.innerTextField.frame = CGRectMake(self.x, self.y, newWidth, newHeight);
    }
    
    /**
    * Sets the size of this element
    * @param width The width of this element in percent
    * @param height The height of this element in percent
    */
    public func setSize(width: Int, height: Int)
    {
        var f_width = CGFloat(width);
        var f_height = CGFloat(height);
        setSize(f_width, height: f_height);
    }
    
    /**
    * Sets the position of this element
    * @param x The x position of this element
    * @param y The y position of this element
    */
    public func setPosition(x: CGFloat, y: CGFloat)
    {
        var screenWidth = (self.parentUIContext as! ApplicationView).getWidth();
        var screenHeight = (self.parentUIContext as! ApplicationView).getHeight();
        
        var newX = (screenWidth / 100) * x;
        var newY = (screenHeight / 100) * y;

        self.innerTextField.frame = CGRectMake(newX, newY, self.innerTextField.frame.width, self.innerTextField.frame.height);
    }
    
    /**
    * Sets the position of this element
    * @param x The x position of this element
    * @param y The y position of this element
    */
    public func setPosition(x: Int, y: Int)
    {
        var f_x = CGFloat(x);
        var f_y = CGFloat(y);
        setPosition(f_x, y: f_y);
    }
    
    /**
    * Gets the wrapped element
    */
    public func getWrappedElement() -> UITextField
    {
        return innerTextField;
    }
    
    /**
     * Adds this text field to its parent application view
     */
    public func addToApplicationView()
    {
        parentUIContext.view.addSubview(getWrappedElement());
    }
}