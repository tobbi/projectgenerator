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
    
    /**
    * Public constructor of class text field
    */
    public init()
    {
        self.innerTextField = UITextField(frame: CGRect(x:0, y: 0, width: 100, height: 100));
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