package sample.spring3._16_webtest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.Test;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Spring MVC 의 Junit 테스트를 위해 AbstractDispatcherServletTest 를 구성.
 * AbstractDispatcherServletTest 는 내부적으로 ConfigurableDispatcherServlet 을 내장하고 있다.
 * 
 * AbstractDispatcherServletTest 는 AfterRunService 인터페이스를 구현하고 있으며 해당 기능 시그니처는 다음과같다.
 * <pre><code>
	String getContentAsString() throws UnsupportedEncodingException;
	WebApplicationContext getContext();
	<T> T getBean(Class<T> beanType);
	ModelAndView getModelAndView();
	AfterRunService assertViewName(String viewname);
	AfterRunService assertModel(String name, Object value);
	<code></pre>
 */
public class _03_SimpleHelloControllerTest extends AbstractDispatcherServletTest {
	@Test
	public void helloController() throws ServletException, IOException {
		ModelAndView mav = setRelativeLocations("spring-servlet.xml")
				.setClasses(HelloSpring.class)
				.initRequest("/hello", RequestMethod.GET).addParameter("name", "Spring")
				.runService()
				.getModelAndView();

		assertThat(mav.getViewName(), is("/WEB-INF/view/hello.jsp"));
		assertThat((String) mav.getModel().get("message"), is("Hello Spring"));
	}

	@Test
	public void helloControllerWithAssertMethods() throws ServletException, IOException {
		this.setRelativeLocations("spring-servlet.xml")
				.setClasses(HelloSpring.class)
				.initRequest("/hello", RequestMethod.GET).addParameter("name", "Spring")
				.runService()
				.assertModel("message", "Hello Spring")
				.assertViewName("/WEB-INF/view/hello.jsp");
	}

	@Test
	public void helloControllerWithServletPath() throws ServletException, IOException {
		this.setRelativeLocations("spring-servlet.xml")
				.setClasses(HelloSpring.class)
				.setServletPath("/app")
				.initRequest("/app/hello", RequestMethod.GET).addParameter("name", "Spring")
				.runService()
				.assertModel("message", "Hello Spring")
				.assertViewName("/WEB-INF/view/hello.jsp");
	}
}
