import UIKit
import AVFoundation

var str = "Hello, playground"

class Person {
    let name: String
    init(_ aName: String) {
        name = aName
        print("\(name) is initialized")
    }

    var apartment: Apartment?
    deinit {
        print("\(name) is being deinitialized")
    }
}

class Apartment {
    let unit: String
    init(unit aUnit: String) {
        unit = aUnit
        print("\(unit) is initialized")
    }

    weak var tenant: Person?
    deinit {
        print("Apartment (\(unit)) is being deinitialized")
    }
}

var john: Person? = Person("John Doe")
var unit4A: Apartment? = Apartment(unit: "4A")

john?.apartment = unit4A
unit4A?.tenant = john

unit4A = nil
// sleep(5)
john = nil

class Country {
    let name: String
    var capitialCity: City!
    init(name: String, capitalName: String) {
        self.name = name
        capitialCity = City(name: capitalName, country: self)
    }

    deinit {
        print("Country (\(name)) is being deinitialized")
    }
}

class City {
    let name: String
    let country: Country
    init(name: String, country: Country) {
        self.name = name
        self.country = country
    }

    deinit {
        print("City (\(name)) is being deinitialized")
    }
}

var korea: Country? = Country(name: "한국", capitalName: "서울")
print("\(korea?.name ?? "")'s capital city is called \(korea?.capitialCity?.name ?? "")")
korea?.capitialCity = nil
korea = nil


class HTMLElement {
    let name: String
    let text: String?
    lazy var asHTML: () -> String = {
        [unowned self] in
        if let text = self.text {
            return "<\(self.name)>\(text)</\(self.name)>"
        } else {
            return "<\(self.name) />"
        }
    }
    init(name: String, text: String? = nil) {
        self.name = name
        self.text = text
    }
    deinit {
        print("\(name) is being deinitialized")
    }
    
}

var heading: HTMLElement = HTMLElement(name:"h1")
let defaultText = "some default text"
print(heading.asHTML())
heading.asHTML = {
    return "<\(heading.name)>\(heading.text ?? defaultText)</\(heading.name)>"
}
print(heading.asHTML())
//heading = nil

var paragraph: HTMLElement? = HTMLElement(name:"p", text: "hello, world")
print(paragraph?.asHTML() ?? "")
paragraph = nil

let names = ["크리스", "f알렉스", "1이와", "배리", "다니엘라"]
var reversedNames = names.sorted(by: >)

var optionalName = "Jone Appleseed" as String?
var greeting = "Hello!"
if let name = optionalName {
    greeting = "hello, \(name)"
}
print(greeting)

var optionalString = "Hello" as String?

if let str = optionalString {
    print(str)
} else {
    print("str is nil")
}

protocol ContnetPresentable: class/*, Layout*/ {
    var frame: CGRect { get set }
    var canPresentContent: Bool { get }
}

extension ContnetPresentable {
    var canPresentContent: Bool {
        return true
    }
}

extension UIImageView: ContnetPresentable {
    
}

extension AVPlayerLayer: ContnetPresentable {
}

struct someStrut {
    var name: String
}

/* 클래스만 가능함
extension someStrut: ContnetPresentable {
    
}
*/
