package nablarch.common.databind.fixedlength;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.util.Arrays;

import nablarch.common.databind.ObjectMapper;
import nablarch.common.databind.fixedlength.converter.Converter.FieldConverter;

public abstract class FixedLengthObjectMapperSupport<T> implements ObjectMapper<T> {

    protected Line readLine(final ReadableByteChannel stream, final FixedLengthDatBindConfig config) {
        try {
            ByteBuffer line = ByteBuffer.allocate(config.getLength());
            final int readLength = stream.read(line);
            if (readLength == -1) {
                return null;
            }
            if (readLength != config.getLength()) {
                throw new IllegalStateException();
            }

            skipLineSeparator(stream, config);

            return new Line(line.array());
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    private void skipLineSeparator(final ReadableByteChannel stream, final FixedLengthDatBindConfig config) throws
            IOException {
        final ByteBuffer lineSeparator = ByteBuffer.allocate(config.getLineSeparator().length());
        final int lineSeparatorLength = stream.read(lineSeparator);

        if (lineSeparatorLength != -1) {
            if (lineSeparatorLength != config.getLineSeparator().length()) {
                throw new IllegalStateException();
            }

            if (!new String(lineSeparator.array(), config.getCharset()).equals(config.getLineSeparator())) {
                throw new IllegalStateException();
            }
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
