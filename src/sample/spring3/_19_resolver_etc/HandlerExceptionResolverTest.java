package sample.spring3._19_resolver_etc;

import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import sample.spring3._16_webtest.AbstractDispatcherServletTest;

/**
 * HandlerExceptionResolver 는 컨트롤러의 작업 중에 발생한 예외를 어떻게 처리할지 결정하는 전략이다.
 * 
 * 컨트롤러나 그 뒤의 계층에서 던져진예외는 DispatcherServlet 이 일단 전달받은 뒤에 다시 서블릿 밖으러 던져서 서블릿컨테이너가 처리하게 될 것이다.
 * 서블릿 컨테이너는 별다른 설정이 없다면 브라우저에 에러 내용을 던진다
 * web.xml 에 <error-page> 가 지정되어 있다면 해당 JSP 페이지 등을 보여줄 수 있다
 * 
 * 그런데 HandlerExceptionResolver 가 등록되어있다면 DispatcherServlet 먼저 HandlerExceptionResolver 가 예외처리를 할 수 있는지 확인한다.
 * 해당 예외에 대한 HandlerExceptionResolver 가 있다면 HandlerExceptionResolver 단에서 예외가 처리되고 DispatcherServlet 은 이후 다른곳으로 예외를 전파하지 않는다.
 * 
 * AnnotationMethodHandlerExceptionResolver
 *     디폴트 전략이며, 컨트롤러의 클래스 안에서 "@ExceptionHandler(ExceptionType)" 을 가진 메소드를 찾아 예외처리를 맡긴다.
 *     해당 메소드가 마치 하나의 컨트롤러 처럼 동작하며, ModelAndView 를 리턴 할 수 있다.
 * 
 * ResponseStatusExceptionResolver
 *     디폴트 전략이며,
 *     "@ResponseStatus" 를 지정한 예외 클래스를 만들고 HttpStatus 에 정의되어 있는 HTTP 응답상태값 을 value 엘리먼트에 지정한다.
 *     필요하면 reason 에 설명을 넣을 수 있다.
 * 
 * DefaultHandlerExceptionResolver
 *     디폴트 전략이며 위 두가지 예외 리졸버가 처리하지 못한 예외의 처리를 담당한다.
 *     스프링 내부적으로 발생하는 주요 예외를 처리해주는 표준 예외처리 로직을 담고있다.
 *     다른 핸들러 예외 리졸버를 빈으로 등록해서 위 두 디폴트 예외 리졸버가 동작하지 않는경우라면 명시적으로 DefaultHandlerExceptionResolver 를 빈으로 등록해줘야 동작한다.
 *     
 * SimpleMappingExceptionResolver
 *     예외와 그에 대응하는 뷰 이름을 프로퍼티로 등록하여 예외에 뷰를 매핑하는 방법이다.
 *     simple_mapping_exception_resolver.xml 참고
 *     디폴트 전략이 아니므로 사용하려면 직접 빈으로 등록해줘야한다.
 *     예외발생시 바로 view 로 전달되므로 로그를 남기거나 관리자에게 통보하는 작업을 필요로하는 경우 
 *     HandlerInterceptor 의 afterCompletion() 메소드가 담당하는것이 좋다.
 *     예외를 처리할 JSP 뷰를 사용한다고 해서 로그작성 과 같은 코드를 JSP 안에 넣는것은 바람직하지 않다.
 * 
 */
public class HandlerExceptionResolverTest extends AbstractDispatcherServletTest {
	@Test
	public void annotationMethod() throws ServletException, IOException {
		setClasses(HelloCon.class);
		runService("/hello");
		assertViewName("dataexception");
		System.out.println(getModelAndView().getModel().get("msg"));
	}

	@RequestMapping
	static class HelloCon {
		@RequestMapping("/hello")
		public void hello() {
			if (true) throw new DataRetrievalFailureException("hi");
		}

		@ExceptionHandler(DataAccessException.class)
		public ModelAndView dataAccessExceptionHandler(DataAccessException ex) {
			return new ModelAndView("dataexception").addObject("msg", ex.getMessage());
		}
	}

	@Test
	public void responseStatus() throws ServletException, IOException {
		setClasses(HelloCon2.class);
		runService("/hello");
		System.out.println(response.getStatus());
		System.out.println(response.getErrorMessage());
	}

	@RequestMapping
	static class HelloCon2 {
		@RequestMapping("/hello")
		public void hello() {
			if (true) throw new NotInServiceException();
		}
	}

	@SuppressWarnings("serial")
	@ResponseStatus(value = HttpStatus.SERVICE_UNAVAILABLE, reason = "서비스 긴급 점검중")
	static class NotInServiceException extends RuntimeException {
	}

}
