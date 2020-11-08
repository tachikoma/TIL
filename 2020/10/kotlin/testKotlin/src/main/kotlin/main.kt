import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.rxkotlin.toObservable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit
import kotlin.random.Random
import kotlin.system.measureTimeMillis

fun main(args: Array<String>) {
    println("Hello World! ${args.map { it }}")

    val list: List<Any> = listOf("One", 2, "Three", "Four", 4.5, "Five", 6.0f)
    val iterator = list.iterator()
    while (iterator.hasNext()) {
        println(iterator.next())
    }

    val observable: Observable<Any> = list.toObservable()

    observable.subscribeBy(
        onNext = { println(it) },
        onError = { it.printStackTrace() },
        onComplete = { println("Done!") }
    )

    val subject: Subject<Int> = PublishSubject.create()
    fun isEven(a: Int) = a.isEven()
    subject.map { isEven(it) }.subscribe { println("The number is ${(if (it) "Even" else "Odd")}") }

    subject.onNext(4)
    subject.onNext(9)

    println("---------------------------------------------")

    val sum = { x: Int, y: Int -> x + y }
    println("Sum ${sum(12, 14)}")
    val anonymousMul = { x: Int -> (Random.nextInt(15) + 1) * x }
    println("random output ${anonymousMul(2)}")

    println("---------------------------------------------")

    println("named pure func square = ${square(3)}")
    val qube = { n: Int -> n * n * n }
    println("lambda pure func qube = ${qube(3)}")

    println("---------------------------------------------")

    highOrderFun(12) { a: Int -> a.isEven() }
    highOrderFun(19) { a -> a.isEven() }

    println("---------------------------------------------")

    for (i in 1..10) {
        println("$i output ${doSomeStuff(i)}")
    }

    println("---------------------------------------------")

    runBlocking {
        val exeTime = longRunningTask()
        println("Execution time is $exeTime")
    }

    val time = GlobalScope.async {
        longRunningTask()
    }
    println("Print after async")
    runBlocking {
        println("printing time ${time.await()}")
    }

    println("---------------------------------------------")

    var a = 0
    var b = 1
    print("$a, ")
    print("$b, ")

    for (i in 2..9) {
        val c = a + b
        print("$c, ")
        a = b
        b = c
    }
    println()
    println("---------------------------------------------")
    val fibonacciSeries = sequence {
        var a = 0
        var b = 1
        yield(a)
        yield(b)

        while (true) {
            val c = a + b
            yield(c)
            a = b
            b = c
        }
    }

    println(fibonacciSeries.take(10).joinToString(", "))

    println("---------------------------------------------")

    val flowable = Flowable.range(1, 111)
    flowable.buffer(10, 15).subscribe {
        println("Subscription 1 $it")
    }
    flowable.buffer(15, 7).subscribe {
        println("Subscription 2 $it")
    }

    println("---------------------------------------------")

    val flowable2 = Flowable.interval(100, TimeUnit.MILLISECONDS)
    flowable2.buffer(1, TimeUnit.SECONDS).subscribe {
        println(it)
    }

    runBlocking {
        delay(5000)
    }

    println("---------------------------------------------")

    val flowable3 = Flowable.range(1, 111)
    flowable3.window(10).subscribe { flo ->
        flo.subscribe {
            print("$it, ")
        }
        println()
    }

    println("---------------------------------------------")

    val flowable4 = Flowable.interval(100, TimeUnit.MILLISECONDS)
    flowable4.throttleFirst(200, TimeUnit.MILLISECONDS).subscribe {
        println(it)
    }
    runBlocking { delay(1000) }

    println("=============================================")

    println("Initial output with a = 15, b = 10")
    val calculator = ReactiveCalculator(15, 10)

    println("Enter a = <number> or b = <number> in separate lines\nexit to exit the program")
    var line: String?
    do {
        line = readLine()
        GlobalScope.async {
            calculator.handleInput(line)
        }
    } while (line != null && !line.toLowerCase().contains("exit"))
}

fun square(n: Int): Int {
    return n * n
}

fun Int.isEven() = 0 == this and 1

inline fun highOrderFun(a: Int, validityCheckFun: (a: Int) -> Boolean) {
    if (validityCheckFun(a)) {
        println("a $a is Valid")
    } else {
        println("a $a is Invalid")
    }
}

fun doSomeStuff(a: Int = 0) = a + (a * a)

suspend fun longRunningTask(): Long {
    return measureTimeMillis {
        println("Please wait")
        delay(TimeUnit.SECONDS.toMillis(2))
        println("Delay Over")
    }
}