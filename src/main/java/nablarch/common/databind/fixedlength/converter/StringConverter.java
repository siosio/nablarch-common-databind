package nablarch.common.databind.fixedlength.converter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import nablarch.common.databind.fixedlength.FixedLengthDatBindConfig;
import nablarch.common.databind.fixedlength.converter.Converter.FieldConverter;
import nablarch.common.databind.fixedlength.converter.StringConverter.Impl;

/**
 * 文字列に変換することを示す。
 */
@Converter(converter = Impl.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface StringConverter {

    /** パッディング文字 */
    char paddingChar() default ' ';

    /** ファイルの文字コードを上書きする場合に指定する */
    String charset() default "";

    public class Impl implements FieldConverter<StringConverter, String> {

        public String convertOfRead(
                final FixedLengthDatBindConfig datBindConfig,
                final StringConverter converterConfig,
                final byte[] input) {
            final String string = new String(input, datBindConfig.getCharset());

            int i;
            for (i = string.length(); i > 0; i--) {
                if (string.charAt(i - 1) != converterConfig.paddingChar()) {
                    break;
                }
            }
            return string.substring(0, i);
        }

        @Override
        public byte[] convertOfWrite(final FixedLengthDatBindConfig datBindConfig,
                final StringConverter converterConfig,
                final String output) {
            return new byte[0];
        }
    }
}
