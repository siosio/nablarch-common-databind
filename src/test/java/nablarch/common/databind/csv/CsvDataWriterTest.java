package nablarch.common.databind.csv;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.BufferedWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import nablarch.common.databind.DataWriter;
import nablarch.common.databind.csv.CsvDataBindConfig.QuoteMode;

import org.junit.After;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * {@link CsvDataWriter}のテストクラス。
 */
@RunWith(Parameterized.class)
public class CsvDataWriterTest {

    /** テストで使用するフォーマット定義 */
    private final CsvDataBindConfig config;

    /** テストで使用するフォーマッター */
    DataWriter<Object[]> sut;

    /** テストで使用するフィールドセパレータ */
    final String fieldSeparator;

    /** テストで使用するクォート文字 */
    final String quote;

    @Parameters
    public static List<CsvDataBindConfig[]> parameters() {
        return Arrays.asList(
                new CsvDataBindConfig[] {CsvDataBindConfig.DEFAULT},
                new CsvDataBindConfig[] {CsvDataBindConfig.RFC4180},
                new CsvDataBindConfig[] {CsvDataBindConfig.EXCEL},
                new CsvDataBindConfig[] {CsvDataBindConfig.TSV},
                new CsvDataBindConfig[] {CsvDataBindConfig.DEFAULT.withFieldSeparator('\r')},
                new CsvDataBindConfig[] {CsvDataBindConfig.RFC4180.withQuote('\'')},
                new CsvDataBindConfig[] {CsvDataBindConfig.TSV.withQuoteMode(QuoteMode.ALL)},
                new CsvDataBindConfig[] {CsvDataBindConfig.EXCEL.withQuoteMode(QuoteMode.NOT_NUMERIC)},
                new CsvDataBindConfig[] {CsvDataBindConfig.DEFAULT.withQuoteMode(QuoteMode.CUSTOM)
                        .withQuotedColumnNames("field1", "field3")},
                new CsvDataBindConfig[] {CsvDataBindConfig.DEFAULT.withQuoteMode(null)}
        );
    }

    @After
    public void tearDown() throws Exception {
        if (sut != null) {
            sut.close();
        }
    }

    public CsvDataWriterTest(final CsvDataBindConfig config) {
        System.out.println("user config: " + config);
        this.config = config;
        fieldSeparator = String.valueOf(config.getFieldSeparator());
        quote = String.valueOf(config.getQuote());
    }

    /**
     * 単一のフィールドの場合は、その値がそのままレコードとしてフォーマットされること。
     */
    @Test
    public void testSingleField() throws Exception {
        Assume.assumeThat(config.getQuoteMode(), is(QuoteMode.NORMAL));

        final StringWriter actual = new StringWriter();

        sut = new CsvDataWriter(new BufferedWriter(actual), config, new String[] {"col1"});
        sut.write(objects("12345"));
        sut.close();

        assertThat(actual.toString(), is("12345" + config.getLineSeparator()));
    }

    /**
     * 複数のフィールドが存在する場合は、フィールド区切り文字で各フィールドが連結されること。
     */
    @Test
    public void testMultiField() throws Exception {
        Assume.assumeThat(config.getQuoteMode(), is(QuoteMode.NORMAL));
        String[] param = {"12345", "54321"};

        final StringWriter actual = new StringWriter();
        sut = new CsvDataWriter(new BufferedWriter(actual), config, new String[] {"col1", "col2"});
        sut.write(param);
        sut.close();

        assertThat(actual.toString(), is(join(fieldSeparator, param) + config.getLineSeparator()));
    }

    /**
     * フィールド内にnullがあった場合は、空要素としてフォーマットされること
     */
    @Test
    public void testNullField() throws Exception {
        Assume.assumeThat(config.getQuoteMode(), is(QuoteMode.NORMAL));
        String[] param = {"12345", null, "54321"};

        final StringWriter actual = new StringWriter();
        sut = new CsvDataWriter(new BufferedWriter(actual), config, new String[] {"col1", "col2", "col3"});
        sut.write(param);
        sut.close();

        assertThat(actual.toString(), is(join(fieldSeparator, param) + config.getLineSeparator()));
    }

