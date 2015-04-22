//
//  Activity.swift
//  MobileApplicationsSwiftAufgabe1
//
//  Created by Tobias Markus on 21.04.15.
//  Copyright (c) 2015 Tobias Markus. All rights reserved.
//

import Foundation
import UIKit

public class Activity {

    private var innerActivity: UIViewController
    
    private var containedViews: [UIView] = [UIView]();
    
    public init() {
        innerActivity = UIViewController();
    }
    
    public func addElement(textfield: Textfield)
    {
        containedViews.append(textfield.getRawElement());
    }
    
    public func addElement(button: Button)
    {
        containedViews.append(button.getRawElement());
    }
}