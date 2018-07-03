package sample.spring3._16_webtest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * 서블릿 테스트용 목 오브젝트
 * MockHttpServletRequest
 * MockHttpServletResponse
 * MockHttpSession
 * MockServletConfig
 * MockServletContext
 * 
 * 실제 테스트용 서버를 구성하지 않고 테스트하기 위해 작성.
 * 테스트하기 까다롭다.
 * 
 */
public class _01_ServletTest {
	@Test
	public void getMethodServlet() throws ServletException, IOException {
		MockHttpServletRequest req = new MockHttpServletRequest("GET", "/hello");
		req.addParameter("name", "Spring");
		MockHttpServletResponse res = new MockHttpServletResponse();

		SimpleGetServlet servlet = new SimpleGetServlet();
		servlet.service(req, res);
		servlet.init();

		assertThat(res.getContentAsString(), is("<HTML><BODY>Hello Spring</BODY></HTML>"));
		assertThat(res.getContentAsString().contains("Hello Spring"), is(true));
	}

}
