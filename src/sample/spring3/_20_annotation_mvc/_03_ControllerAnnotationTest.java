package sample.spring3._20_annotation_mvc;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.util.NestedServletException;

import sample.spring3._16_webtest.AbstractDispatcherServletTest;

/**
 * "@Controller" 의 메소드 파라미터 로 사용되는 것들
 *     httpServletRequest
 *     HttpServletResponse
 *     HttpSession
 *     WebRequest
 *     NativeWebRequest
 *     Locale
 *     InputStream, Reader
 *     OutputStream, Writer
 *     "@PathVariable"
 *     "@RequestParam"
 *     "@CookieValue"
 *     "@RequestHeader"
 *     Map, Model, ModelMap
 *     "@ModelAttribute"
 *     SessionStatus
 *     "@RequestBody"
 *     "@Value"
 *     "@Valid"
 *     
 */
public class _03_ControllerAnnotationTest extends AbstractDispatcherServletTest {
	/*
	 * "@RequestMapping" 을 이용하는 경우, 컨트롤러 메소드의 파라미터로
	 * "@RequestParam", "@CookieValue" 등을 사용
	 * required, defaultValue 등의 속성을 지정 할 수 있다.
	 * 
	 * "@RequestParam" 은 스프링의 내장 변환기가 다룰 수 있는 모든 타입을 지원한다.
	 * public String view(@RequestParam("file") MultipartFile file) { ... }
	 * 
	 * "@RequestParam" 을 사용할 변수이름과 넘어온 파라미터 이름이 같다면 파라미터 이름을 생략 할 수 있다
	 * public void view(@RequestParam int id) { ... }
	 */
	@Test
	public void simple() throws ServletException, IOException {
		setClasses(ViewResolver.class, SimpleController.class);
		runService("/hello").assertViewName("hello");
		initRequest("/complex").addParameter("name", "Spring");
		// request.setCookies(new Cookie("auth", "ABCD"));
		runService();
		assertViewName("myview");
		assertModel("info", "Spring/NONE");

	}

	@RequestMapping
	static class SimpleController {
		@RequestMapping("/hello")
		public void hello() {
		}

		@RequestMapping("/complex")
		public String complex(@RequestParam("name") String name,
				@CookieValue(value = "auth", required = false, defaultValue = "NONE") String auth,
				ModelMap map) {
			map.put("info", name + "/" + auth);
			return "myview";
		}
	}

	static class ViewResolver extends InternalResourceViewResolver {
		{
			setSuffix(".jsp");
		}
	}

	/*
	 * "@PathVariable"
	 */
	@Test
	public void pathvar() throws ServletException, IOException {
		setClasses(PathController.class);
		runService("/hello/toby/view/1").assertViewName("toby/1");
		runService("/hello/toby/view/badtype");
		assertThat(response.getStatus(), is(400));
	}

	@RequestMapping
	static class PathController {
		@RequestMapping("/hello/{user}/view/{id}")
		public String pathvar(@PathVariable("user") String user, @PathVariable("id") int id) {
			return user + "/" + id;
		}
	}

	/*
	 * @RequestParam Map
	 *     request parameter 를 Map 에 자동으로 매핑
	 *     
	 * @ModelAttribute("returnModelName") TargetType targetName - parameter level
	 *     TargetType 의 필드 이름에 맞추어 targetName 오브젝트에 파라미터를 set
	 *     Return Model 의 이름은 returnModelName 으로 지정.
	 *     
	 *     @RequestParam 는 변환불가능한 탕빙의 요청 파라미터가 들어오면 HTTP 예외발생, 컨트롤러는 수행되지 않음
	 *     @ModelAttribute 는 검증작업을 거쳐 예외발생시 해당 예외를 컨트롤러에게 전달. ModelAttribute 바로 뒤에 Errors, 혹은 BindingResult 파라미터를 명시.
	 *     public String hello(@ModelAttribute("user") User user, Errors errors) { ... }
	 *     public String add(@ModelAttribute User user, BindingResult bindingResult) { ... }
	 *     
	 * @RequestParam, @ModelAttribute 는 생략이 가능하다.
	 * 스프링이 적당히 매핑한다. (보통 단순타입은 @RequestParam, 복합타입은 @ModelAttribute)
	 * 그러나 의도한 대로의 매핑을 보장받기 위해서라면 명시 해 주는것이 좋겠다.
	 *     
	 */
	@Test
	public void requestParamAndModelAttribute() throws ServletException, IOException {
		setClasses(RequestParamController.class);
		initRequest("/hello").addParameter("id", "10").runService().assertViewName("10");
		initRequest("/hello2").addParameter("id", "11").runService().assertViewName("11");
		initRequest("/hello3").addParameter("id", "12").addParameter("name", "Spring");
		runService().assertViewName("12/Spring");
		initRequest("/hello").runService();
		assertThat(response.getStatus(), is(400)); // no required param
		runService("/hello2").assertViewName("-1"); // with default param
		initRequest("/hello4").addParameter("id", "15").runService().assertViewName("15");

		initRequest("/hello5").addParameter("id", "1").addParameter("name", "Spring");
		runService().assertViewName("1/Spring");
		assertThat(getModelAndView().getModel().get("user"), is(notNullValue()));

		initRequest("/hello").addParameter("id", "bad").runService();
		assertThat(response.getStatus(), is(400));
		initRequest("/hello5").addParameter("id", "bad").addParameter("name", "Spring");
		try {
			runService().assertViewName("bad/Spring");
			fail();
		} catch (NestedServletException e) {
			assertThat(e.getCause(), is(BindException.class));
		}
		initRequest("/hello6").addParameter("id", "bad").addParameter("name", "Spring").runService();
		assertThat(response.getStatus(), is(200));
		assertThat(getModelAndView().getViewName(), is("id"));

	}
	
