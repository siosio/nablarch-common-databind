package nablarch.common.databind;

import nablarch.core.util.annotation.Published;

/**
 * データのフォーマット不正により解析に失敗した場合に送出される例外クラス。
 *
 * @author Naoki Yamamoto
 */
@Published
public class InvalidDataFormatException extends RuntimeException {

    /** メッセージ */
    private static final String MESSAGE = "data format is invalid. ";

    /** エラー発生レコード番号 */
    private final long lineNumber;

    /**
     * 指定された行番号とメッセージを持つ{@code InvalidCsvFormatException}を生成する。
     *
     * @param message メッセージ
     * @param lineNumber 行番号
     */
    public InvalidDataFormatException(final String message, final long lineNumber) {
        super(MESSAGE + message + " line number = [" + lineNumber + ']');
        this.lineNumber = lineNumber;
    }

    /**
     * エラー発生レコード番号を取得する。
     * @return エラー発生レコード番号
     */
    public long getLineNumber() {
        return lineNumber;
    }
}
