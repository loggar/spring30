package sample.spring3._17_controller;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.SimpleServletHandlerAdapter;

import sample.spring3._16_webtest.AbstractDispatcherServletTest;

/**
 * Standard Servlet 과 SimpleServletHandlerAdapter
 * 
 * 일반적인 표준 서블릿도 다음처럼 스프링 빈으로 등록 후 SimpleServletHandlerAdapter 를 통해 service 할 수 있다.
 * 단 서블릿이 컨트롤러 빈으로 등록된 경우에는 init(), destroy() 와 같은 서블릿 생명주기 메소드가 자동으로 호출 되진 않는다.
 * 초기화 작업을 하는 코드가 있다면
 * XML: <bean> 태그의 init-method 어트리뷰트
 * Java Code: @PostConstruct 메소드
 * 등을 이용해 빈 생성 후 초기화 메소드가 실행 되도록 유도한다.
 * 
 * 기본적으로 서블릿 타입의 컨트롤러를 DispatcherServlet 이 호출 해줄때 필요한 핸들러 어댑터는 SimpleServletHandlerAdapter 이며,
 * XML 설정에 다음과 같이 핸들러 어댑터를 등록해줘야한다.
 * <bean class="org.springframework.web.servlet.handler.SimpleServletHandlerAdapter" />
 * 
 * 아래는 XML 설정없이 AbstractDispatcherServletTest 기능으로 어댑터를 설정하고 테스트 했다.
 * 
 * Servlet 타입의 컨트롤러는 ModelAndView 를 반환하지 않는다. HttpServletResponse 를 확인해야 한다.
 * 
 */
public class _01_SimpleServletHandlerAdapterTest extends AbstractDispatcherServletTest {
	@Test
	public void helloServletController() throws ServletException, IOException {
		setClasses(SimpleServletHandlerAdapter.class, HelloServlet.class);
		initRequest("/hello").addParameter("name", "Spring");
		assertThat(runService().getContentAsString(), is("Hello Spring"));
	}

	@SuppressWarnings("serial")
	@Component("/hello")
	static class HelloServlet extends HttpServlet {
		protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			String name = req.getParameter("name");
			resp.getWriter().print("Hello " + name);
		}
	}
}
