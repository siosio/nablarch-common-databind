package nablarch.common.databind.fixedlength;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import nablarch.common.databind.DataBindConfig;
import nablarch.common.databind.DataBindConfigConverter;
import nablarch.common.databind.DataBindUtil;
import nablarch.common.databind.fixedlength.FixedLengthDatBindConfig.FieldConverterHolder;
import nablarch.common.databind.fixedlength.FixedLengthDatBindConfig.FieldDefinition;
import nablarch.common.databind.fixedlength.converter.Converter;
import nablarch.common.databind.fixedlength.converter.Converter.FieldConverter;
import nablarch.core.beans.BeanUtil;

public class FixedLengthDataConfigConverter implements DataBindConfigConverter<FixedLength> {

    @Override
    public <BEAN> DataBindConfig create(final Class<BEAN> beanClass) {
        final FixedLength fixedLength = beanClass.getAnnotation(FixedLength.class);

        final FixedLengthDataBindConfigBuilder builder = FixedLengthDataBindConfigBuilder.newBuilder()
                .charset(Charset.forName(fixedLength.charset()))
                .lineSeparator(fixedLength.lineSeparator())
                .length(fixedLength.length())
                .multiLayout(fixedLength.multiLayout());

        if (fixedLength.multiLayout()) {
            createMultiLayout(builder, beanClass);
        } else {
            builder.addLayout(FieldDefinition.SINGLE_LAYOUT_NAME, createSingleLayout(beanClass));
        }
        return builder.build();
    }

    private <BEAN> void createMultiLayout(final FixedLengthDataBindConfigBuilder builder, final Class<BEAN> beanClass) {
        final PropertyDescriptor[] descriptors = BeanUtil.getPropertyDescriptors(beanClass);
        for (final PropertyDescriptor descriptor : descriptors) {
            final Method method = descriptor.getReadMethod();
            final Record record = method.getAnnotation(Record.class);
            if (record == null) {
                continue;
            }
            builder.addLayout(descriptor.getName(), createSingleLayout(method.getReturnType()));
        }
    }

    private <BEAN> List<FieldDefinition> createSingleLayout(final Class<BEAN> beanClass) {
        final PropertyDescriptor[] descriptors = BeanUtil.getPropertyDescriptors(beanClass);
        final List<FieldDefinition> fieldDefinitions = new ArrayList<FieldDefinition>(descriptors.length);
        for (final PropertyDescriptor descriptor : descriptors) {
            final Method method = descriptor.getReadMethod();
            final Field field = method.getAnnotation(Field.class);
            if (field == null) {
                continue;
            }

            FieldConverterHolder fieldConverterHolder = null;
            for (final Annotation annotation : method.getAnnotations()) {
                final Converter converter = annotation.annotationType().getAnnotation(Converter.class);
                if (converter != null) {
                    if (fieldConverterHolder != null) {
                        throw new IllegalStateException();
                    }
                    final Class<? extends FieldConverter<?, ?>> fieldConverter = converter.converter();
                    fieldConverterHolder = new FieldConverterHolder(annotation, DataBindUtil.getInstance(fieldConverter));
                }
            }
            fieldDefinitions.add(new FieldDefinition(descriptor.getName(), field.offset(), field.length(), fieldConverterHolder));
        }
        return fieldDefinitions;
    }

    @Override
    public Class<FixedLength> type() {
        return FixedLength.class;
    }
}
