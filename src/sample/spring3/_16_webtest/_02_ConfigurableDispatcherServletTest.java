package sample.spring3._16_webtest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.web.servlet.ModelAndView;

/**
 * DispatcherServlet 을 테스트 용도로 확장한 ConfigurableDispatcherServlet 을 구성
 * setRelativeLocations() 으로 applicationContext 들을 지정할 수 있고 setClasses() 로 직접 bean 을 구성할 수도 있다.
 * service(req, res) 를 수행 후 ModelAndView 가져와 테스트 할 수 있다.
 * 사용전 반드시 init() 을 수행 해야 한다.
 * 
 * 현재 버전의 ConfigurableDispatcherServlet 에는 ROOT WEB APPLICATION CONTEXT 를 가져오는 기능이 없다.
 * root web application context 를 가져오는 기능을 추가한다면 컨트롤러 외에도 Service, Dao 계층까지 모두 DI 하여 테스트 할 수 있을것이다.
 * 
 */
public class _02_ConfigurableDispatcherServletTest {
	@Test
	public void helloController() throws ServletException, IOException {
		ConfigurableDispatcherServlet servlet = new ConfigurableDispatcherServlet();
		servlet.setRelativeLocations(getClass(), "spring-servlet.xml");
		servlet.setClasses(HelloSpring.class);
		servlet.init(new MockServletConfig("spring"));

		// MockHttpServletRequest req = new MockHttpServletRequest("GET", "/app/hello");
		// req.setAttribute("javax.servlet.include.servlet_path", "/app");

		MockHttpServletRequest req = new MockHttpServletRequest("GET", "/hello");
		req.addParameter("name", "Spring");
		MockHttpServletResponse res = new MockHttpServletResponse();

		servlet.service(req, res);

		ModelAndView mav = servlet.getModelAndView();
		assertThat(mav.getViewName(), is("/WEB-INF/view/hello.jsp"));
		assertThat((String) mav.getModel().get("message"), is("Hello Spring"));

	}
}
