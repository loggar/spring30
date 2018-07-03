package sample.spring3._18_view;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.view.json.MappingJacksonJsonView;

import sample.spring3._16_webtest.AbstractDispatcherServletTest;

/**
 * MappingJacksonJsonView
 * application/json 으로 지정되는 view 작성
 * 기본적으로 모델의 모든 오브젝트를 JSON 으로 변환
 * Set<String> renderedAttributes 프로퍼티를 지정해서 일부 모델 오브젝트만 변환에 사용할 수 있음
 * 변환작업은 Jackson JSON processor 를 사용.
 * 
 * 이 챕터에서 소개하지 않은 기타 View 들
 * VelocityView
 * FreeMarkerView
 * AbstractExcelView
 * AbstractJExcelView
 * AbstractPdfView
 * AbstractAtomFeedView
 * AbstractRssFeedView
 * XsltView
 * TilesView
 * AbstractJasperReportsView
 * 
 */
public class _04_JsonViewTest extends AbstractDispatcherServletTest {
	@Test
	public void jsonView() throws ServletException, IOException {
		setClasses(HelloController.class);
		initRequest("/hello").addParameter("name", "Spring");
		runService();
		assertThat(getContentAsString(), is("{\"messages\":\"Hello Spring\"}"));
	}
	
	@RequestMapping("/hello")
	public static class HelloController implements Controller {
		MappingJacksonJsonView jacksonJsonView = new MappingJacksonJsonView();
		
		public ModelAndView handleRequest(HttpServletRequest req, HttpServletResponse res) throws Exception {
			Map<String, Object> model = new HashMap<String, Object>();
			model.put("messages", "Hello " +req.getParameter("name"));
			
			return new ModelAndView(jacksonJsonView, model);
		}
	}
}
