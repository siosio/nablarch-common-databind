package nablarch.common.databind.csv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;

import nablarch.common.databind.InvalidDataFormatException;

/**
 * CSVの各要素を分解するクラス
 *
 * @author Naoki Yamamoto
 */
class CsvTokenizer {

    /** 改行コード（CR） */
    private static final char CR = '\r';

    /** 改行コード（LF） */
    private static final char LF = '\n';

    /** 改行コード(CR)の文字列表記 */
    private static final String CR_STR = String.valueOf(CR);

    /** 改行コード(LF)の文字列表記 */
    private static final String LF_STR = String.valueOf(LF);

    /** 改行コード(CRLF) */
    private static final String CRLF = "\r\n";

    /** 解析対象CSVの{@link BufferedReader} */
    private final ExtendedReader reader;

    /** CSVの形式を表す{@link CsvDataBindConfig} */
    private final CsvDataBindConfig format;

    /** 次の要素が存在するか否か */
    private boolean hasNext = true;

    /**
     * コンストラクタ。
     *
     * @param reader 解析対象CSVの{@link BufferedReader}
     * @param format CSVの形式を表す{@link CsvDataBindConfig}
     */
    public CsvTokenizer(final BufferedReader reader, final CsvDataBindConfig format) {
        this.reader = new ExtendedReader(reader);
        this.format = format;
    }

    /**
     * 次の要素を取得する
     *
     * @return 要素
     * @throws IOException ファイルアクセスに失敗した場合
     */
    public String next() throws IOException {
        final int c = reader.read();
        if (isEndOfFile(c) || isEndOfLine(c)) {
            hasNext = false;
            return format.isEmptyToNull() ? null : "";
        } else if (isFieldSeparator(c)) {
            return format.isEmptyToNull() ? null : "";
        } else if (isQuote(c)) {
            return readQuotedItem();
        } else {
            return readItem((char) c);
        }
    }

    /**
     * ダブルクォートで囲まれた要素を取得する。
     *
     * @return 要素
     * @throws IOException ファイルアクセスに失敗した場合
     */
    private String readQuotedItem() throws IOException {
        final StringBuilder sb = new StringBuilder(256);
        final long startLine = reader.getLineNumber();
        while (true) {
            final int c = reader.read();
            if (isQuote(c)) {
                final int nextChar = reader.read();
                if (isQuote(nextChar)) {
                    sb.append((char) c);
                } else if (isEndOfLine(nextChar) || isEndOfFile(nextChar)) {
                    hasNext = false;
                    break;
                } else if (isFieldSeparator(nextChar)) {
                    break;
                } else {
                    // エスケープされていない単独のクォート文字はエラー
                    throw new InvalidDataFormatException("unescaped quote character.", reader.getLineNumber());
                }
            } else {
                if (isEndOfFile(c)) {
                    // クォートが閉じられないままファイルの終端に達した場合はエラー
                    throw new InvalidDataFormatException("EOF reached before quoted token finished.", startLine);
                }
                sb.append((char) c);
            }
        }
        return sb.toString();
    }

    /**
     * ダブルクォートで囲まれていない要素を取得する
     *
     * @param c 先頭の1文字
     * @return 要素
     * @throws IOException ファイルアクセスに失敗した場合
     */
    private String readItem(final char c) throws IOException {
        checkValidChar(c);

        final StringBuilder sb = new StringBuilder(256);
        sb.append(c);
        while (true) {
            final int nextChar = reader.read();
            if (isEndOfFile(nextChar) || isEndOfLine(nextChar)) {
                hasNext = false;
                break;
            } else if (isFieldSeparator(nextChar)) {
                break;
            }
            checkValidChar(nextChar);
            sb.append((char) nextChar);
        }
        return sb.toString();
    }

    /**
     * クォートで囲まれていない要素で許容される文字かどうかをチェックする。
     * <p/>
     * 許容されない文字が存在していた場合、例外を送出する。
     *
     * @param c 文字
     * @throws IOException ファイルアクセスに失敗した場合
     */
    private void checkValidChar(final int c) throws IOException {
        if (isQuote(c)) {
            throw new InvalidDataFormatException("invalid quote character.", reader.getLineNumber());
        } else {
            if (c == LF || c == CR) {
                throw new InvalidDataFormatException("invalid line separator.", reader.getLineNumber());
            }
        }
    }

    /**
     * 文字が{@link CsvDataBindConfig}で指定されたクォートか否かを判定する。
     *
     * @param c 文字
     * @return {@link CsvDataBindConfig}で指定されたクォートであればtrue
     */
    private boolean isQuote(final int c) {
        return c == format.getQuote();
    }

    /**
     * 文字が{@link CsvDataBindConfig}で指定された列区切り文字か否かを判定する。
     *
     * @param c 文字
     * @return {@link CsvDataBindConfig}で指定された列区切り文字であれば{@code true}
     */
    private boolean isFieldSeparator(final int c) {
        return c == format.getFieldSeparator();
    }

    /**
     * ファイルの終端に達したか否かを判定する。
     *
     * @param c 文字
     * @return ファイルの終端の場合は{@code true}
     */
    private boolean isEndOfFile(final int c) {
        return c == -1;
    }

