import org.junit.Assert.assertEquals
import org.junit.Test

class ExampleTest
{
    @Test
    fun additionIsCorrect()
    {
        assertEquals("Example unit test, to check if it is executed", 4, 2 + 2)
    }

    @Test
    fun failingTest() = assertEquals("This test will fail", 2, 4)
}