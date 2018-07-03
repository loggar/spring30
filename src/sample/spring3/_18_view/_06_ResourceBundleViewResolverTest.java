package sample.spring3._18_view;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.view.ResourceBundleViewResolver;

import sample.spring3._16_webtest.AbstractDispatcherServletTest;

/**
 * ResourceBundleViewResolver
 * 프로퍼티 파일로부터 뷰의 정보를 매핑받아 사용 views.properties 참고.
 * 프로퍼티 파일의 기본 경로는 classpath 이며, 이를 변경하려면 ResourceBundleViewResolver 의 basename 프로퍼티를 설정하여야한다.
 * 프로퍼티 파일은 로케일별로 나뉠 수 있다.
 * 
 * XmlViewResoler 는 기본적으로 ResourceBundleViewResolver 와 유사하며 프로퍼티 파일대신 XML 의 빈 설정파일을 이용해 뷰를 등록한다.
 * 뷰이름과 일치하는 아이디를 가진 빈을 뷰로 이용한다.
 * "/WEB-INF/views.xml" 이 기본 설정패스이며, 서블릿 컨텍스트를 부모로 갖는 애플리케이션 컨텍스트 레벨로 만들어진다.
 * 빈으로 등록되기 때문에 자유로이 DI 할 수 있지만, 로케일은 제공하지 않는다.
 * 
 * BeanNameViewResolver 는 뷰 이름과 동일한 빈 이름을 가진 빈을 찾아서 뷰로 사용하게 해준다. 
 * 이때 검색하는 빈의 레벨은 서블릿 컨텍스트 레벨이다.
 * 
 */
public class _06_ResourceBundleViewResolverTest extends AbstractDispatcherServletTest {
	@Test
	public void rbvr() throws ServletException, IOException {
		setClasses(SampleResourceBundleViewResolver.class, HelloController.class);
		runService("/hello");
		assertThat(response.getForwardedUrl(), is("/WEB-INF/view/hello.jsp"));
	}
	
	static class SampleResourceBundleViewResolver extends ResourceBundleViewResolver {
		{
			setBasename("sample/spring3/_18_view/views");
		}
	}

	@RequestMapping("/hello")
	public static class HelloController implements Controller {
		public ModelAndView handleRequest(HttpServletRequest req, HttpServletResponse res) throws Exception {
			return new ModelAndView("hello");
		}
	}
}
