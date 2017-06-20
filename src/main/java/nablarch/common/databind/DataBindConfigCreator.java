package nablarch.common.databind;

import java.lang.annotation.Annotation;

public interface DataBindConfigCreator<T extends Annotation> {
    <BEAN> DataBindConfig create(Class<BEAN> beanClass);

    Class<T> type();
}

