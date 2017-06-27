package nablarch.common.databind.fixedlength

import nablarch.common.databind.*
import nablarch.common.databind.fixedlength.converter.*
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
                @get:NumberStringConverter
                var id: Int? = null,

                @get:Field(offset = 2, length = 5)
                @get:StringConverter(paddingChar = '　')
                var name: String? = null
        ) {
            constructor() : this(null, null)
        }
        ObjectMapperFactory.create(Single::class.java, ByteArrayInputStream("1あ　\n2いい\n".toByteArray(MS932()))).use { sut ->
            assertThat(sut, instanceOf(FixedLengthBeanMapper::class.java))

            assertThat(sut.read(), `is`(Single(1, "あ")))
            assertThat(sut.read(), `is`(Single(2, "いい")))
            assertThat(sut.read(), `is`(nullValue()))
        }
    }

    enum class RecordType : MultiLayout.RecordName {
        HEADER {
            override fun getRecordName(): String = "header"
        },
        DATA {
            override fun getRecordName(): String = "data"
        };

    }

    @Test
    fun `マルチレイアウトなファイルを読むことができること`() {
        data class Header (
                @get:Field(offset = 1, length = 2)
                @get:NumberStringConverter
                var id: Int? = null,
                @get:Field(offset = 3, length = 4)
                @get:StringConverter
                var name: String? = null
        ) {
            constructor() : this(null, null)
        }

        data class Data(
                @get:Field(offset = 1, length = 1)
                @get:NumberStringConverter
                var id: Int? = null,
                @get:Field(offset = 2, length = 4)
                @get:StringConverter
                var data: String? = null
        ) {
            constructor() : this(null, null)
        }

        @FixedLength(length = 4, multiLayout = true, charset = "Windows-31J")
        class Multi : MultiLayout() {

            override fun getLayoutName(line: ByteArray): MultiLayout.RecordName {
                return if (line.first().toInt() == 0x31) {
                    RecordType.HEADER
                } else {
                    RecordType.DATA
                }

            }

            @get:Record
            var header: Header? = null
            @get:Record
            var data: Data? = null
        }

        ObjectMapperFactory.create(Multi::class.java, "12AB212311AA".toByteArray().inputStream()).use {
            assertThat(it, instanceOf(FixedLengthBeanMapper::class.java))
            val first = it.read()
            assertThat("最初のレコードはヘッダー", first.recordName, `is`<MultiLayout.RecordName>(RecordType.HEADER))
            assertThat("データレコードはnull", first.data, `is`(nullValue()))
            assertThat("ヘッダは最初のレコードの情報が入っている", first.header, allOf(
                    hasProperty("id", `is`(12)),
                    hasProperty("name", `is`("AB"))
            ))

            val second = it.read()
            assertThat("2番目のレコードはデータ", second.recordName, `is`<MultiLayout.RecordName>(RecordType.DATA))
            assertThat("ヘッダレコードはnull", second.header, `is`(nullValue()))
            assertThat("データにはレコードの情報が入っている", second.data, allOf(
                    hasProperty("id", `is`(2)),
                    hasProperty("data", `is`("123"))
            ))

            val third = it.read()
            assertThat("最後のレコードはヘッダー", third.recordName, `is`<MultiLayout.RecordName>(RecordType.HEADER))
            assertThat("データレコードはnull", third.data, `is`(nullValue()))
            assertThat("ヘッダは最初のレコードの情報が入っている", third.header, allOf(
                hasProperty("id", `is`(11)),
                hasProperty("name", `is`("AA"))
            ))
            
            assertThat("EOFになったのでnull", it.read(), `is`(nullValue()))
        }
    }

    @Test
    fun `最終レコードに改行がなくても問題なく読み取れること`() {
        @FixedLength(length = 5, charset = "Windows-31J", lineSeparator = "\n")
        data class Single(
                @get:Field(offset = 1, length = 1)
                @get:NumberStringConverter
                var id: Int? = null,
                @get:Field(offset = 2, length = 3)
                @get:StringConverter(paddingChar = '　')
                var name: String? = null,
                @get:Field(offset = 4, length = 5)
                @get:StringConverter(paddingChar = '　')
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
