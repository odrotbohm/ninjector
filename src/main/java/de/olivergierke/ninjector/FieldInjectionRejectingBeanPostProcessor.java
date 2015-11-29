/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.olivergierke.ninjector;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationNotAllowedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;

/**
 * {@link BeanPostProcessor} to scan bean classes to be created for injection annotations on fields and rejecting the
 * instantiation of those bean types.
 * 
 * @author Oliver Gierke
 */
@SuppressWarnings("unchecked")
public class FieldInjectionRejectingBeanPostProcessor extends InstantiationAwareBeanPostProcessorAdapter {

	private static final Set<Class<? extends Annotation>> INJECTION_ANNOTATIONS;
	private static final String ERROR = "Field injection detected at field @%s %s of bean class %s! Use constructor injection instead!";

	static {

		Set<Class<? extends Annotation>> annotations = new HashSet<Class<? extends Annotation>>();

		annotations.add(Autowired.class);

		for (String annotationName : Arrays.asList("javax.inject.Inject", "javax.annotation.Resource"))

			try {
				annotations.add((Class<? extends Annotation>) ClassUtils.forName(annotationName,
						FieldInjectionRejectingBeanPostProcessor.class.getClassLoader()));
			} catch (ClassNotFoundException o_O) {}

		INJECTION_ANNOTATIONS = Collections.unmodifiableSet(annotations);
	}

	/* 
	 * (non-Javadoc)
	 * @see org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter#postProcessBeforeInstantiation(java.lang.Class, java.lang.String)
	 */
	@Override
	public Object postProcessBeforeInstantiation(final Class<?> beanClass, final String beanName) throws BeansException {

		ReflectionUtils.doWithFields(beanClass, new FieldCallback() {

			@Override
			public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {

				for (Class<? extends Annotation> annotationType : INJECTION_ANNOTATIONS) {
					if (AnnotationUtils.findAnnotation(field, annotationType) != null) {
						throw new BeanCreationNotAllowedException(beanName,
								String.format(ERROR, annotationType.getSimpleName(), field.getName(), beanClass.getSimpleName()));
					}
				}
			}
		});

		return null;
	}
}
