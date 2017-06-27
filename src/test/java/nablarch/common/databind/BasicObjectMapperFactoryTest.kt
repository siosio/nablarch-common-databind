package nablarch.common.databind

import nablarch.common.databind.ObjectMapperFactoryTest.*
import nablarch.common.databind.csv.*
import nablarch.core.repository.*
import org.hamcrest.Matchers.*
import org.junit.*
import org.junit.Assert.*
import org.junit.rules.*
import java.io.*
import java.util.*


class BasicObjectMapperFactoryTest {

    @get:Rule
    val expectedException: ExpectedException = ExpectedException.none()

    @Test
    fun `createMapper_InputStreamで想定外のMapperタイプの場合例外が送出されること`() {
        val sut = object : BasicObjectMapperFactory() {
            override fun toMapperType(clazz: Class<*>?, dataBindConfig: DataBindConfig?): MapperType? {
                return null
            }
        }

        @Csv(type = Csv.CsvType.RFC4180, properties = arrayOf("dummy"))
        class Dummy

        expectedException.expect(IllegalArgumentException::class.java)
        expectedException.expectMessage("Unsupported config or class")
        sut.createMapper(Dummy::class.java, ByteArrayInputStream(ByteArray(0)))
    }

    @Test
    fun `createMapper_config_InputStreamで想定外のMapperタイプの場合例外が送出されること`() {
        val sut = object : BasicObjectMapperFactory() {
            override fun toMapperType(clazz: Class<*>?, dataBindConfig: DataBindConfig?): MapperType? {
                return null
            }
        }

        expectedException.expect(IllegalArgumentException::class.java)
        expectedException.expectMessage("Unsupported config or class")
        sut.createMapper(Map::class.java, ByteArrayInputStream(byteArrayOf()), CsvDataBindConfig.DEFAULT)
    }

    @Test
    fun `createMapper_Readerで想定外のMapperタイプの場合例外が送出されること`() {
        val sut = object : BasicObjectMapperFactory() {
            override fun toMapperType(clazz: Class<*>?, dataBindConfig: DataBindConfig?): MapperType? {
                return null
            }
        }

        @Csv(type = Csv.CsvType.RFC4180, properties = arrayOf("dummy"))
        class Dummy

        expectedException.expect(IllegalArgumentException::class.java)
        expectedException.expectMessage("Unsupported config or class")
        sut.createMapper(Dummy::class.java, StringReader(""))
    }

    @Test
    fun `createMapper_config_Readerで想定外のMapperタイプの場合例外が送出されること`() {
        val sut = object : BasicObjectMapperFactory() {
            override fun toMapperType(clazz: Class<*>?, dataBindConfig: DataBindConfig?): MapperType? {
                return null
            }
        }

        expectedException.expect(IllegalArgumentException::class.java)
        expectedException.expectMessage("Unsupported config or class")
        sut.createMapper(Any::class.java, StringReader(""), CsvDataBindConfig.DEFAULT)
    }

    @Test
    fun `createMapper_OutputStreamで想定外のMapperタイプの場合例外が送出されること`() {
        val sut = object : BasicObjectMapperFactory() {
            override fun toMapperType(clazz: Class<*>?, dataBindConfig: DataBindConfig?): MapperType? {
                return null
            }
        }

        @Csv(type = Csv.CsvType.RFC4180, properties = arrayOf("dummy"))
        class Dummy

        expectedException.expect(IllegalArgumentException::class.java)
        expectedException.expectMessage("Unsupported config or class")
        sut.createMapper(Dummy::class.java, ByteArrayOutputStream())
    }

    /**
     * [ObjectMapperFactory.create]の例外ケース
     */
    @Test
    fun `createMapper_Writerで想定外のMapperタイプの場合例外が送出されること`() {
        val sut = object : BasicObjectMapperFactory() {
            override fun toMapperType(clazz: Class<*>?, dataBindConfig: DataBindConfig?): MapperType? {
                return null
            }
        }

        @Csv(type = Csv.CsvType.RFC4180, properties = arrayOf("dummy"))
        class Dummy

        expectedException.expect(IllegalArgumentException::class.java)
        expectedException.expectMessage("Unsupported config or class")
        sut.createMapper(Dummy::class.java, StringWriter())
    }


    /**
     * [ObjectMapperFactory.create]の例外ケース
     */
    @Test
    @Throws(Exception::class)
    fun `createMapper_config_OutputStreamで想定外のMapperタイプの場合例外が送出されること`() {
        val sut = object : BasicObjectMapperFactory() {
            override fun toMapperType(clazz: Class<*>?, dataBindConfig: DataBindConfig?): MapperType? {
                return null
            }
        }
        expectedException.expect(IllegalArgumentException::class.java)
        expectedException.expectMessage("Unsupported config or class")
        sut.createMapper(Map::class.java, ByteArrayOutputStream(), CsvDataBindConfig.DEFAULT)
    }

    @Test
    fun `createMapper_config_writerで想定外のMapperタイプの場合例外が送出されること`() {
        val sut = object : BasicObjectMapperFactory() {
            override fun toMapperType(clazz: Class<*>?, dataBindConfig: DataBindConfig?): MapperType? {
                return null
            }
        }

        expectedException.expect(IllegalArgumentException::class.java)
        expectedException.expectMessage("Unsupported config or class")
        sut.createMapper(Map::class.java, StringWriter(), CsvDataBindConfig.DEFAULT)

    }

    @Test
    fun crateFactory() {

        class DummyMapper<T> : ObjectMapper<T> {
            override fun write(`object`: T) {
            }

            override fun read(): T? {
                return null
            }

            override fun close() {
            }
        }

        SystemRepository.load {
            val objects = HashMap<String, Any>()
            objects.put("objectMapperFactory", object : BasicObjectMapperFactory() {
                override fun <T> createMapper(clazz: Class<T>, stream: InputStream): ObjectMapper<T> {
                    return DummyMapper<T>()
                }
            })
            objects
        }

        assertThat("input:InputStream",
                ObjectMapperFactory.create(CsvBean::class.java, ByteArrayInputStream(ByteArray(0))),
                instanceOf(DummyMapper::class.java))

    }

    @Test
    fun createMapper_InputStream_Beanとしてデータが読み取れること() {
        @Csv(type = Csv.CsvType.RFC4180, properties = arrayOf("id", "name"))
        data class Bean(
            var id:Int? = null,
            var name:String? = null
        ) {
            constructor() : this(null, null)
        }

        val sut = BasicObjectMapperFactory()
        val beans = sut.createMapper(Bean::class.java, ByteArrayInputStream("1,name\r\n2,name2".toByteArray())).use {
            generateSequence {
                it.read()
            }.toList()
        }

        assertThat(beans, hasSize(2))
        assertThat(beans[0], `is`(Bean(1, "name")))
        assertThat(beans[1], `is`(Bean(2, "name2")))
    }
}
