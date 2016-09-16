package nablarch.common.databind;

import nablarch.core.util.annotation.Published;

import java.io.Closeable;

/**
 * Javaオブジェクトと任意のフォーマットをバインディングするインタフェース。
 *
 * @param <T> バインディング対象のJavaオブジェクトの型
 * @author Hisaaki Shioiri
 */
@Published(tag = "architect")
public interface ObjectMapper<T> extends Closeable {

    /**
     * オブジェクトの情報をアウトプットする。
     *
     * @param object オブジェクト
     */
    @Published
    void write(T object);

    /**
     * オブジェクトにマッピングする。
     *
     * @return オブジェクト
     */
    @Published
    T read();

    /**
     * リソースを開放する。
     */
    @Published
    void close();
}
