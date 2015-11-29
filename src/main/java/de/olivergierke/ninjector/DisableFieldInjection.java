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

import de.olivergierke.ninjector.DisableFieldInjection.DisableFieldInjectionConfiguration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Disables field injection for Spring components. Will register infrastructure to reject the instantiation of Spring
 * beans that use injection annotations on fields.
 * 
 * @see http://olivergierke.de/2013/11/why-field-injection-is-evil/
 * @author Oliver Gierke
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(DisableFieldInjectionConfiguration.class)
public @interface DisableFieldInjection {

	@Configuration
	static class DisableFieldInjectionConfiguration {

		@Bean
		public static FieldInjectionRejectingBeanPostProcessor fieldInjectionRejectionBeanPostProcessor() {
			return new FieldInjectionRejectingBeanPostProcessor();
		}
	}
}
