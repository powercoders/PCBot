package org.rsbot.script;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)

public @interface ScriptManifest {

	String[] authors();
	
	Script.Category category() default Script.Category.OTHER;

	String description() default "";

	String[] keywords() default {};

	String name();

	int requiresVersion() default 200;

	double version() default 1.0;

	String website() default "";

}
