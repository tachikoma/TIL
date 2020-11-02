//: A Cocoa based Playground to present user interface

import AppKit
import PlaygroundSupport

let nibFile = NSNib.Name("MyView")
var topLevelObjects : NSArray?

Bundle.main.loadNibNamed(nibFile, owner:nil, topLevelObjects: &topLevelObjects)
let views = (topLevelObjects as! Array<Any>).filter { $0 is NSView }

var boundBox: CGRect = CGRect(origin: CGPoint(x:10.0,y:10.0), size: CGSize(width: 10, height: 10))
boundBox = boundBox.union(CGRect(origin: CGPoint(x:20.0, y:200.0), size: CGSize(width: 0, height: 0)))

print(boundBox)

// Present the view in Playground
PlaygroundPage.current.liveView = views[0] as! NSView

