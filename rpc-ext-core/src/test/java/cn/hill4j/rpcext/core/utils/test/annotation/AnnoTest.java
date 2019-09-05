package cn.hill4j.rpcext.core.utils.test.annotation;

import javax.annotation.Resource;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 〈一句话功能简述〉<br>
 * Description: 测试注解
 *
 * @author hillchen
 * @create 2019/8/25 11:55
 */
@Target({PACKAGE, TYPE, FIELD, METHOD})
@Retention(RUNTIME)
public @interface AnnoTest {
    int value() default 0;
}