    /**
     * asciiの範囲外の文字でも問題なく扱えること。
     */
    @Test
    public void testNotAsciiCode() throws Exception {
        Assume.assumeThat(config.getQuoteMode(), is(QuoteMode.NORMAL));
        String[] param = {"12345", "あいうえお", "ｷﾀ─wﾍ√ﾚｖ～(ﾟ∀ﾟ)─wﾍ√ﾚｖ～─!!"};

        final StringWriter actual = new StringWriter();
        sut = new CsvDataWriter(new BufferedWriter(actual), config, new String[] {"col1", "col2", "col3"});
        sut.write(param);
        sut.close();

        assertThat(actual.toString(), is(join(fieldSeparator, param) + config.getLineSeparator()));
    }

    /**
     * 要素内にフィールド区切り文字が存在していた場合、そのフィールドが区切り文字で囲まれること。(カンマ区切り版)
     */
    @Test
    public void testContainsFieldSeparator_comma() throws Exception {
        Assume.assumeThat(config.getQuoteMode(), is(QuoteMode.NORMAL));
        Assume.assumeThat(fieldSeparator, is(","));

        String[] param = {"1,3", "456", "78,"};

        final StringWriter actual = new StringWriter();
        sut = new CsvDataWriter(new BufferedWriter(actual), config, new String[] {"col1", "col2", "col3"});
        sut.write(param);
        sut.close();

        if (quote.equals("\"")) {
            assertThat(actual.toString(), is(join(fieldSeparator, "\"1,3\"", "456", "\"78,\"")
                    + config.getLineSeparator()));
        } else if (quote.equals("'")) {
            assertThat(actual.toString(), is(join(fieldSeparator, "'1,3'", "456", "'78,'")
                    + config.getLineSeparator()));
        } else {
            throw new IllegalStateException("unsupported quote character. quote = " + quote);
        }
    }

    /**
     * 要素内にフィールド区切り文字が存在していた場合、そのフィールドが区切り文字で囲まれること。(タブ区切り版)
     */
    @Test
    public void testContainsFieldSeparator_tab() throws Exception {
        Assume.assumeThat(config.getQuoteMode(), is(QuoteMode.NORMAL));
        Assume.assumeThat(fieldSeparator, is("\t"));

        final StringWriter actual = new StringWriter();
        sut = new CsvDataWriter(new BufferedWriter(actual), config, new String[] {"col1", "col2", "col3"});
        sut.write(objects("1\t3", "456", "78\t"));
        sut.close();

        assertThat(actual.toString(), is(join(fieldSeparator, "\"1\t3\"", "456", "\"78\t\"")
                + config.getLineSeparator()));
    }

    /**
     * 要素内にフィールド区切り文字が存在していた場合、そのフィールドが区切り文字で囲まれること。(カスタム)
     */
    @Test
    public void testContainsFieldSeparator_custom() throws Exception {
        Assume.assumeThat(config.getQuoteMode(), is(QuoteMode.NORMAL));
        Assume.assumeThat(fieldSeparator, is("\r"));

        final StringWriter actual = new StringWriter();
        sut = new CsvDataWriter(new BufferedWriter(actual), config, new String[] {"col1", "col2", "col3"});
        sut.write(objects("1\r3", "456", "78\r"));
        sut.close();

        assertThat(actual.toString(), is(join(fieldSeparator, "\"1\r3\"", "456", "\"78\r\"")
                + config.getLineSeparator()));
    }

