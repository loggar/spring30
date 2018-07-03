package sample.spring3._17_controller;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.springframework.stereotype.Component;
import org.springframework.web.HttpRequestHandler;

import sample.spring3._16_webtest.AbstractDispatcherServletTest;
import sample.spring3._16_webtest.AfterRunService;

/**
 * 서블릿 인터페이스와 비슷한 구조의 HttpRequestHandler 를 구현한 컨트롤러
 * 
 * 실제로 httpRequestHandler 는 서블릿처럼 동작하는 컨트롤러를 만들기 위해 사용한다.
 * 전형적인 서블릿 스펙을 준수할 필요없이 HTTP 프로토콜을 기반으로 한 전용 서비스를 만들려고 할때 사용한다.
 * 예시) Spring HTTP Invoker
 * HttpRequestHandler 는 이렇게 모델과뷰 개념이 없는 HTTP 기반의 RMI 와 같은 로우레벨 서비스를 개발 할 때 이용할수 있다.
 * 
 * HttpRequestHandlerAdapter 는 Spring 디폴드 전략이다. 이미 컨텍스트에 빈으로 등록되어있다.
 * 
 */
public class _02_HttpRequestHandlerAdapterTest extends AbstractDispatcherServletTest {
	@Test
	public void helloServletController() throws ServletException, IOException {
		// HttpRequestHandlerAdapter 이미 컨텍스트에 빈으로 등록되어있다.(디폴트)
		// setClasses(HttpRequestHandlerAdapter.class, HelloServlet2.class);
		setClasses(HelloServlet2.class);

		initRequest("/hello").addParameter("name", "Spring");
		AfterRunService ars = runService();

		System.out.println(ars.getContentAsString());
		assertThat(ars.getContentAsString(), is("Hello Spring"));
	}

	@Component("/hello")
	static class HelloServlet2 implements HttpRequestHandler {
		@Override
		public void handleRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			System.out.println("HelloServlet2.handleRequest() ");
			String name = req.getParameter("name");
			resp.getWriter().print("Hello " + name);
		}

	}
}
