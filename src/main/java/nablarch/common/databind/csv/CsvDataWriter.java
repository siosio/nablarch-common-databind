package nablarch.common.databind.csv;

import java.io.BufferedWriter;
import java.io.IOException;

import nablarch.common.databind.DataWriter;
import nablarch.common.databind.csv.CsvDataBindConfig.QuoteMode;
import nablarch.core.util.StringUtil;

/**
 * CSVを出力するクラス。
 *
 * @author Hisaaki Shioiri
 */
public class CsvDataWriter implements DataWriter<Object[]> {

    /** フォーマット定義 */
    private final CsvDataBindConfig config;

    /** 出力先のリソース */
    private final BufferedWriter writer;

    /** プロパティ名リスト */
    private final String[] properties;


    /**
     * 指定されたフォーマット定義を持つ{@code CsvLineFormatter}を生成する。
     *
     * @param writer 出力リソース
     * @param config フォーマット定義
     * @param properties プロパティ名リスト
     */
    public CsvDataWriter(final BufferedWriter writer, final CsvDataBindConfig config, final String[] properties) {
        this.config = config;
        this.writer = writer;
        this.properties = properties;
    }

    /**
     * nullを空文字列に変換する。
     *
     * @param column 値
     * @return nullを空文字列に変換したもの
     */
    private static Object nullToEmpty(final Object column) {
        return column == null ? "" : column;
    }

    /**
     * フィールドのフォーマットを行う。
     *
     * @param fieldName フィールド名
     * @param fieldValue フィールドの値
     * @throws IOException 書き込み例外
     */
    private void writeField(final String fieldName, final Object fieldValue) throws IOException {
        final String fieldStr = StringUtil.toString(fieldValue);

        final char quote = config.getQuote();
        final boolean quotedField = isQuotedField(fieldName, fieldValue);

        if (quotedField) {
            writer.write((int) quote);
        }
        for (int i = 0; i < fieldStr.length(); i++) {
            final char c = fieldStr.charAt(i);
            writer.write((int) c);
            if (c == quote) {
                writer.write((int) c);
            }
        }
        if (quotedField) {
            writer.write((int) quote);
        }
    }

    /**
     * クォート文字でフィールドを囲む必要があるかどうか。
     *
     * @param fieldName フィールド名
     * @param fieldValue フィールドの値
     * @return 囲む必要がある場合はtrue
     */
    private boolean isQuotedField(final String fieldName, final Object fieldValue) {
        final String fieldStr = fieldValue.toString();
        if (config.getQuoteMode() == QuoteMode.ALL) {
            return true;
        } else if (config.getQuoteMode() == QuoteMode.NOT_NUMERIC) {
            return !Number.class.isInstance(fieldValue);
        } else if (config.getQuoteMode() == QuoteMode.CUSTOM) {
            return config.getQuotedColumnNames()
                    .contains(fieldName);
        } else if (config.getQuoteMode() == QuoteMode.NORMAL) {
            return hasEscapedChar(fieldStr);
        }
        return false;
    }

    /**
     * エスケープが必要な文字を持っているかどうか。
     *
     * @param value 文字列
     * @return エスケープが必要な場合は{@code true}
     */
    private boolean hasEscapedChar(final String value) {
        for (char c : value.toCharArray()) {
            if (c == '\r'
                    || c == '\n'
                    || c == config.getQuote()
                    || c == config.getFieldSeparator()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void write(final Object[] data) throws IOException {
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("columns should not be empty and null.");
        }

        for (int i = 0; i < data.length; i++) {
            if (i != 0) {
                writer.write((int) config.getFieldSeparator());
            }
            final Object column = data[i];
            writeField(properties[i], nullToEmpty(column));
        }
        writer.write(config.getLineSeparator());
    }

    @Override
    public void close() throws IOException {
        writer.close();
    }
}