    /**
     * 要素内に改行コード(LF)が存在していた場合、そのフィールドが区切り文字で囲まれること
     */
    @Test
    public void testContainsLF() throws Exception {
        Assume.assumeThat(config.getQuoteMode(), is(QuoteMode.NORMAL));

        String[] params = {"あ\n", "このまま", "\nい"};
        final StringWriter actual = new StringWriter();
        sut = new CsvDataWriter(new BufferedWriter(actual), config, new String[] {"col1", "col2", "col3"});
        sut.write(params);
        sut.close();

        if (quote.equals("\"")) {
            assertThat(actual.toString(), is(join(fieldSeparator, "\"あ\n\"", "このまま", "\"\nい\"")
                    + config.getLineSeparator()));

        } else if (quote.equals("'")) {
            assertThat(actual.toString(), is(join(fieldSeparator, "'あ\n'", "このまま", "'\nい'")
                    + config.getLineSeparator()));
        } else {
            throw new IllegalStateException("unsupported quote character. quote = " + quote);
        }
    }

    /**
     * 要素内に改行コード(CR)が存在していた場合、そのフィールドが区切り文字で囲まれること
     */
    @Test
    public void testContainsCR() throws Exception {
        Assume.assumeThat(config.getQuoteMode(), is(QuoteMode.NORMAL));
        String[] params = {"あ\r", "このまま", "\rい"};
        final StringWriter actual = new StringWriter();
        sut = new CsvDataWriter(new BufferedWriter(actual), config, new String[] {"col1", "col2", "col3"});
        sut.write(params);
        sut.close();

        if (quote.equals("\"")) {
            assertThat(actual.toString(), is(join(fieldSeparator, "\"あ\r\"", "このまま", "\"\rい\"")
                    + config.getLineSeparator()));
        } else if (quote.equals("'")) {
            assertThat(actual.toString(), is(join(fieldSeparator, "'あ\r'", "このまま", "'\rい'")
                    + config.getLineSeparator()));
        } else {
            throw new IllegalStateException("unsupported quote character. quote = " + quote);
        }
    }

    /**
     * 要素内に改行コード(CRLF)が存在していた場合、そのフィールドが区切り文字で囲まれること
     */
    @Test
    public void testContainsCRLF() throws Exception {
        Assume.assumeThat(config.getQuoteMode(), is(QuoteMode.NORMAL));

        String[] params = {"あ\r\n", "このまま", "\r\nい"};

        final StringWriter actual = new StringWriter();
        sut = new CsvDataWriter(new BufferedWriter(actual), config, new String[] {"col1", "col2", "col3"});
        sut.write(params);
        sut.close();

        if (quote.equals("\"")) {
            assertThat(actual.toString(), is(join(fieldSeparator, "\"あ\r\n\"", "このまま", "\"\r\nい\"")
                    + config.getLineSeparator()));
        } else if (quote.equals("'")) {
            assertThat(actual.toString(), is(join(fieldSeparator, "'あ\r\n'", "このまま", "'\r\nい'")
                    + config.getLineSeparator()));
        } else {
            throw new IllegalStateException("unsupported quote character. quote = " + quote);
        }
    }

    /**
     * 要素内にクォート文字が存在していた場合、そのフィールドが区切り文字で囲まれ、クォートはクォートでエスケープされること。
     */
    @Test
    public void testContainsQuote() throws Exception {
        Assume.assumeThat(config.getQuoteMode(), is(QuoteMode.NORMAL));
        Assume.assumeThat(quote, is("\""));

        final StringWriter actual = new StringWriter();
        sut = new CsvDataWriter(new BufferedWriter(actual), config, new String[] {"col1", "col2", "col3"});
        sut.write(objects("あ\"い", "このまま", "\"い"));
        sut.close();
        assertThat(actual.toString(), is(join(fieldSeparator, "\"あ\"\"い\"", "このまま", "\"\"\"い\"")
                + config.getLineSeparator()));
    }

    /**
     * 空の要素が1つだけの場合でも変換がされること
     */
    @Test
    public void testEmptyFieldOnly() throws Exception {
        Assume.assumeThat(config.getQuoteMode(), is(QuoteMode.NORMAL));

        final StringWriter actual = new StringWriter();
        sut = new CsvDataWriter(new BufferedWriter(actual), config, new String[] {"col1"});
        sut.write(objects(""));
        sut.close();

        assertThat(actual.toString(), is(config.getLineSeparator()));
    }

