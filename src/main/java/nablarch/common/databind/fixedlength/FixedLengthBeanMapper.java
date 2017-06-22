package nablarch.common.databind.fixedlength;

import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.List;

import nablarch.common.databind.DataBindUtil;
import nablarch.common.databind.fixedlength.FixedLengthDatBindConfig.Layout;
import nablarch.core.beans.BeanUtil;
import nablarch.core.util.FileUtil;

public class FixedLengthBeanMapper<T> extends FixedLengthObjectMapperSupport<T> {

    private final Class<T> beanClass;

    private final FixedLengthDatBindConfig config;

    private final ReadableByteChannel stream;

    public FixedLengthBeanMapper(Class<T> beanClass, final FixedLengthDatBindConfig config, final InputStream stream) {
        this.beanClass = beanClass;
        this.config = config;
        this.stream = Channels.newChannel(stream);
    }

    @Override
    public void write(final T object) {
        throw new UnsupportedOperationException("");
    }

    @Override
    public T read() {

        final Line line = readLine(stream, config);
        if (line == null) {
            return null;
        }
        final T bean = DataBindUtil.getInstance(this.beanClass);

        if (!config.isMultiLayout()) {
            final List<Layout> layouts = config.getLayout();
            for (final Layout layout : layouts) {
                final String name = layout.getName();
                final Object fieldValue = line.readField(config, layout);
                BeanUtil.setProperty(bean, name, fieldValue);
            }
        }
        return bean;
    }

    @Override
    public void close() {
        FileUtil.closeQuietly(stream);
    }
}
