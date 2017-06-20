package nablarch.common.databind.fixedlength

import nablarch.common.databind.*
import org.hamcrest.*
import org.hamcrest.Matchers.*
import org.junit.*
import org.junit.Assert.*
import sun.nio.cs.ext.*
import java.io.*

class FixedLengthBeanMapperTest {

    @Test
    fun `単純なシングルレイアウトの固定長データが読み取れること`() {
        @FixedLength(length = 5, charset = "Windows-31J", lineSeparator = "\n")
        data class Single(
                @get:Field(offset = 1, length = 1)
                var id: Int? = null,
                @get:Field(offset = 2, length = 5)
                var name: String? = null
        ) {
            constructor() : this(null, null)
        }
        ObjectMapperFactory.create(Single::class.java, ByteArrayInputStream("1ああ\n2いい\n".toByteArray(MS932()))).use { sut ->
            assertThat(sut, instanceOf(FixedLengthBeanMapper::class.java))

            assertThat(sut.read(), `is`(Single(1, "ああ")))
            assertThat(sut.read(), `is`(Single(2, "いい")))
            assertThat(sut.read(), `is`(nullValue()))
        }
    }

    @Test
    fun `最終レコードに改行がなくても問題なく読み取れること`() {
        @FixedLength(length = 5, charset = "Windows-31J", lineSeparator = "\n")
        data class Single(
                @get:Field(offset = 1, length = 1)
                var id: Int? = null,
                @get:Field(offset = 2, length = 3)
                var name: String? = null,
                @get:Field(offset = 4, length = 5)
                var name2: String? = null
        ) {
            constructor() : this(null, null, null)
        }
        ObjectMapperFactory.create(Single::class.java, ByteArrayInputStream("1あい\n2かき".toByteArray(MS932()))).use { sut ->
            assertThat(sut, instanceOf(FixedLengthBeanMapper::class.java))

            assertThat(sut.read(), `is`(Single(1, "あ", "い")))
            assertThat(sut.read(), `is`(Single(2, "か", "き")))
            assertThat(sut.read(), `is`(nullValue()))
        }
    }
}