	@RequestMapping
	static class RequestParamController {
		@RequestMapping("/hello")
		public String hello(@RequestParam("id") int id) {
			return "" + id;
		}

		@RequestMapping("/hello2")
		public String hello2(@RequestParam(required = false, defaultValue = "-1") int id) {
			return "" + id;
		}

		@RequestMapping("/hello3")
		public String hello3(@RequestParam Map<String, String> params) {
			return params.get("id") + "/" + params.get("name");
		}

		@RequestMapping("/hello4")
		public String hello4(int id) {
			return "" + id;
		}

		@RequestMapping("/hello5")
		public String hello5(@ModelAttribute("user") User user) {
			return user.id + "/" + user.name;
		}

		@RequestMapping("/hello6")
		public String hello6(@ModelAttribute("user") User user, Errors errors) {
			return errors.getFieldErrors().get(0).getField();
		}
	}

	static class User {
		public User() {
		}

		public User(int id, String name) {
			this.id = id;
			this.name = name;
		}

		int id;
		String name;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

	/*
	 * 컨트롤러 메소드의 파라미터로 Model / ModelMap 이용
	 * addAttribute("name", object)
	 * addAttribute(object) = addAttribute("objectName", Object);
	 * 
	 */
	@Test
	public void modelmap() throws ServletException, IOException {
		setClasses(ModelController.class);
		assertThat(runService("/hello.do").getModelAndView().getModel().get("user"), is(notNullValue()));
		assertThat(getModelAndView().getModel().get("us"), is(notNullValue()));
	}

	@RequestMapping
	static class ModelController {
		@RequestMapping("/hello")
		public void hello(ModelMap model) {
			User u = new User(1, "Spring");
			model.addAttribute(u);
			model.addAttribute("us", u);
			model.addAttribute("Spring");
		}
	}

	/*
	 * 자동등록 Model
	 * method level @ModelAttribute("modelName")
	 */
	@Test
	public void autoAddedModel() throws ServletException, IOException {
		setClasses(ReturnController.class);

		initRequest("/hello1").addParameter("id", "1").addParameter("name", "Spring").runService();
		assertViewName("hello1.jsp");

		// for(String name : getModelAndView().getModel().keySet()) { System.out.println(name); System.out.println(getModelAndView().getModel().get(name).getClass());}
		assertModel("mesg", "hi"); // 자동등록 command model
		assertModel("string", "string"); // 자동등록 parameter model
		assertModel("ref", "data"); // 자동등록 @ModelAttribute method
		assertThat(getModelAndView().getModel().size(), is(5)); // BindResult 도 자동등록

		assertThat(((User) getModelAndView().getModel().get("user")).getId(), is(1));
		assertThat(((User) getModelAndView().getModel().get("user")).getName(), is("Spring"));

		runService("/hello2.do").assertModel("name", "spring");
		runService("/hello3.do").assertModel("name", "spring");
		runService("/hello4.do").assertModel("name", "spring");
	}

	@Controller
	static class ReturnController {
		/*
		 * "ref" 라는 이름의 참조정보가 된다. 자동으로 Model 에 등록된다.
		 */
		@ModelAttribute("ref")
		public String ref() {
			return "data";
		}

		@RequestMapping("/hello1")
		public ModelAndView hello1(User u, Model model) {
			model.addAttribute("string");
			return new ModelAndView("hello1.jsp").addObject("mesg", "hi");
		}

		@RequestMapping("/hello2")
		public Model hello2() {
			return new ExtendedModelMap().addAttribute("name", "spring");
		}

		@RequestMapping("/hello3")
		public ModelMap hello3() {
			return new ModelMap().addAttribute("name", "spring");
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		@RequestMapping("/hello4")
		public Map hello4() {
			Map map = new HashMap();
			map.put("name", "spring");
			return map;
		}
	}

	/*
	 * @Value
	 *     시스템 프로퍼티나 다른 빈의 프로퍼티 값, 
	 *     또는 좀더 복잡한 SpEL 을 이용해 클래스의 상수를 읽어오거나 특정 메소드를 호출 한 결과 값, 조건식 등을 넣을 수 있다.
	 */
	@Test
	public void value() throws ServletException, IOException {
		setClasses(ValueController.class);
		runService("/hello.do").assertViewName(System.getProperty("os.name"));
	}

	@Controller
	static class ValueController {
		@RequestMapping("/hello")
		public String hello(@Value("#{systemProperties['os.name']}") String osName) {
			return osName;
		}
	}
}
