package nablarch.common.databind.fixedlength;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface FixedLength {
    int length();
    String charset();
    String lineSeparator();
    boolean multiLayout() default false;
}
