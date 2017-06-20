package nablarch.common.databind.fixedlength

import org.hamcrest.Matchers.*
import org.hamcrest.core.*
import org.junit.*
import org.junit.Assert.*

/**
 * [FixedLengthDatBindConfig]のテスト。
 */
class FixedLengthDatBindConfigTest {

    @Test
    fun builderTest() {

        val actual = FixedLengthDataBindConfigBuilder.newBuilder()
                .charset(charset("utf-8"))
                .length(1024)
                .lineSeparator("\n")
                .build()

        assertThat(actual.charset, `is`(charset("utf-8")))
        assertThat(actual.length, `is`(1024))
        assertThat(actual.isMultiLayout, `is`(false))
        assertThat(actual.lineSeparator, `is`("\n"))
    }
}
