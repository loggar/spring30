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
import org.springframework.web.servlet.view.RedirectView;

import sample.spring3._16_webtest.AbstractDispatcherServletTest;

/**
 * RedirectView
 * HttpServletResponse.sendRedirect() 를 호출해주는 기능
 * 모델정보가있다면 URL 파라미터로 추가된다.
 */
public class _02_RedirectViewTest extends AbstractDispatcherServletTest {
	@Test
	public void redirectView() throws ServletException, IOException {
		setClasses(HelloController.class);
		runService("/hello");
		assertThat(this.response.getRedirectedUrl(), is("/main?name=Spring"));
	}

	@RequestMapping("/hello")
	public static class HelloController implements Controller {
		public ModelAndView handleRequest(HttpServletRequest req, HttpServletResponse res) throws Exception {
			return new ModelAndView(new RedirectView("/main", true)).addObject("name", "Spring");
			// 다음처럼 사용하는것도 편리하다.
			// return new ModelAndView("redirect:/main").addObject("name", "Spring");
		}
	}
}
