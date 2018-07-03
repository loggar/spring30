package sample.spring3._17_controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.mvc.annotation.DefaultAnnotationHandlerMapping;
import org.springframework.web.servlet.mvc.support.ControllerBeanNameHandlerMapping;
import org.springframework.web.servlet.mvc.support.ControllerClassNameHandlerMapping;

import sample.spring3._16_webtest.AbstractDispatcherServletTest;

public class _05_HandlerMappingTest extends AbstractDispatcherServletTest {
	/*
	 * BeanNameUrlHandlerMapping
	 * <bean name="url" .. />
	 * 빈의 name 에 해당하는 url 이 요청되면 해당 bean controller 를 핸들링.
	 * 복잡한 어플리케이션에서는 너무 중복된 URL 정보의 혼용(XML, Annotation 등) 으로 관리가 불편.
	 * 
	 * BeanNameUrlHandlerMapping 은 디폴트 전략 빈이다.
	 */
	@Test
	public void beanNameUrlHM() throws ServletException, IOException {
		setRelativeLocations("bean_name_url_handler_mapping.xml");
		runService("/hello").assertViewName("/hello.jsp");
		runService("/hello/world").assertViewName("/hello/world.jsp");
		runService("/multi/").assertViewName("/multi/*.jsp");
		runService("/multi/a").assertViewName("/multi/*.jsp");
		runService("/root/sub").assertViewName("/root/**/sub.jsp");
		runService("/root/a/b/c/sub").assertViewName("/root/**/sub.jsp");
		runService("/s").assertViewName("/s*.jsp");
		runService("/s1234").assertViewName("/s*.jsp");
	}

	static class Controller1 extends AbstractController {
		private String url;

		public void setUrl(String url) {
			this.url = url;
		}

		protected ModelAndView handleRequestInternal(HttpServletRequest req, HttpServletResponse res) throws Exception {
			return new ModelAndView(url + ".jsp");
		}
	}

	/*
	 * ControllerBeanNameHandlerMapping
	 * 빈의 id 나 name 값을 이용해 url 매핑해준다.
	 * 
	 * <bean id="hello" class="MyController" />
	 * 이 경우 "/hello 에 매핑된다. id 의 경우 문자의 제약이 더 있으므로 name 값을 지정하여 매핑할 수도 있다.
	 * 
	 * XML <bean> name 을 이용하거나, 스테레오타입 애노테이션을 이용하는경우라면 좀더 자유롭다.
	 * 
	 * @Component("hello") public class MyController implements Controller { }
	 * 이 경우 ControllerBeanNameHandlerMapping 에 의해 "/hello" 에 매핑된다.
	 * 
	 * ControllerBeanNameHandlerMapping 은 디폴트 핸들러 매핑이 아니므로 사용하려면 전략 빈으로 등록해줘야한다.
	 * prefix, suffix 지정할 수 있다.
	 * 
	 * <bean class="org.springframework.web.servlet.support.ControllerBeanNameHandlerMapping">
	 * <property name="urlPrefix' value="/app/beanname/" />
	 * </bean>
	 * 
	 * ControllerBeanNameHandlerMapping 처럼 특정 전략을 빈으로 등록하면, 디폴트 핸들러 매핑인
	 * AnnotationMethodHandlerAdapter 와 DefaultAnnotationHandlerMapping 은 적용되지 않는다.
	 */
	@Test
	public void controllerBeanNameHM() throws ServletException, IOException {
		setClasses(ControllerBeanNameHandlerMapping.class, Controller2.class);
		runService("/hello").assertViewName("controller2.jsp");
	}

	@Component("hello")
	static class Controller2 implements Controller {
		public ModelAndView handleRequest(HttpServletRequest req, HttpServletResponse res) throws Exception {
			return new ModelAndView("controller2.jsp");
		}
	}

	/*
	 * ControllerClassNameHandlerMapping
	 * 컨트롤러의 클래스 이름을 소문자로 한 url 에 매핑된다
	 * 클래스 이름이 -Controller 로 끝나는경우 "Controller" 라는 문자는 제외하고 매핑을 적용한다.
	 * 
	 * ControllerClassNameHandlerMapping 는 디폴트 전략이 아니다.
	 */
	@Test
	public void controllerClassNameHM() throws ServletException, IOException {
		setClasses(ControllerClassNameHandlerMapping.class, Controller3Controller.class);
		runService("/_05_handlermappingtest.controller3").assertViewName("controller3.jsp");
	}

