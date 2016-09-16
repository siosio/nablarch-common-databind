package nablarch.common.databind;

import nablarch.core.util.annotation.Published;

import java.io.Closeable;
import java.io.IOException;

/**
 * データを書き込むインタフェース。
 *
 * @param <T> 書き込むデータ型
 * @author Hisaaki Shioiri
 */
@Published(tag = "architect")
public interface DataWriter<T> extends Closeable {

    /**
     * データを書き込む。
     *
     * @param data データ
     * @throws IOException データ書き込みに失敗した場合
     */
    void write(T data) throws IOException;
}
