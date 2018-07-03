package sample.spring3._18_view;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.RequestToViewNameTranslator;
import org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.view.DefaultRequestToViewNameTranslator;

import sample.spring3._16_webtest.AbstractDispatcherServletTest;

/**
 * View 와 관련된 DispatcherServlet 전략 중에는 ResourceToViewNameTranslator 라는 것도 있다.
 * 이 전략은 뷰 이름을 Controller 가 넘겨주지 않았을 때 요청 URL 을 이용해 자동으로 뷰 이름을 만들어준다.
 * 이 뷰 이름에 prefix, suffix 를 추가해 최종 view 경로를 자동생성한다.
 * "/admin/member.do" -> prefix + "admin/member" + suffix
 * 이런 개발 스타일을 "관례를 우선하는 설정" CoC (Convention Over Configuration) 이라 한다.
 * 
 */
public class _05_RequestViewNameTranslatorTest extends AbstractDispatcherServletTest {
	@Test
	public void defaultRequestToViewNameTranslatorTest() throws ServletException, IOException {
		setClasses(Config1.class);
		runService("/hello").assertViewName("hello.jsp");
		runService("/hello/world").assertViewName("hello/world.jsp");
		runService("/hi").assertViewName("hi.jsp");
	}
	@Configuration static class Config1 {
		@Bean public HandlerMapping handlerMapping() {
			return new BeanNameUrlHandlerMapping() {{
				this.setDefaultHandler(defaultHandler());
			}};
		}
		@Bean public RequestToViewNameTranslator viewNameTranslator() {
			return new DefaultRequestToViewNameTranslator() {{
				this.setSuffix(".jsp");
			}};
		}
		@Bean public Controller defaultHandler() {
			return new Controller() {
				public ModelAndView handleRequest(HttpServletRequest req, HttpServletResponse res) throws Exception {
					return new ModelAndView();
				}
			};
		}
	}
}
