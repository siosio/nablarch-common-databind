package nablarch.common.databind.fixedlength.converter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import nablarch.common.databind.fixedlength.FixedLengthDatBindConfig;

@Converter(converter = NumberStringConverter.Impl.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface NumberStringConverter {

    char paddingChar() default '0';

    class Impl implements Converter.FieldConverter<NumberStringConverter, String> {

        public String convertOfRead(
                final FixedLengthDatBindConfig datBindConfig,
                final NumberStringConverter converterConfig,
                final byte[] input) {

            final String string = new String(input, datBindConfig.getCharset());

            int i;
            for (i = 0; i < string.length(); i++) {
                if (string.charAt(i) != converterConfig.paddingChar()) {
                    break;
                }
            }
            return string.substring(i);
        }

        @Override
        public byte[] convertOfWrite(final FixedLengthDatBindConfig datBindConfig,
                final NumberStringConverter converterConfig,
                final String output) {
            return new byte[0];
        }
    }
}
