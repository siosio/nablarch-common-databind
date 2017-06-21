package nablarch.common.databind.fixedlength;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.util.Arrays;

import nablarch.common.databind.ObjectMapper;
import nablarch.common.databind.fixedlength.converter.Converter;
import nablarch.common.databind.fixedlength.converter.Converter.FieldConverter;

public abstract class FixedLengthObjectMapperSupport<T> implements ObjectMapper<T> {

    protected Line readLine(final InputStream stream, final FixedLengthDatBindConfig config) {
        try {
            final byte[] line = new byte[config.getLength()];
            final int readLength = stream.read(line);
            if (readLength == -1) {
                return null;
            }
            if (readLength != line.length) {
                throw new IllegalStateException();
            }

            final byte[] lineSeparator = new byte[config.getLineSeparator().length()];
            final int lineSeparatorLength = stream.read(lineSeparator);

            if (lineSeparatorLength != -1) {
                if (lineSeparatorLength != lineSeparator.length) {
                    throw new IllegalStateException();
                }

                if (!new String(lineSeparator, config.getCharset()).equals(config.getLineSeparator())) {
                    throw new IllegalStateException();
                }
            }

            return new Line(line);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    public static class Line {
        private final byte[] line;

        public Line(final byte[] line) {
            this.line = line;
        }

        @SuppressWarnings("unchecked")
        public <T> T readField(final FixedLengthDatBindConfig config, final FixedLengthDatBindConfig.Layout layout) {
            final byte[] fieldData = Arrays.copyOfRange(line, layout.getOffset() - 1, layout.getLength());
            final FixedLengthDatBindConfig.FieldConverterHolder<?> converter = layout.getFieldConverter();
            if (converter == null) {
                return (T) fieldData;
            } else {
                final FieldConverter fieldConverter = converter.getFieldConverter();
                final Annotation annotation = converter.getAnnotation();
                return (T) fieldConverter.convertOfRead(config, annotation, fieldData);
            }
        }
    }
}
