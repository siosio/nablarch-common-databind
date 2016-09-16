package nablarch.common.databind;

import nablarch.core.util.annotation.Published;

import java.io.Closeable;

/**
 * データをリードするためのインタフェース。
 *
 * @param <T> 読み取ったデータ型
 * @author Hisaaki Shioiri
 */
@Published(tag = "architect")
public interface DataReader<T> extends Closeable {

    /**
     * データを読み取る。
     *
     * @return 読みっとったデータ。
     */
    T read();
}
