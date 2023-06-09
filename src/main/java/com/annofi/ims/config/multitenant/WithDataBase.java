package com.annofi.ims.config.multitenant;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
//@Transactional
public @interface WithDataBase {

	//DataSourceType value() default DataSourceType.PRIMARY;
	String value() default "primary";
}
