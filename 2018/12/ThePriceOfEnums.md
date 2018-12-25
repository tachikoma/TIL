# The Price of ENUMs

보통의 자바에서는 enum을 쓰는 것이 좋지만

안드로이드 DEX에서는 메모리가 정수를 쓸 때 보다 많이 (약 13배) 늘어나기 때문에

enum은 지양하고 @IntDef annotation을 사용하여 정수를 쓰는 것 처럼 최적한된 코드를 사용할 수 있다.

출처 : [The price of ENUMs](https://brunch.co.kr/@oemilk/94)

