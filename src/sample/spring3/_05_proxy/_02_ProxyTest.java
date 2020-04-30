package sample.spring3._05_proxy;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Proxy;

import org.junit.Test;

public class _02_ProxyTest {
	@Test
	public void helloTest() {
		Hello hello = new HelloTarget();

		assertThat(hello.sayHello("Loggar"), is("Hello Loggar"));
		assertThat(hello.sayHi("Loggar"), is("Hi Loggar"));
		assertThat(hello.sayThanks("Loggar"), is("Thanks Loggar"));
	}

	/**
	 * Proxy class Test
	 */
	@Test
	public void helloUpperTest() {
		Hello hello = new HelloUpperProxy(new HelloTarget());

		assertThat(hello.sayHello("Loggar"), is("HELLO LOGGAR"));
		assertThat(hello.sayHi("Loggar"), is("HI LOGGAR"));
		assertThat(hello.sayThanks("Loggar"), is("THANKS LOGGAR"));
	}

	/**
	 * Dynamic Proxy Test
	 */
	@Test
	public void helloUpperProxyhandlerTest() {
		Hello hello = (Hello) Proxy.newProxyInstance(
				getClass().getClassLoader(),
				new Class[] { Hello.class },
				new HelloUpperProxyHandler(new HelloTarget()));

		assertThat(hello.sayHello("Loggar"), is("HELLO LOGGAR"));
		assertThat(hello.sayHi("Loggar"), is("HI LOGGAR"));
		assertThat(hello.sayThanks("Loggar"), is("THANKS LOGGAR"));
	}
}
