import UIKit

var completionHandlers: [() -> Void] = []

func withEscaping(completion: @escaping () -> Void) {
    // 함수 밖에 있는 completionHandlers 배열에 해당 클로저를 저장
    completionHandlers.append(completion)
}

func withoutEscaping(completion: () -> Void) {
    completion()
}

class MyClass {
    var x = 10
    func callFunc() {
        withEscaping { [weak self] in
            guard let self = self else { return } // 여기에는 꼭 필요하다.
            self.x = 100 }
        withoutEscaping { /*[weak self] in
            guard let self = self else { return }*/  // 여기는 필요없다.
            self.x = 200 }
    }
    
    deinit {
        print("\(self) deinit")
    }
}

var mc: MyClass! = MyClass()
mc.callFunc()
print(mc.x)
completionHandlers.first?()
print(mc.x)
mc = nil

func getSumOf(array:[Int], handler: @escaping ((Int)->Void)) {
    //step 2
    var sum: Int = 0
    for value in array {
        sum += value
    }
    //step 3. 비동기 작업
    DispatchQueue.global().asyncAfter(deadline: .now() + 1.0){
        handler(sum)
    }
}

func doSomething() {
    //step 1
    getSumOf(array: [16,756,442,6,23]) { (sum) in
        print(sum)
        //step 4. 함수 종료
    }
}

doSomething()

