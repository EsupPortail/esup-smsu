package org.esupportail.smsu;

import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class OptionalTestSpringBeans {

	@Test
	public void testSpringBeans() {
		@SuppressWarnings({ "unused", "resource" })
		ClassPathXmlApplicationContext unused = new ClassPathXmlApplicationContext("properties/applicationContext.xml");
	}

}
