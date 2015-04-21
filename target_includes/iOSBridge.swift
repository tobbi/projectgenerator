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
* Class that represents a button
* @author tobiasmarkus
*
*/
public class Button {
    
    private var innerButton: UIButton;
    private var x: CGFloat = 0, y: CGFloat = 0;
    
    /**
    * Public constructor of class "Button"
    */
    public init()
    {
        innerButton = UIButton(frame: CGRect(x: 0, y: 0, width: 50, height: 50));
    }
    
    /**
    * Returns the label for this button
    * @return String The label for this element
    */
    public func getLabel() -> String
    {
        if let titleLabel = self.innerButton.titleLabel
        {
            if let text = titleLabel.text
            {
                return text;
            }
        }
        
        return "";
    }
    
    /**
    * Sets the label for this button
    * @param String The label to set
    */
    public func setLabel(label: String)
    {
        if let titleLabel = self.innerButton.titleLabel
        {
            titleLabel.text = label;
        }
    }
    
    public func setSize(width: CGFloat, height: CGFloat)
    {
        self.innerButton.frame = CGRectMake(self.x, self.y, width, height);
    }
    
    public func setPosition(x: CGFloat, y: CGFloat)
    {
        self.innerButton.frame = CGRectMake(x, y, self.innerButton.frame.width, self.innerButton.frame.height);
    }
}

/**
* Class that represents a text field.
* @author tobiasmarkus
*/
public class Textfield {
    
    private var innerTextField: UITextField;
    private var x: CGFloat = 0, y: CGFloat = 0;
    
    /**
    * Public constructor of class text field
    */
    public init()
    {
        self.innerTextField = UITextField(frame: CGRect(x:0, y: 0, width: 200, height: 50));
    }
    
    public func setSize(width: CGFloat, height: CGFloat)
    {
        self.innerTextField.frame = CGRectMake(self.x, self.y, width, height);
    }
    
    public func setPosition(x: CGFloat, y: CGFloat)
    {
        self.innerTextField.frame = CGRectMake(x, y, self.innerTextField.frame.width, self.innerTextField.frame.height);
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
}