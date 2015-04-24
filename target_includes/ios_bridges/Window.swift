//
//  Window.swift
//  MobileApplicationsSwiftAufgabe1
//
//  Created by Tobias Markus on 21.04.15.
//  Copyright (c) 2015 Tobias Markus. All rights reserved.
//

import Foundation
import UIKit

/**
 * Class that represents an iOS Application Window
 */
public class Window {
    
    /**
     * Variable that saves the parent UIResponder object (for later reference)
     */
    var parentContext: UIResponder;
    
    /**
     * Wrapped UIWindow instance
     */
    var parentWindow: UIWindow;
    
    /**
     * Initialize window
     * @param context The UI Responder object (aka main function) we create this activity for
     */
    public init(context: UIResponder) {
        parentContext = context;
        
        // Größe festlegen:
        parentWindow = UIWindow(frame: UIScreen.mainScreen().bounds);
        
        // Hintergrundfarbe festlegen:
        parentWindow.backgroundColor = UIColor.whiteColor();
    }
    
    /**
     * Function call required by iOS
     */
    required public init(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    /**
     * Return the wrapped UIWindow instance
     * @return UIWindow The wrapped UIWindow instance
     */
    public func getWrappedElement() -> UIWindow {
        return parentWindow;
    }
    
    /**
     * Create an activity inside this window
     * @return The created activity
     */
    public func createActivity() -> Activity {
        var activity = Activity(context: parentContext);
        parentWindow.rootViewController = activity;
        return activity;
    }
}