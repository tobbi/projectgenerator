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
*/
public class Button {
    
    private var innerButton: UIButton;
    private var x: CGFloat = 0, y: CGFloat = 0;
    private var parentContext: UIViewController;
    
    /**
    * Public constructor of class "Button"
    */
    public init(context: UIViewController!)
    {
        // Set parent context:
        self.parentContext = context;
        
        // Standardwerte für den Button festlegen.
        self.innerButton = UIButton(frame: CGRect(x: 0, y: 0, width: 100, height: 30));
        self.innerButton.backgroundColor = UIColor.whiteColor();
        self.innerButton.setTitleColor(UIColor.blackColor(), forState: UIControlState.Normal);
        self.innerButton.setTitleColor(UIColor.blueColor(), forState: UIControlState.Selected);
        self.innerButton.layer.borderWidth = 1;
        self.innerButton.layer.cornerRadius = 5;
        if let titleLabel = self.innerButton.titleLabel
        {
            titleLabel.font = UIFont(name: "Arial", size: 12);
        }
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
        self.innerButton.setTitle(label, forState: UIControlState.Normal);
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
        self.x = x;
        self.y = y;
        self.innerButton.frame = CGRectMake(x, y, self.innerButton.frame.width, self.innerButton.frame.height);
    }
    
    /**
    * Gets the wrapped element
    */
    public func getRawElement() -> UIButton
    {
        return innerButton;
    }
    
    /**
     * Adds this button to the specified activity
     * @param activity: The activity to add the button to
     */
    public func addToActivity()
    {
        parentContext.view.addSubview(getRawElement());
    }
    
    /**
     * Adds an onClick listener to this button
     * @param sender The object the listener is in
     * @param listenerName Function name of the listener
     */
    public func addOnClickListener(listenerName: String) {
        innerButton.addTarget(parentContext,
            action: Selector(listenerName + ":"),
            forControlEvents: UIControlEvents.TouchUpInside);
    }
}
