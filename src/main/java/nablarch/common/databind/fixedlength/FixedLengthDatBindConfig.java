package nablarch.common.databind.fixedlength;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nablarch.common.databind.DataBindConfig;

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

    private final Map<String, List<Layout>> layout;

    /**
     * コンストラクタ。
     *  @param charset 文字コード
     * @param length レコード長(バイト数)
     * @param lineSeparator
     * @param multiLayout
     */
    FixedLengthDatBindConfig(
            final Charset charset,
            final int length,
            final String lineSeparator,
            final boolean multiLayout,
            final Map<String, List<Layout>> layout) {
        this.charset = charset;
        this.length = length;
        this.lineSeparator = lineSeparator;
        this.multiLayout = multiLayout;
        this.layout = Collections.unmodifiableMap(layout);
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
     * @return 改行コード
     */
    public String getLineSeparator() {
        return lineSeparator;
    }

    public boolean isMultiLayout() {
        return multiLayout;
    }

    public List<Layout> getLayout() {
        return layout.get(Layout.SINGLE_LAYOUT_NAME);
    }

    /**
     * レコードのレイアウト定義
     */
    public static class Layout {

        public static final String SINGLE_LAYOUT_NAME = "single";

        private final String name;
        private final int offset;
        private final int length;

        public Layout(final String name, final int offset, final int length) {
            this.name = name;
            this.offset = offset;
            this.length = length;
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
    }
}
