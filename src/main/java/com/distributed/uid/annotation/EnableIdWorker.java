package com.distributed.uid.annotation;

import com.distributed.uid.CenterModel;
import com.distributed.uid.IdWorkerConfigurationSelector;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 开启发号器能力
 *
 * @author typhoon
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(IdWorkerConfigurationSelector.class)
public @interface EnableIdWorker {

    CenterModel mode() default CenterModel.DB;

}