	static class Controller3Controller implements Controller {
		public ModelAndView handleRequest(HttpServletRequest req, HttpServletResponse res) throws Exception {
			return new ModelAndView("controller3.jsp");
		}
	}

	/*
	 * SimpleUrlHandlerMapping
	 * 가장 많이 사용되어 온 핸들러 매핑법
	 * SimpleUrlHandlerMapping 를 빈으로 등록할때 mappings 라는 property 에 url 매핑을 나열하는 방법
	 * 
	 * SimpleUrlHandlerMapping 은 디폴트 전략이 아니다.
	 */
	@Test
	public void simpleUrlHM() throws ServletException, IOException {
		setRelativeLocations("simple_url_handler_mapping.xml");
		runService("/hello").assertViewName("c1.jsp");
		runService("/multi/a").assertViewName("c2.jsp");
		runService("/deep/a/b/c/sub").assertViewName("c3.jsp");
	}

	/*
	 * DefaultAnnotationHandlerMapping
	 * "@RequestMapping" 이라는 어노테이션을 컨트롤러 클래스나 메소드에 직접 부여하고 매핑
	 * HTTP METHOD, REQUEST PARAMETER, HTTP HEADER 정보 까지 매핑에 활용할 수 있음.
	 * 
	 * 매핑 어노테이션의 사용정책과 작성 기준을 잘 만들어두지 안흥면 매핑정보가 관리하기 힘듬
	 * 
	 * DefaultAnnotationHandlerMapping 은 디폴트 전략 빈이다.
	 */
	@Test
	public void annotationHM() throws ServletException, IOException {
		setClasses(DefaultAnnotationHandlerMapping.class, AbcController.class);
		runService("/hello").assertViewName("hello.jsp");

	}

	@RequestMapping("/hello")
	static class AbcController implements Controller {
		public ModelAndView handleRequest(HttpServletRequest req, HttpServletResponse res) throws Exception {
			return new ModelAndView("hello.jsp");
		}
	}

	/*
	 * 여러개의 핸들러 매핑이 빈으로 등록되어 있을 때, URL 매핑을 성공시키는 우선순위를 줄 수있다.
	 * 핸들러 매핑은 모두 Ordered 인터페이스를 구현하고 있으며, 이 인터페이스가 제공하는 order 프로퍼티를 통해 우선순위를 지정 할 수 있다.
	 * <bean> 등록시 order 프로퍼티 등록
	 */
	@Test
	public void orderOfHM() throws ServletException, IOException {
		setClasses(Controller4.class, Controller5.class);
		runService("/hello").assertViewName("controller5.jsp");

		setClasses(BeanNameHM.class, AnnotationHM.class, Controller4.class, Controller5.class);
		buildDispatcherServlet();
		runService("/hello").assertViewName("controller4.jsp");
	}

	static class BeanNameHM extends BeanNameUrlHandlerMapping {
		public BeanNameHM() {
			setOrder(2);
		}
	}

	static class AnnotationHM extends DefaultAnnotationHandlerMapping {
		public AnnotationHM() {
			setOrder(1);
		}
	}

	@RequestMapping
	static class Controller4 {
		@RequestMapping("/hello")
		public String hello() {
			return "controller4.jsp";
		}
	}

	@Component("/hello")
	static class Controller5 implements Controller {
		public ModelAndView handleRequest(HttpServletRequest req, HttpServletResponse res) throws Exception {
			return new ModelAndView("controller5.jsp");
		}
	}

	/*
	 * 핸들러매핑을 빈으로 등록할때 해당 빈의 프로퍼티로 디폴티 핸들러를 지정해두면
	 * URL 을 매핑할 대상을 찾지 못하였을 경우, 자동으로 디폴트 핸들러를 선택 해 준다.
	 */
	@Test
	public void defaultHandler() throws ServletException, IOException {
		setRelativeLocations("default_handler.xml");
		setClasses(DefaultHandler.class);
		runService("/dsalkfjalk").assertViewName("defaulthandler.jsp");
	}

	@Component("defaultHandler")
	static class DefaultHandler implements Controller {
		public ModelAndView handleRequest(HttpServletRequest req, HttpServletResponse res) throws Exception {
			return new ModelAndView("defaulthandler.jsp");
		}
	}
}
