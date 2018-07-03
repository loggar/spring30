package sample.spring3._17_controller;

import java.io.IOException;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;

import sample.spring3._16_webtest.AbstractDispatcherServletTest;

/**
 * 핸들러어댑터의 확장
 * 커스텀 핸들러 인터페이스와 커스텀 컨트롤러 정의
 * ViewName, RequiredParams 어노테이션 정의
 * 
 */
public class _07_HandlerAdapterExtendTest extends AbstractDispatcherServletTest {
	@Test
	public void simpleHandlerAdapter() throws ServletException, IOException {
		setClasses(SimpleHandlerAdapter.class, HelloController.class);
		initRequest("/hello").addParameter("name", "Spring").runService();
		assertViewName("/WEB-INF/view/hello.jsp");
		assertModel("message", "Hello Spring");
	}

	@Component("/hello")
	static class HelloController implements SimpleController {
		@ViewName("/WEB-INF/view/hello.jsp")
		@RequiredParams({ "name" })
		public void control(Map<String, String> params, Map<String, Object> model) {
			model.put("message", "Hello " + params.get("name"));
		}
	}

	static class SimpleHandlerAdapter implements HandlerAdapter {
		/*
		 * 이 핸들어 어댑터가 지원하는 컨트롤러 타입을 확인해 준다. 하나 이상의 타입을 지원 하게 할 수도 있다.
		 */
		public boolean supports(Object handler) {
			return (handler instanceof SimpleController);
		}

		public ModelAndView handle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {
			Method m = ReflectionUtils.findMethod(handler.getClass(), "control", Map.class, Map.class);
			
			/*
			 * 컨트롤러 메소드의 어노테이션에서 필요한 정보를 가져온다.
			 */
			ViewName viewName = AnnotationUtils.getAnnotation(m, ViewName.class);
			RequiredParams requiredParams = AnnotationUtils.getAnnotation(m, RequiredParams.class);

			Map<String, String> params = new HashMap<String, String>();
			for (String param : requiredParams.value()) {
				String value = req.getParameter(param);
				if (value == null) throw new IllegalStateException();
				params.put(param, value);
			}

			Map<String, Object> model = new HashMap<String, Object>();

			/*
			 * DispatcherServlet 은 컨트롤러의 Type 을 모르기 때문에 컨트롤러를 Object Type 으로 넘겨준다. 이를 적절한 TYpe 으로 캐스팅한 후 수행한다.
			 */
			((SimpleController) handler).control(params, model);

			return new ModelAndView(viewName.value(), model);
		}

		/*
		 * getLastModified 가 0보다 작은 값을 리턴하면 getLastModified 의 지원은 없는것으로 만들어진다(캐싱을 적용하지 않는다)
		 */
		public long getLastModified(HttpServletRequest request, Object handler) {
			return -1;
		}
	}

	public interface SimpleController {
		void control(Map<String, String> params, Map<String, Object> model);
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Inherited
	public @interface ViewName {
		String value();
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Inherited
	public @interface RequiredParams {
		String[] value();
	}
}
