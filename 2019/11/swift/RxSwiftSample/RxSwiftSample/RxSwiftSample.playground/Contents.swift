import UIKit
import RxSwift

var str = "Hello, playground"
print(str)

let observable = Observable.of("A" ,"B", "C")
let subscription = observable.subscribe { event in
    print(event.element)
}
