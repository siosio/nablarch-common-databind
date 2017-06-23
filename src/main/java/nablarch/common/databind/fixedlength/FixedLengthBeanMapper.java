package nablarch.common.databind.fixedlength;

import java.beans.PropertyDescriptor;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.List;

import nablarch.common.databind.DataBindUtil;
import nablarch.common.databind.fixedlength.FixedLengthDatBindConfig.FieldDefinition;
import nablarch.common.databind.fixedlength.MultiLayout.RecordName;
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

        if (config.isMultiLayout()) {
            final RecordName recordName = ((MultiLayout) bean).getLayoutName(line.getLine());
            BeanUtil.setProperty(bean, "recordName", recordName);
            final PropertyDescriptor descriptor = BeanUtil.getPropertyDescriptor(beanClass, recordName.getRecordName());
            final Object record = DataBindUtil.getInstance(descriptor.getPropertyType());
            setFileValue(line, record, recordName.getRecordName());
            BeanUtil.setProperty(bean, recordName.getRecordName(), record);
            return bean;
        } else {
            setFileValue(line, bean, FixedLengthDatBindConfig.RecordDefinition.SINGLE_LAYOUT_NAME);
        }
        return bean;
    }

    private void setFileValue(final Line line, final Object bean, final String recordName) {
        final FixedLengthDatBindConfig.RecordDefinition recordDefinition = config.getRecordDefinition(recordName);
        for (final FieldDefinition fieldDefinition : recordDefinition) {
            final String name = fieldDefinition.getName();
            final Object fieldValue = line.readField(config, fieldDefinition);
            BeanUtil.setProperty(bean, name, fieldValue);
        }
    }

    @Override
    public void close() {
        FileUtil.closeQuietly(stream);
    }
}
