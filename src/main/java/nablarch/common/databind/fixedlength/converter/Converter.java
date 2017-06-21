package nablarch.common.databind.fixedlength.converter;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import nablarch.common.databind.fixedlength.FixedLengthDatBindConfig;

@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Converter {

    Class<? extends FieldConverter<?, ?>> converter();

    interface FieldConverter<C extends Annotation, T> {

        T convertOfRead(FixedLengthDatBindConfig datBindConfig, C converterConfig, byte[] input);

        byte[] convertOfWrite(FixedLengthDatBindConfig datBindConfig, C converterConfig, T output);
    }
}
