package sample.spring3._17_controller;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import sample.spring3._16_webtest.AbstractDispatcherServletTest;

/**
 * Controller, AbstractController(Controller 를 구현하였으며 기본적인 웹브라우저를 클라이언트로 갖는 컨트롤러 로서의 필수기능 제시) 를 구현한 클래스와 SimpleControllerHandlerAdapter
 * 가장 많이 사용되어온 Spring Controller Type
 * 
 * AbstractController 의 유용한 프로퍼티
 * 1. synchronizeOnSession: HTTP 세션에 대한 동기화 여부 결정.
 * 2. supportedMethods: HTTP method 지정
 * 3. useExpiresHeader, userCacheControlHeader, useCacheControlNoStore, cacheSeconds: 브라우저 캐시 설정정보 관련..
 * 
 * SimpleControllerHandlerAdapter 는 Spring 디폴드 전략이다. 이미 컨텍스트에 빈으로 등록되어있다.
 * 
 */
public class _03_SimpleControllerHandlerAdapterTest extends AbstractDispatcherServletTest {
	@Test
	public void helloSimpleController() throws ServletException, IOException {
		// SimpleControllerHandlerAdapter 이미 컨텍스트에 빈으로 등록되어있다.(디폴트)
		// setClasses(SimpleControllerHandlerAdapter.class, HelloController.class);
		setClasses(HelloController.class);
		initRequest("/hello").addParameter("name", "Spring");
		runService();
		assertModel("message", "Hello Spring");
		assertViewName("/WEB-INF/view/hello.jsp");
	}

	@Test(expected = Exception.class)
	public void noParameterHelloSimpleController() throws ServletException, IOException {
		setClasses(HelloController.class);
		initRequest("/hello");
		runService();
	}

	@Test
	public void helloControllerUnitTest() throws Exception {
		Map<String, String> params = new HashMap<String, String>();
		params.put("name", "Spring");
		Map<String, Object> model = new HashMap<String, Object>();

		new HelloController().control(params, model);

		assertThat((String) model.get("message"), is("Hello Spring"));
	}

	@RequestMapping("/hello")
	static class HelloController extends SimpleController {
		public HelloController() {
			this.setRequiredParams(new String[] { "name" });
			this.setViewName("/WEB-INF/view/hello.jsp");
		}

		public void control(Map<String, String> params, Map<String, Object> model) throws Exception {
			model.put("message", "Hello " + params.get("name"));
		}
	}

	static abstract class SimpleController implements Controller {
		private String[] requiredParams;
		private String viewName;

		public void setRequiredParams(String[] requiredParams) {
			this.requiredParams = requiredParams;
		}

		public void setViewName(String viewName) {
			this.viewName = viewName;
		}

		final public ModelAndView handleRequest(HttpServletRequest req,
				HttpServletResponse res) throws Exception {
			if (viewName == null) throw new IllegalStateException();

			Map<String, String> params = new HashMap<String, String>();
			for (String param : requiredParams) {
				String value = req.getParameter(param);
				if (value == null) throw new IllegalStateException();
				params.put(param, value);
			}

			Map<String, Object> model = new HashMap<String, Object>();

			this.control(params, model);

			return new ModelAndView(this.viewName, model);
		}

		public abstract void control(Map<String, String> params, Map<String, Object> model) throws Exception;
	}
}
