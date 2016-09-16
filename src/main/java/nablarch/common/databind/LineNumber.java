package nablarch.common.databind;

import nablarch.core.util.annotation.Published;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ファイル行数取得対象であることを示すアノテーション。
 *
 * @author Kumiko Omi
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Published
public @interface LineNumber {
}

