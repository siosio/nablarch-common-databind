package nablarch.common.databind.fixedlength;

import java.beans.PropertyDescriptor;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import nablarch.common.databind.DataBindConfig;
import nablarch.common.databind.DataBindConfigCreator;
import nablarch.common.databind.fixedlength.FixedLengthDatBindConfig.Layout;
import nablarch.core.beans.BeanUtil;

public class FixedLengthDataConfigCreator implements DataBindConfigCreator<FixedLength>{

    @Override
    public <BEAN> DataBindConfig create(final Class<BEAN> beanClass) {
        final FixedLength fixedLength = beanClass.getAnnotation(FixedLength.class);

        final FixedLengthDataBindConfigBuilder builder = FixedLengthDataBindConfigBuilder.newBuilder()
                .charset(Charset.forName(fixedLength.charset()))
                .lineSeparator(fixedLength.lineSeparator())
                .length(fixedLength.length())
                .multiLayout(fixedLength.multiLayout());

        if (fixedLength.multiLayout()) {

        } else {
            final PropertyDescriptor[] descriptors = BeanUtil.getPropertyDescriptors(beanClass);
            final List<Layout> layouts = new ArrayList<Layout>(descriptors.length);
            for (final PropertyDescriptor descriptor : descriptors) {
                final Field field = descriptor.getReadMethod().getAnnotation(Field.class);
                if (field != null) {
                    layouts.add(new Layout(descriptor.getName(), field.offset(), field.length()));
                }
            }
            builder.addLayout(Layout.SINGLE_LAYOUT_NAME, layouts);
        }
        return builder.build();
    }

    @Override
    public Class<FixedLength> type() {
        return FixedLength.class;
    }
}
