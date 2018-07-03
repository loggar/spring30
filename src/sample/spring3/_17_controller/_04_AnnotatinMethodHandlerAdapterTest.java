package sample.spring3._17_controller;

import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.Test;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import sample.spring3._16_webtest.AbstractDispatcherServletTest;

/**
 * AnnotatinMethodHandlerAdapter
 * 새롭게 많이 쓰이는 Spring Controller Type
 * 
 * Controller Type 에 관계없이 컨트롤러의 메소드에 붙은 [몇가지 어노테이션 정보, 메소드 이름, 파라미터, 리턴타입규칙] 을 분석해서 컨트롤러를 선별하고 호출방식을 결정
 * AnnotatinMethodHandlerAdapter 는 다른 핸들러어댑터들과는 다르게 DefaultAnnotationHandlerMapping 과 함께 사용해야한다. 두가지 모두 동일한 어노테이션을 사용하기 때문이다.
 * 
 * AnnotatinMethodHandlerAdapter 는 Spring 디폴드 전략이다. 이미 컨텍스트에 빈으로 등록되어있다.
 * 
 */
public class _04_AnnotatinMethodHandlerAdapterTest extends AbstractDispatcherServletTest {
	@Test
	public void annotationHello() throws ServletException, IOException {
		setClasses(HelloController.class);
		initRequest("/hello").addParameter("name", "Spring");
		runService();
		assertModel("message", "Hello Spring");
		assertViewName("/WEB-INF/view/hello.jsp");
	}

	@Controller
	static class HelloController {
		@RequestMapping("/hello")
		public String hello(@RequestParam("name") String name, ModelMap map) {
			map.put("message", "Hello " + name);
			return "/WEB-INF/view/hello.jsp";
		}
	}
}
