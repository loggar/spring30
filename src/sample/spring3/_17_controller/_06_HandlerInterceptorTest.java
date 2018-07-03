package sample.spring3._17_controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.mvc.Controller;

import sample.spring3._16_webtest.AbstractDispatcherServletTest;

/**
 * 핸들러매핑 은 DispatcherServlet 으로부터 매핑 작업을 요청받으면 그 결과로 핸들러 실행 체인 (HandlerExecutionChain)을 돌려준다.
 * HandlerExecutionChain 은 0개 이상의 HandlerInterceptor 를 수행한 후 컨트롤러를 실행될 수 있도록 구성되어있다.
 * 
 * boolean preHandle(req, resp, handler)
 * 컨트롤러가 실행되기 전에 호출, return true 이면 다음 체인을, false 이면 다음체인과 컨트롤러 모두 중단된다.
 * void postHandle(req, resp, handler, modelAndView)
 * 컨트롤러 실행 후 동작. preHandle 에서 false 리턴한 경우에는 postHandle 역시 수행되지 않는다.
 * void afterCompletion(req, resp, handler, exception)
 * 이름그대로 모든 뷰에서 최종 결과를 생성하는 일을 포함한 모든 작업이 다 완료 된 후에 실행된다.
 * 
 * HandlerInterceptor 의 적용 예시
 * 
 * <pre><code>
 * <bean class="org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping">
 *     <property name="interceptors">
 *         <list>
 *             <ref bean="simpleInterceptor" />
 *             <ref bean="logInterceptor" />
 *         </list>
 *     </property>
 * </bean>
 * </code></pre>
 * 
 * 인터셉터는 bean 으로 등록되어야 한다.
 * 
 */
public class _06_HandlerInterceptorTest extends AbstractDispatcherServletTest {
	@Test
	public void preHandleReturnValue() throws ServletException, IOException {
		setClasses(InterceptorConfig.class, Controller1.class);
		runService("/hello").assertViewName("controller1.jsp");
		assertThat((Controller1) getBean(Interceptor1.class).handler, is(getBean(Controller1.class)));

		getBean(Interceptor1.class).ret = false;
		assertThat(runService("/hello").getModelAndView(), is(nullValue()));
	}

	@Component("/hello")
	static class Controller1 implements Controller {
		public ModelAndView handleRequest(HttpServletRequest req, HttpServletResponse res) throws Exception {
			return new ModelAndView("controller1.jsp");
		}
	}

	static class Interceptor1 extends HandlerInterceptorAdapter {
		Object handler;
		boolean ret = true;

		public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
			this.handler = handler;
			return ret;
		}
	}

	@Configuration
	static class InterceptorConfig {
		@Bean
		public HandlerMapping beanNameUrlHM() {
			BeanNameUrlHandlerMapping hm = new BeanNameUrlHandlerMapping();
			hm.setInterceptors(new Object[] { interceptor1() });
			return hm;
		}

		@Bean
		public HandlerInterceptor interceptor1() {
			return new Interceptor1();
		}
	}

	@Test
	public void postHandle() throws ServletException, IOException {
		setClasses(InterceptorConfig2.class, Controller1.class);
		runService("/hello").assertViewName("controller1.jsp");
		assertThat(getBean(Interceptor2.class).post, is(true));

		getBean(Interceptor2.class).ret = false;
		getBean(Interceptor2.class).post = false;
		assertThat(getBean(Interceptor2.class).post, is(false));
	}

	@Configuration
	static class InterceptorConfig2 {
		@Bean
		public HandlerMapping handlerMapping() {
			return new BeanNameUrlHandlerMapping() {
				{
					this.setInterceptors(new Object[] { interceptor2() });
				}
			};
		}

		@Bean
		public HandlerInterceptor interceptor2() {
			return new Interceptor2();
		}
	}

	static class Interceptor2 extends HandlerInterceptorAdapter {
		boolean post, ret = true;

		public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
			post = true;
		}

		public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
			return ret;
		}
	}
}
