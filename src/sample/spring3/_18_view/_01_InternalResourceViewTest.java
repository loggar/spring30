package sample.spring3._18_view;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import sample.spring3._16_webtest.AbstractDispatcherServletTest;

/**
 * InternalResourceView / JstlView
 * 
 * InternalResourceViewResolver 는 디폴드 뷰 리졸버이다. 주로 JSP JSTL 을 사용하고자 할때 쓰인다.
 * prefix, suffix property 로 뷰의 앞뒤 경로를 지정 할 수 있다.
 * JSTL 라이브러리가 클래스패스에 존재하면 JstlView 를 사용하고, 존재하지 않으면 InternalResourceView 를 사용한다.
 * 
 * VelocityViewResolver, FreeMarkerViewResolver 의 사용법도 InternalResourceViewResolver 와 비슷하다.
 * JSP 와는 다르게 템플릿의 경로를 만을 때 사용할 루트 패스를 미리 VelocityConfigurer 나 FreeMarkerConfigurer 로 지정해줘야 한다.
 * 그래서 prefix 는 잘 사용하지 않는다.
 * 
 */
public class _01_InternalResourceViewTest extends AbstractDispatcherServletTest {
	@Test
	public void jstlView() throws ServletException, IOException {
		setRelativeLocations("jstlview.xml");
		setClasses(HelloController.class);
		runService("/hello");

		System.out.println(this.response.getForwardedUrl());
	}

	@RequestMapping("/hello")
	public static class HelloController implements Controller {
		public ModelAndView handleRequest(HttpServletRequest req, HttpServletResponse res) throws Exception {
			return new ModelAndView("hello").addObject("message", "Hello Spring");
		}
	}

}
