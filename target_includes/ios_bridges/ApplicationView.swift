//
//  Activity.swift
//  MobileApplicationsSwiftAufgabe1
//
//  Created by Tobias Markus on 21.04.15.
//  Copyright (c) 2015 Tobias Markus. All rights reserved.
//

import Foundation
import UIKit

public class ApplicationView: UIViewController {

    /**
     * Array containing all subviews of this activity
     */
    var containedViews = [UIView]();
    
    /**
     * UIResponder parent context (aka Main function)
     */
    var parentContext: UIResponder;
    
    var width: CGFloat = 0, height: CGFloat = 0;
    
    /**
     * Initialize application view
     * @param context The UIResponder parent (aka "Main function")
     */
    public init(context: UIResponder, width: CGFloat, height: CGFloat) {
        self.parentContext = context;
        self.width         = width;
        self.height        = height;
        
        super.init(nibName: nil, bundle: nil);
    }

    /**
     * Function call required by iOS
     */
    required public init(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    /**
     * Adds a new text field to this application view
     * @param textfield The text field to add to this application view
     */
    public func addTextfield(textfield: Textfield)
    {
        containedViews.append(textfield.getWrappedElement());
    }
    
    /**
    * Adds a new button to this application view
    * @param button The button to add to this application view
    */
    public func addButton(button: Button)
    {
        containedViews.append(button.getWrappedElement());
    }
 
    /**
    * Creates a new text field element
    * @return The created text field element
    */
    public func createTextfield() -> Textfield
    {
        return Textfield(context: self, eventContext: self.parentContext);
    }
    
    /**
    * Creates a new button element
    * @return The created button element
    */
    public func createButton() -> Button
    {
        return Button(context:self, eventContext: self.parentContext);
    }
    
    /**
     * Puts all added elements on this application view
     */
    private func initializeUIElements() {
        for containedView in containedViews {
            view.addSubview(containedView);
        }
    }
    
    /**
     * Function that is executed when the view has been loaded
     */
    override public func viewDidLoad() {
        super.viewDidLoad();
        initializeUIElements();
    }
}