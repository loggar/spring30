package sample.spring3._18_view;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.Test;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import sample.spring3._16_webtest.AbstractDispatcherServletTest;

/**
 * ContentNegotiatingViewResolver
 * ContentNegotiatingViewResolver 는 view 를 찾아주는것이 아닌 view resolver 를 결정해 주는 리졸버 이다.
 * 특정 URL 정보를 처리한 Controller 가 여러 종류의 view 에 정보를 전달하는 상황이라 할때 유용하다.
 * 
 * ContentNegotiatingViewResolver 를 사용하는 경우에는 다른 뷰 리졸버들은 독립적으로 동작하지 못하므로
 * ContentNegotiatingViewResolver 의 프로퍼티 viewResolvers 에 뷰 리졸버들을 리스트로 등록한다(content_negotiating_view_resolver.xml 참고)
 *     
 * ContentNegotiatingViewResolver 의 View 선정 작업은 다음의 순서를 따른다.
 * 
 * 1. 미디어 타입결정
 *     1.1 URL 확장자 이용 (.html .json .pdf ...)
 *     1.2 파라미터로부터 추출 (hello.do?forma=pdf)
 *     1.3 HTTP 의 콘텐트교섭(Content Negotiation) 에 사용되는 Accept 헤더 사용
 *     1.4 defaultContentType 프로퍼티에 설정해준 defaultMediaType 을 따름
 * 2. View Resolver 위임을 통한 후보 뷰 선정
 *     2.1 ContentNegotiatingViewResolver 후보 뷰 리졸버들의 우선순위(order) 를 무시하고 모든 뷰 리졸버에게 뷰 이름을 처리 할 수 있는지 문의하고 성공한 뷰를 모두 뷰 후보 목록에 추가한다.
 *     2.2 defaultViews 프로퍼티를 이용해서 디폴트 뷰가 등록되어 있다면 이는 자동으로 후보 뷰 목록에 추가한다.
 * 3. 최종 뷰 선정
 *     mediaType 과 후보 뷰 목록을 비교하여 매칭되는 뷰가 있는지 찾아본다(미디어타입 비교)
 * 
 */
public class _07_ContentNegotiatingViewResolverTest extends AbstractDispatcherServletTest {
	@Test
	public void simpleHandler() throws ServletException, IOException {
		this.setClasses(SimpleHandler.class, SimpleViewHandler.class)
				.runService("/hi");
		assertThat(this.response.getContentAsString(), is("hi"));

		this.runService("/view");
		assertThat(this.getModelAndView().getViewName(), is("view.jsp"));
	}

	@Controller
	static class SimpleHandler {
		@RequestMapping("/hi")
		@ResponseBody
		public String hi() {
			return "hi";
		}
	}

	@Controller
	static class SimpleViewHandler {
		@RequestMapping("/view")
		public String view() {
			return "view.jsp";
		}
	}

	@Test
	public void contentNego() throws ServletException, IOException {
		this.setLocations("sample/spring3/_18_view/content_negotiating_view_resolver.xml")
				.setClasses(ContentHandler.class)
				.runService("/content.json");

		assertThat(this.response.getContentType(), is("application/json"));
		assertThat(this.response.getContentAsString(), is("{\"name\":\"Toby\"}"));
	}

	@Controller
	static class ContentHandler {
		@RequestMapping("/content")
		public String content(ModelMap model) {
			model.put("name", "Toby");
			return "content";
		}
	}

}
