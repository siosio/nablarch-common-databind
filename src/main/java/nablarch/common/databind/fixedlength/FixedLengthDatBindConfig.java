package nablarch.common.databind.fixedlength;

import java.lang.annotation.Annotation;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import nablarch.common.databind.DataBindConfig;
import nablarch.common.databind.fixedlength.converter.Converter.FieldConverter;

/**
 * 固定長用のフォーマット定義を表すクラス。
 *
 * @author siosio
 */
public class FixedLengthDatBindConfig implements DataBindConfig {

    /** レコード長(バイト数) */
    private final int length;

    /** 文字コード */
    private final Charset charset;

    /** 改行コード */
    private final String lineSeparator;

    /** レイアウトを判定しレコード定義を持つBeanを返すクラス */
    private final boolean multiLayout;

    private final Map<String, List<FieldDefinition>> fieldDefinitions;

    /**
     * コンストラクタ。
     *
     * @param charset 文字コード
     * @param length レコード長(バイト数)
     * @param lineSeparator
     * @param multiLayout
     */
    FixedLengthDatBindConfig(
            final Charset charset,
            final int length,
            final String lineSeparator,
            final boolean multiLayout,
            final Map<String, List<FieldDefinition>> fieldDefinitions) {
        this.charset = charset;
        this.length = length;
        this.lineSeparator = lineSeparator;
        this.multiLayout = multiLayout;
        this.fieldDefinitions = Collections.unmodifiableMap(fieldDefinitions);
    }

    /**
     * 文字コードを返す。
     *
     * @return 文字コード
     */
    public Charset getCharset() {
        return charset;
    }

    /**
     * レコード長（バイト数）を返す。
     *
     * @return レコード長（バイト数）
     */
    public int getLength() {
        return length;
    }

    /**
     * 改行コードを返す。
     *
     * @return 改行コード
     */
    public String getLineSeparator() {
        return lineSeparator;
    }

    public boolean isMultiLayout() {
        return multiLayout;
    }

    public List<FieldDefinition> getFieldDefinitions() {
        return fieldDefinitions.get(FieldDefinition.SINGLE_LAYOUT_NAME);
    }

    public List<FieldDefinition> getFieldDefinitions(String recordName) {
        return fieldDefinitions.get(recordName);
    }

    /**
     * フィールドの定義
     */
    public static class FieldDefinition {

        public static final String SINGLE_LAYOUT_NAME = "single";

        private final String name;

        private final int offset;

        private final int length;

        private final FieldConverterHolder<?> fieldConverter;

        public FieldDefinition(final String name,
                final int offset,
                final int length,
                final FieldConverterHolder<?> fieldConverter) {
            this.name = name;
            this.offset = offset;
            this.length = length;
            this.fieldConverter = fieldConverter;
        }

        public String getName() {
            return name;
        }

        public int getOffset() {
            return offset;
        }

        public int getLength() {
            return length;
        }

        public FieldConverterHolder<?> getFieldConverter() {
            return fieldConverter;
        }
    }

    public static class FieldConverterHolder<C extends Annotation> {
        private final C annotation;

        private final FieldConverter<C, ?> fieldConverter;

        public FieldConverterHolder(final C annotation, final FieldConverter<C, ?> fieldConverter) {
            this.annotation = annotation;
            this.fieldConverter = fieldConverter;
        }

        public C getAnnotation() {
            return annotation;
        }

        public FieldConverter<C, ?> getFieldConverter() {
            return fieldConverter;
        }
    }
}
