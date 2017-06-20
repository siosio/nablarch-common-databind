package nablarch.common.databind.fixedlength;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import nablarch.common.databind.fixedlength.FixedLengthDatBindConfig.Layout;
import nablarch.core.beans.BeanUtil;

public class FixedLengthBeanMapper<T> extends FixedLengthObjectMapperSupport<T> {

    private final Class<T> bean;

    private final FixedLengthDatBindConfig config;

    private final InputStream stream;

    public FixedLengthBeanMapper(Class<T> bean, final FixedLengthDatBindConfig config, final InputStream stream) {
        this.bean = bean;
        this.config = config;
        this.stream = stream;
    }

    @Override
    public void write(final T object) {
        throw new UnsupportedOperationException("");
    }

    @Override
    public T read() {
        final byte[] line = readLine(stream, config);
        if (line == null) {
            return null;
        }
        final T bean;
        try {
            bean = this.bean.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (!config.isMultiLayout()) {
            final List<Layout> layouts = config.getLayout();
            for (final Layout layout : layouts) {
                final String name = layout.getName();
                final byte[] fieldData = Arrays.copyOfRange(line, layout.getOffset() - 1, layout.getLength());
                final String s = new String(fieldData, config.getCharset());
                BeanUtil.setProperty(bean, name, s);
            }
        }
        return bean;
    }

    @Override
    public void close() {

    }
}