    /**
     * 全ての要素が空の場合でも変換がされること
     */
    @Test
    public void testAllEmptyField() throws Exception {
        Assume.assumeThat(config.getQuoteMode(), is(QuoteMode.NORMAL));

        final StringWriter actual = new StringWriter();
        sut = new CsvDataWriter(new BufferedWriter(actual), config, new String[] {"col1", "col2", "col3", "col4"});
        sut.write(objects("", "", "", ""));
        sut.close();

        assertThat(actual.toString(), is(join(fieldSeparator, "", "", "", "") + config.getLineSeparator()));
    }

    /**
     * 空の要素があった場合でも変換がされること。
     */
    @Test
    public void testEmptyField() throws Exception {
        Assume.assumeThat(config.getQuoteMode(), is(QuoteMode.NORMAL));

        String[] param = {"1", "", "3"};
        final StringWriter actual = new StringWriter();
        sut = new CsvDataWriter(new BufferedWriter(actual), config, new String[] {"col1", "col2", "col3"});
        sut.write(param);
        sut.close();

        assertThat(actual.toString(), is(join(fieldSeparator, param) + config.getLineSeparator()));
    }

    /**
     * 要素に数値型(Numberのサブタイプ)が存在していても変換がされること。
     */
    @Test
    public void testNumericField() throws Exception {
        Assume.assumeThat(config.getQuoteMode(), is(QuoteMode.NORMAL));

        final StringWriter actual = new StringWriter();
        sut = new CsvDataWriter(new BufferedWriter(actual), config, new String[] {"col1", "col2", "col3", "col4"});
        sut.write(objects(1, 2L, 3.0D, BigDecimal.ONE));
        sut.close();

        assertThat(actual.toString(), is(join(fieldSeparator, "1", "2", "3.0", "1") + config.getLineSeparator()));
    }

    /**
     * 要素にBigDecimal型が存在していても指数表現にならずに変換されること
     * @throws Exception
     */
    @Test
    public void testBigDecimalField() throws Exception {
        Assume.assumeThat(config.getQuoteMode(), is(QuoteMode.NORMAL));

        final StringWriter actual = new StringWriter();
        sut = new CsvDataWriter(new BufferedWriter(actual), config, new String[] {"col1", "col2", "col3", "col4"});
        sut.write(objects(1, 2L, 3.0D, new BigDecimal("0.0000000001")));
        sut.close();

        assertThat(actual.toString(), is(join(fieldSeparator, "1", "2", "3.0", "0.0000000001") + config.getLineSeparator()));
    }

