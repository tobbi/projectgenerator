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

    var innerActivity: UIViewController = UIViewController();
    var containedViews = [UIView]();
    
    public init() {
    }
    
    public func addElement(textfield: Textfield)
    {
        innerActivity.view.addSubview(textfield.getRawElement());
        containedViews.append(textfield.getRawElement());
    }
    
    public func addElement(button: Button)
    {
        innerActivity.view.addSubview(button.getRawElement());
        containedViews.append(button.getRawElement());
    }
}