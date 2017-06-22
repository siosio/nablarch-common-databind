package nablarch.common.databind;

import java.lang.annotation.Annotation;

public interface DataBindConfigConverter<T extends Annotation> {
    <BEAN> DataBindConfig create(Class<BEAN> beanClass);

    Class<T> type();
}

