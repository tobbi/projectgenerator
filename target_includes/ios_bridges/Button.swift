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
    
    /**
    * Sets the size of this element
    * @param width The width of this element
    * @param height The height of this element
    */
    public func setSize(width: CGFloat, height: CGFloat)
    {
        self.innerButton.frame = CGRectMake(self.x, self.y, width, height);
    }
    
    /**
    * Sets the position of this element
    * @param x The x position of this element
    * @param y The y position of this element
    */
    public func setPosition(x: CGFloat, y: CGFloat)
    {
        self.innerButton.frame = CGRectMake(x, y, self.innerButton.frame.width, self.innerButton.frame.height);
    }
}