    /**
     * ファイルの終端に達したか否か。
     *
     * @return ファイルの終端の場合は{@code true}
     * @throws IOException ファイルアクセスに失敗した場合
     */
    public boolean isEndOfFile() throws IOException {
        return reader.readNextCharAndReset() == -1;
    }

    /**
     * 指定された文字が行末を表しているか否かを判定する。
     * <p/>
     * {@link CsvDataBindConfig#getLineSeparator()} が1文字の場合は、指定された文字がその文字と一致している場合行末とする。
     * {@link CsvDataBindConfig#getLineSeparator()} が2文字の場合は、指定された文字と次の文字が、その文字列と一致している場合に行末とする。
     *
     * @param c 文字
     * @return 行末（改行コードの場合)はtrue
     * @throws IOException ファイルアクセスに失敗した場合
     */
    private boolean isEndOfLine(final int c) throws IOException {
        final String separator = format.getLineSeparator();
        if (c == LF) {
            return separator.equals(LF_STR);
        } else if (c == CR) {
            if (reader.readNextCharAndReset() == LF) {
                if (separator.equals(CRLF)) {
                    reader.read();
                    return true;
                }
            } else {
                return separator.equals(CR_STR);
            }
        }
        return false;
    }

    /**
     * 行の終端に達したか否か。
     *
     * @return 行の終端の場合は{@code true}
     */
    public boolean isEndOfLine() {
        return !hasNext;
    }

    /**
     * 新しいレコードを読むために状態をリセットする。
     */
    public void reset() {
        hasNext = true;
    }

    /**
     * 現在のレコード番号を返す。
     *
     * @return レコード番号
     * @throws IOException ファイルアクセスに失敗した場合
     */
    public long getLineNumber() {
        return reader.getLineNumber();
    }

    /**
     * ファイル内のレコード番号を管理するための{@link BufferedReader}拡張クラス。
     * <p/>
     * {@link java.io.LineNumberReader}を使用すれば、レコード番号を取得することができるが、
     * このクラスは改行文字を全てLF(\n)に変換してしまう。
     * このため、本クラスにて改行コードを変換せずにレコード番号を管理できる機能を実現する。
     * <p/>
     * 本機能は、以下のメソッドはサポートしない。
     * <ul>
     * <li>{@link #readLine()}</li>
     * <li>{@link #read(char[], int, int)}</li>
     * <li>{@link #read(char[])}</li>
     * <li>{@link #read(CharBuffer)}</li>
     * <li>{@link #skip(long)}</li>
     * </ul>
     */
    private static class ExtendedReader extends BufferedReader {

        /** レコード番号 */
        private long lineNumber = 1L;

        /** 最後に読み取った文字 */
        private int lastChar = -1;

        /**
         * レコード番号付きリーダを生成する。
         *
         * @param in {@link Reader}
         */
        public ExtendedReader(final Reader in) {
            super(in);
        }

        /**
         * 1文字分読み込む。
         * <p/>
         * 読み込んだ文字が改行コードの場合、レコード番号をインクリメントする。
         */
        @Override
        public int read() throws IOException {
            final int read = super.read();
            if (isLineSeparator(read)) {
                lineNumber++;
            }
            if (read != -1) {
                lastChar = read;
            }
            return read;
        }

        /**
         * 指定された文字が改行文字か否かを判定する。
         *
         * @param c 文字
         * @return 改行文字の場合{@code true}
         * @throws IOException ファイルアクセスに失敗した場合
         */
        private boolean isLineSeparator(final int c) throws IOException {
            if (c == LF) {
                return true;
            } else if (c == CR && readNextCharAndReset() != LF) {
                return true;
            }
            return false;
        }

        /**
         * サポートしません。
         */
        @Override
        public int read(char[] cbuf, int off, int len) throws IOException {
            throw new UnsupportedOperationException("unsupported.");
        }

        /**
         * サポートしません。
         */
        @Override
        public int read(CharBuffer target) throws IOException {
            throw new UnsupportedOperationException("unsupported.");
        }

        /**
         * サポートしません。
         */
        @Override
        public int read(char[] cbuf) throws IOException {
            throw new UnsupportedOperationException("unsupported.");
        }

        /**
         * サポートしません。
         */
        @Override
        public String readLine() throws IOException {
            throw new UnsupportedOperationException("unsupported.");
        }

        /**
         * サポートしません。
         */
        @Override
        public long skip(long n) throws IOException {
            throw new UnsupportedOperationException("unsupported.");
        }

        /**
         * カレントポジションを移動せずに次の一文字をリードする。
         *
         * @return 読み込んだ文字
         * @throws IOException ファイルアクセスに失敗した場合
         */
        private int readNextCharAndReset() throws IOException {
            mark(1);
            final int c = super.read();
            reset();
            return c;
        }

        /**
         * 現在のレコード番号を返す。
         *
         * @return レコード番号
         */
        public long getLineNumber() {
            try {
                return isLineSeparator(lastChar) ? lineNumber - 1 : lineNumber;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
