package nablarch.common.databind.csv;

import nablarch.core.util.annotation.Published;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@link CsvFormat#quoteMode()}で{@link CsvDataBindConfig.QuoteMode#CUSTOM}を選択した場合に
 * 対象のフィールドを示すアノテーション。
 * <p/>
 * 対象のフィールドのgetterに本アノテーションを付与すること。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Published
public @interface Quoted {
}
