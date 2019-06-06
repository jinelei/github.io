package cn.jinelei.rainbow

import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val array = arrayOf("a", "b", "c")
        print(array.reduce { acc, s ->  "acc: {$acc} s: {$s} "})
        print("\n")
        print(array.reduce { acc, s ->  "$acc $s"})
    }
}
