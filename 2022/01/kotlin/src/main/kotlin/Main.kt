import java.math.BigInteger
import kotlin.system.measureTimeMillis
import kotlin.reflect.*

typealias Func<T, R> = (T) -> R

class RecursiveFunc<T, R>(val p: (RecursiveFunc<T, R>) -> Func<T, R>)

fun <T, R> y(f: (Func<T, R>) -> Func<T, R>): Func<T, R> {
    val rec = RecursiveFunc<T, R> { r -> f { r.p(r)(it) } }
    return rec.p(rec)
}

fun fibY(f: Func<Int, Long>) = { x: Int -> if (x <= 2) 1 else f(x - 1) + f(x - 2) }

@OptIn(ExperimentalStdlibApi::class)
fun main(args: Array<String>) {

    println("args = ${args.toList()}")

    data class Person(val name: String, val age: Int? = null)

    val listOfPerson = listOf(Person("철수", 29), Person("영희"))

    val oldestPerson = listOfPerson.maxByOrNull { it.age ?: 0 }

    println("oldest = $oldestPerson")

    println(
        "quickSort(12, 5, 15, 12, 8, 19) : ${
            measureTimeMillis {
                println(
                    quickSort(
                        listOf(
                            12,
                            5,
                            15,
                            12,
                            8,
                            19
                        )
                    )
                )
            }
        } ms"
    )

    println("factorialRec(5000) : ${measureTimeMillis { factorialRec(5000) }} ms")

    println("factorial(5000) : ${measureTimeMillis { factorial(5000) }} ms")

    println("factorialIterative(5000) : ${measureTimeMillis { factorialIterative(5000) }} ms")

    val deepFib = DeepRecursiveFunction<Int, Long> { n ->
        when(n) {
            0 -> 0L
            1, 2 -> 1L
            else -> callRecursive(n - 1) + callRecursive(n - 2)
        }
    }

    val deepFibTail = { x: Int ->
        data class XWrap(val x: Int, val a: Long, val b: Long)

        val inner = DeepRecursiveFunction<XWrap, Long> { w ->
            when (w.x) {
                0 -> w.a
                else -> callRecursive(XWrap(w.x - 1, w.b, w.a + w.b))
            }
        }
        inner(XWrap(x, 0, 1))
    }

    println("deepFib(40) : ${measureTimeMillis { println(deepFib(40)) }} ms")
    println("deepFibTail(500) : ${measureTimeMillis { println(deepFibTail(500)) }} ms")
    println("fibonacci(40) : ${measureTimeMillis { println(fibonacci(40)) }} ms")
    println("fibonacci(45) : ${measureTimeMillis { println(fibonacci(45)) }} ms")
    println("fibonacciTailRec(500) : ${measureTimeMillis { println(fibonacciTailRec(500)) }} ms")
    println(
        """fibY(40) : ${
            measureTimeMillis {
                for (i in 1..40) print("${y(::fibY)(i)} ")
                println()
            }
        } ms"""
    )

    fib = { n: Int ->
        when (n) {
            0 -> 0L
            1, 2 -> 1L
            else -> fib(n - 1) + fib(n - 2)
        }
    }.memoize()

    println("fib(40) : ${measureTimeMillis { fib(40) }} ms")
    println("fib(45) : ${measureTimeMillis { println(fib(45)) }} ms")
    println("fib(500) : ${measureTimeMillis { fib(500) }} ms")

    var ff: ((Int) -> Long) = { _ -> 0 }
    val fibD: (Int) -> Long by Memoize { n: Int ->
        when (n) {
            0 -> 0L
            1, 2 -> 1L
            else -> ff(n - 1) + ff(n - 2)
        }
    }
    ff = fibD

    println("fibD(40) : ${measureTimeMillis { fibD(40) }} ms")
    println("fibD(45) : ${measureTimeMillis { println(fibD(45)) }} ms")
    println("fibD(500) : ${measureTimeMillis { println(fibD(500)) }} ms")

    val fibIterMem: (Int) -> BigInteger by Memoize { n: Int ->
        var p: BigInteger
        var previousNumber = BigInteger("0")
        var currentNumber = BigInteger("1")
        for (i in 1..n) {
            p = previousNumber
            previousNumber = currentNumber
            currentNumber = p + previousNumber
        }
        currentNumber
    }

    println("fibIterMem(40) : ${measureTimeMillis { fibIterMem(40) }} ms")
    println("fibIterMem(45) : ${measureTimeMillis { println(fibIterMem(45)) }} ms")
    println("fibIterMem(50000) : ${measureTimeMillis { println(fibIterMem(50000)) }} ms")
    println("fibIterMem(50000) : ${measureTimeMillis { println(fibIterMem(50000)) }} ms")

    val fibTailMem: (Int) -> Long by Memoize { n: Int ->
        tailrec fun inner(i: Int, a: Long, b: Long): Long {
            return when (i) {
                0 -> a
                else -> {
                    inner(i - 1, b, a + b)
                }
            }
        }
        inner(n, 0, 1)
    }

    println("fibTailMem(40) : ${measureTimeMillis { fibTailMem(40) }} ms")
    println("fibTailMem(45) : ${measureTimeMillis { println(fibTailMem(45)) }} ms")
    println("fibTailMem(500) : ${measureTimeMillis { println(fibTailMem(500)) }} ms")
}

class Memoize<T, R>(val func: (T) -> R) {
    private val cache = mutableMapOf<T, R>()
    private val f = { n: T ->
        cache.getOrPut(n) { func(n) }
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>) = f
}

fun <T, R> ((T) -> R).memoize(): ((T) -> R) {
    val original = this
    val cache = mutableMapOf<T, R>()
    return { n: T -> cache.getOrPut(n) { original(n) } }
}

lateinit var fib: (Int) -> (Long)

fun fibonacci(n: Int): Long = when (n) {
    0 -> 0L
    1, 2 -> 1L
    else -> fibonacci(n - 1) + fibonacci(n - 2)
}

fun fibonacciTailRec(n: Int): Long {
    tailrec fun inner(x: Int, a: Long, b: Long): Long {
        return when (x) {
            0 -> a
            else -> inner(x - 1, b, a + b)
        }
    }
    return inner(n, 0, 1)
}

fun quickSort(numbers: List<Int>): List<Int> = if (numbers.isEmpty()) numbers else {
    val pivot = numbers.first()
    val tail = numbers.drop(1)
    val lessOrEqual = tail.filter { e -> e <= pivot }
    val larger = tail.filter { e -> e > pivot }
    quickSort(lessOrEqual) + pivot + quickSort(larger)
}

fun factorialRec(n: Int): BigInteger =
    if (n <= 0) 1.toBigInteger() else n.toBigInteger() * factorialRec(n - 1)

tailrec fun factorial(n: Int, result: BigInteger = 1.toBigInteger()): BigInteger =
    if (n <= 0) result else factorial(n - 1, n.toBigInteger() * result)

fun factorialIterative(n: Int): BigInteger =
    (1..n).fold(BigInteger("1")) { product, e -> product * e.toBigInteger() }