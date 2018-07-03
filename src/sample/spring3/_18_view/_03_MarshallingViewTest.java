package sample.spring3._18_view;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.view.xml.MarshallingView;

import sample.spring3._16_webtest.AbstractDispatcherServletTest;

/**
 * Spring 3.0 에서 새롭게 등장한 OXM(Object-XML Mapping) 추상화 기능을 활용해서 
 * application/xml 타입의 XML content 를 작성해주는 뷰
 */
public class _03_MarshallingViewTest extends AbstractDispatcherServletTest {
	@Test
	public void marshallingView() throws ServletException, IOException {
		setRelativeLocations("marshallingview.xml");
		initRequest("/hello").addParameter("name", "Spring").addParameter("viewtype", "xml");
		runService();
		
		System.out.println(getContentAsString());
		assertThat(getContentAsString().indexOf("<info><message>Hello Spring</message></info>") >= 0, is(true));
	}
	
	@Test
	public void marshallingView_2() throws ServletException, IOException {
		setRelativeLocations("marshallingview.xml", "jstlview.xml");
		initRequest("/hello").addParameter("name", "Spring");
		runService();
		
		System.out.println(this.response.getForwardedUrl());
	}
	
	@RequestMapping("/hello")
	public static class HelloController implements Controller {
		// 같은 타입의 뷰가 여러 개 존재할 수 있으므로 빈 이름으로 매핑하도록 "@Resource" 사용했음
		@Resource MarshallingView helloMarshallingView;
		
		public ModelAndView handleRequest(HttpServletRequest req, HttpServletResponse res) throws Exception {
			Map<String, Object> model = new HashMap<String, Object>();
			model.put("info", new Info("Hello " +req.getParameter("name")));
			
			// XML 포멧을 원하면 MarshallingView 로, JSP 를 원하면 InternalResourceView 를 선택하게 할 수도 있다.
			if("xml".equals(req.getParameter("viewtype"))) {
				return new ModelAndView(helloMarshallingView, model);
			} else {
				return new ModelAndView("/WEB-INF/views/hello.jsp", model);
			}
		}
	}

}
