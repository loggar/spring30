package sample.spring3._05_proxy;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.lang.reflect.InvocationTargetException;

import org.junit.Test;

/**
 * reflection test
 * getMethod, invoke
 * 
 */
public class _01_ReflectionTest {
	@Test
	public void invokeMethod() throws IllegalArgumentException, SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		String name = "Spring";

		assertThat(name.length(), is(6));
		assertThat((Integer) String.class.getMethod("length").invoke(name), is(6));

		assertThat(name.charAt(0), is('S'));
		assertThat((Character) String.class.getMethod("charAt", int.class).invoke(name, 0), is('S'));
	}
}
