package de.olivergierke.ninjector;

import org.junit.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * Integration tests for {@link DisableFieldInjection}.
 *
 * @author Oliver Gierke
 */
public class NinjectorApplicationTest {

	@Configuration
	@ComponentScan
	@DisableFieldInjection
	static class Config {}

	@SuppressWarnings("resource")
	@Test(expected = BeanCreationException.class)
	public void rejectsFieldInjected() {
		new AnnotationConfigApplicationContext(Config.class);
	}

	@Component
	static class MyComponent {
		@Autowired MyOtherComponent myOtherComponent;
	}

	@Component
	static class MyOtherComponent {}
}