    /**
     * 列の値にnullを設定したい場合は、例外が送出されること。
     */
    @Test
    public void testSetColumnsNull() throws Exception {
        Assume.assumeThat(config.getQuoteMode(), is(QuoteMode.NORMAL));

        final StringWriter actual = new StringWriter();
        sut = new CsvDataWriter(new BufferedWriter(actual), config, new String[] {"col1", "col2", "col3", "col4"});
        try {
            sut.write(null);
            fail("例外が発生するはず");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("columns should not be empty and null."));
        }
    }

    /**
     * 列リストに空を設定した場合は、例外が送出されること。
     */
    @Test
    public void testSetEmptyColumns() throws Exception {
        Assume.assumeThat(config.getQuoteMode(), is(QuoteMode.NORMAL));

        final StringWriter actual = new StringWriter();
        sut = new CsvDataWriter(new BufferedWriter(actual), config, new String[] {"col1", "col2", "col3", "col4"});
        try {
            sut.write(new Object[0]);
            fail("例外が発生するはず");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("columns should not be empty and null."));
        }
    }

    /**
     * {@link QuoteMode#ALL}なので、すべての要素が囲み文字で囲まれること。
     */
    @Test
    public void testQuoteModeAll() throws Exception {
        Assume.assumeThat(config.getQuoteMode(), is(QuoteMode.ALL));

        Object[] param = {"12345", 1, 100L, BigDecimal.ONE};

        final StringWriter actual = new StringWriter();
        sut = new CsvDataWriter(new BufferedWriter(actual), config, new String[] {"col1", "col2", "col3", "col4"});
        sut.write(param);
        sut.close();

        if (quote.equals("\"")) {
            assertThat(actual.toString(),
                    is(join(fieldSeparator, "\"12345\"", "\"1\"", "\"100\"", "\"1\"") + config.getLineSeparator()));
        } else if (quote.equals("'")) {
            assertThat(actual.toString(),
                    is(join(fieldSeparator, "'12345'", "'1'", "'100'", "'1'") + config.getLineSeparator()));
        } else {
            throw new IllegalStateException("unsupported quote character. quote = " + quote);
        }
    }

    /**
     * {@link QuoteMode#NOT_NUMERIC}の場合、数値型(Number互換)以外は囲み文字で囲まれること。
     */
    @Test
    public void testQuoteModeNotNumeric() throws Exception {
        Assume.assumeThat(config.getQuoteMode(), is(QuoteMode.NOT_NUMERIC));

        Object[] param = {"12345", 1, 100L, BigDecimal.ONE, "\r", "あ"};

        final StringWriter actual = new StringWriter();
        sut = new CsvDataWriter(new BufferedWriter(actual), config,
                new String[] {"col1", "col2", "col3", "col4", "col5", "col6"});
        sut.write(param);
        sut.close();

        if (quote.equals("\"")) {
            assertThat(actual.toString(),
                    is(join(fieldSeparator, "\"12345\"", "1", "100", "1", "\"\r\"", "\"あ\"")
                            + config.getLineSeparator()));
        } else if (quote.equals("'")) {
            assertThat(actual.toString(),
                    is(join(fieldSeparator, "'12345'", "1", "100", "1", "'\r'", "'あ'") + config.getLineSeparator()));
        } else {
            throw new IllegalStateException("unsupported quote character. quote = " + quote);
        }

    }

    /**
     * {@link QuoteMode#CUSTOM}の場合、指定されたフィールドのみ囲み文字で囲まれること。
     */
    @Test
    public void testQuoteModeCustom() throws Exception {
        Assume.assumeThat(config.getQuoteMode(), is(QuoteMode.CUSTOM));

        Object[] param = {"12345", "aaa", null, BigDecimal.ONE};

        final StringWriter actual = new StringWriter();
        sut = new CsvDataWriter(new BufferedWriter(actual), config,
                new String[] {"field1", "field2", "field3", "field4"});
        sut.write(param);
        sut.close();

        if (quote.equals("\"")) {
            assertThat(actual.toString(),
                    is(join(fieldSeparator, "\"12345\"", "aaa", "\"\"", "1") + config.getLineSeparator()));
        } else if (quote.equals("'")) {
            assertThat(actual.toString(),
                    is(join(fieldSeparator, "'12345'", "aaa", "''", "1") + config.getLineSeparator()));
        } else {
            throw new IllegalStateException("unsupported quote character. quote = " + quote);
        }
    }

    /**
     * {@link QuoteMode}がnullの場合のケース
     */
    @Test
    public void testQuotedModeNull() throws Exception {
        Assume.assumeThat(config.getQuoteMode(), is(nullValue()));

        Object[] param = {"12345", "aaa", null, BigDecimal.ONE};

        final StringWriter actual = new StringWriter();
        sut = new CsvDataWriter(new BufferedWriter(actual), config,
                new String[] {"field1", "field2", "field3", "field4"});
        sut.write(param);
        sut.close();

        assertThat("囲み文字で囲まれないこと", actual.toString(), is("12345,aaa,,1\r\n"));
    }

    /**
     * Objectの配列を作るそれだけです。
     *
     * @param objects オブジェクトの可変引数
     * @return 引数が配列になったもの
     */
    private static Object[] objects(Object... objects) {
        return objects;
    }

    private String join(String separator, String... columns) {
        final StringBuilder sb = new StringBuilder();
        for (String column : columns) {
            sb.append(column == null ? "" : column);
            sb.append(separator);
        }
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }
}

