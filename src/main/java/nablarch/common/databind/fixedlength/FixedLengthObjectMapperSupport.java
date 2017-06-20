package nablarch.common.databind.fixedlength;

import java.io.IOException;
import java.io.InputStream;

import nablarch.common.databind.ObjectMapper;

public abstract class FixedLengthObjectMapperSupport<T> implements ObjectMapper<T> {

    protected byte[] readLine(final InputStream stream, final FixedLengthDatBindConfig config) {
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

            return line;
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}
