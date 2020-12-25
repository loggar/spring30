package sample.spring3._20_annotation_mvc;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import javax.servlet.ServletException;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CharsetEditor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.Formatter;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.format.support.FormattingConversionServiceFactoryBean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.ConfigurableWebBindingInitializer;
import org.springframework.web.bind.support.WebBindingInitializer;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

import sample.spring3._08_aop_tx.Level;
import sample.spring3._16_webtest.AbstractDispatcherServletTest;

/**
 * "@ModelAttribute" 가 지정된 파라미터는 
 *     1. 객체를 생성한다.(혹은  "@SessionAttributes" 에서 해당이름의 객체를 가져온다)
 *     2. 위 객체에 웹 파라미터를 바인딩한다.(실패시 BindingResult 에 바인딩 오류를 저장해서 컨트롤러로 넘겨줌)
 *     3. 객체의 프로퍼티를 검증한다.
 *     
 * 이중 바인딩 기능은 "@ModelAttribute" 외에도 "@RequestParam", "@PathVariable" 에도 적용된다.
 * 스프링은 바인딩 과정에서 필요한 변환 작업을 위해 기본적으로 두가지 종류의 API 를 제공한다.
 *     1. PropertyEditor
 *     2. Converter and Formatter
 * 
 * PropertyEditor
 *     PropertyEditor 는 기본적으로 문자열과 객체사이의 연결점으로
 *     문자열 -> setAsText() -> PropertyEditor -> getValue() -> Object
 *     의 순서로 바인딩 된다.
 *     Object = PropertyEditor.setAsText(String).getValue();
 *     String = PropertyEditor.setValue(Object).getAsString();
 *     새로운 타입의 오브젝트를 바인딩하려면 해당 타입을 상호 변환하는 PropertyEditor 타입의 클래스를 만들어서 WebDataBinder 에 등록 해줘야 한다.
 *     
 */
public class _06_BindingTest extends AbstractDispatcherServletTest {
	@Test
	public void defaultPropertyEditor() throws ServletException, IOException {
		setClasses(DefaultPEController.class);
		initRequest("/hello.do").addParameter("charset", "UTF-8");
		runService();
		assertModel("charset", Charset.forName("UTF-8"));
	}

	@Controller
	static class DefaultPEController {
		@RequestMapping("/hello")
		public void hello(@RequestParam Charset charset, Model model) {
			model.addAttribute("charset", charset);
		}
	}
	
	/*
	 * 위 hello() 에서 charset 의 바인딩은 실제로 스프링이 디폴트로 등록 해 준 CharsetEditor 에 의해 바인딩 되었다.
	 * 아래는 숨겨진 바인딩 과정을 보여준다.
	 */
	@Test
	public void charsetEditor() {
		CharsetEditor charsetEditor = new CharsetEditor();
		charsetEditor.setAsText("UTF-8");
		assertThat(charsetEditor.getValue(), is(instanceOf(Charset.class)));
		assertThat((Charset) charsetEditor.getValue(), is(Charset.forName("UTF-8")));
	}

	@Test
	public void levelPropertyEditor() {
		LevelPropertyEditor levelEditor = new LevelPropertyEditor();

		levelEditor.setValue(Level.BASIC);
		assertThat(levelEditor.getAsText(), is("1"));

		levelEditor.setAsText("3");
		assertThat((Level) levelEditor.getValue(), is(Level.GOLD));
	}

	@Test
	public void levelTypeParameter() throws ServletException, IOException {
		setClasses(SearchController.class);
		initRequest("/user/search.do").addParameter("level", "1");
		runService();
		assertModel("level", Level.BASIC);
	}

	/*
	 * AnnotationMethodHandlerAdapter 는 바인딩 작업이 필요한 작업을 만나면 WebDataBinder 를 만든다.
	 * 이 WebDataBinder 에 기본으로 등록된 PropertyEditor 외의 PropertyEditor 를 등록하려면 아래와 같이 등록할 수 있다.
	 * WebDataBinder 에 
	 */
	@Controller
	static class SearchController {
		/*
		 * "@InitBinder" 가 지시된 메소드는 파라미터를 바인딩하기 전에 자동으로 호출된다.
		 */
		@InitBinder
		public void initBinder(WebDataBinder dataBinder) {
			dataBinder.registerCustomEditor(Level.class, new LevelPropertyEditor());
		}

		@RequestMapping("/user/search")
		public void search(@RequestParam Level level, Model model) {
			model.addAttribute("level", level);
		}
	}

	/*
	 * User.Level 에 대한 바인딩을 지원 할 PropertyEditor
	 * 바인딩이 시작되면 스프링은 LevelPropertyEditor.setAsText(string-parameter) 를 호출
	 * -> (ObjectType) LevelPropertyEditor.getValue() 를 통해 해당 오브젝트를 가져옴.
	 * 
	 */
	static class LevelPropertyEditor extends PropertyEditorSupport {
		public String getAsText() {
			return String.valueOf(((Level) this.getValue()).intValue());
		}

		public void setAsText(String text) throws IllegalArgumentException {
			this.setValue(Level.valueOf(Integer.parseInt(text.trim())));
		}
	}

	@Test
	public void webBindingInitializer() throws ServletException, IOException {
		setClasses(SearchController2.class, ConfigForWebBinidngInitializer.class);
		initRequest("/user/search").addParameter("level", "2");
		runService();
		assertModel("level", Level.SILVER);
	}

	@Controller
	static class SearchController2 {
		@RequestMapping("/user/search")
		public void search(@RequestParam Level level, Model model) {
			model.addAttribute("level", level);
		}
	}
	
	/*
	 * 컨트롤러에 매번 "@InitBinder" 를 작성하기 번거로운 경우 아래처럼 AnnotationMethodHandlerAdapter 를 빈으로 등록하면서
	 * 커스텀 WebBindingInitializer 를 DI 해도 된다. Level 타입 바인딩이 필요없는 컨트롤러에도 매번 적용되는 단점이 있다.
	 * 
	 */
	@Configuration
	static class ConfigForWebBinidngInitializer {
		@Bean
		public AnnotationMethodHandlerAdapter annotationMethodHandlerAdaptor() {
			return new AnnotationMethodHandlerAdapter() {
				{
					setWebBindingInitializer(webBindingInitializer());
				}
			};
		}

		@Bean
		public WebBindingInitializer webBindingInitializer() {
			return new WebBindingInitializer() {
				public void initBinder(WebDataBinder binder, WebRequest request) {
					binder.registerCustomEditor(Level.class, new LevelPropertyEditor());
				}
			};
		}
	}

	@Test
	public void dataBinder() {
		WebDataBinder dataBinder = new WebDataBinder(null);
		dataBinder.registerCustomEditor(Level.class, new LevelPropertyEditor());
		assertThat(dataBinder.convertIfNecessary("1", Level.class), is(Level.BASIC));
	}

	/*
	 * "id", "age" 는 모두 int 타입으로 바인딩 되길 원하지만
	 * 서로 다른 PropertyEditor 를 통해 바인딩 된다.
	 * id 는 스프링의 디폴트 int 전환 PropertyEditor 를, age 는 initBinder 에서 등록한 MinMaxPropertyEditor 를 통한다.
	 */
	@Test
	public void namedPropertyEditor() throws ServletException, IOException {
		setClasses(MemberController.class);
		initRequest("/add.do").addParameter("id", "10000").addParameter("age", "10000");
		runService();
		System.out.println(getModelAndView().getModel().get("member"));
	}

	@Controller
	static class MemberController {
		/*
		 * 아래는 프로퍼티 이름으로 바인딩을 제한하는 경우이다.
		 * "age" 라는 이름을 가진 파라미터에 대해서만 MinMaxPropertyEditor(0,200) 을 수행해 int 타입으로 바인딩한다.
		 */
		@InitBinder
		public void initBinder(WebDataBinder dataBinder) {
			dataBinder.registerCustomEditor(int.class, "age", new MinMaxPropertyEditor(0, 200));
		}

		@RequestMapping("/add")
		public void add(@ModelAttribute Member member) {
		}
	}

	static class Member {
		int id;
		int age;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public int getAge() {
			return age;
		}

		public void setAge(int age) {
			this.age = age;
		}

		public String toString() {
			return "Member [age=" + age + ", id=" + id + "]";
		}

	}

	static class MinMaxPropertyEditor extends PropertyEditorSupport {
		int min = Integer.MIN_VALUE;
		int max = Integer.MAX_VALUE;

		public MinMaxPropertyEditor(int min, int max) {
			this.min = min;
			this.max = max;
		}

		public MinMaxPropertyEditor() {
		}

		public String getAsText() {
			return String.valueOf(this.getValue());
		}

		public void setAsText(String text) throws IllegalArgumentException {
			Integer val = Integer.parseInt(text);
			if (val < min) val = min;
			else if (val > max) val = max;

			setValue(val);
		}
	}

	@Test
	public void webBindingInit() throws ServletException, IOException {
		setClasses(Config.class, UserController.class);
		initRequest("/add.do", "POST");
		addParameter("id", "1").addParameter("name", "Spring").addParameter("date", "02/03/01");
		addParameter("level", "3");
		runService();
	}

	@Controller
	static class UserController {
		@RequestMapping("/add")
		public void add(@ModelAttribute User user) {
			System.out.println(user);
		}
	}

	@Configuration
	static class Config {
		@Autowired FormattingConversionService conversionService;

		@Bean
		public AnnotationMethodHandlerAdapter annotationMethodHandlerAdapter() {
			return new AnnotationMethodHandlerAdapter() {
				{
					setWebBindingInitializer(webBindingInit());
				}
			};
		}

		@Bean
		public WebBindingInitializer webBindingInit() {
			return new ConfigurableWebBindingInitializer() {
				{
					setConversionService(Config.this.conversionService);
				}
			};
		}

		@Bean
		public FormattingConversionServiceFactoryBean formattingConversionServiceFactoryBean() {
			return new FormattingConversionServiceFactoryBean() {
				{
// setConverters(new LinkedHashSet(Arrays.asList(new Converter[] {new LabelToStringConverter(), new StringToLabelConverter()}))); // convert ���
				}

				protected void installFormatters(FormatterRegistry registry) {
					super.installFormatters(registry);
					registry.addFormatterForFieldType(Level.class, new LabelStringFormatter());
				}
			};
		}

		// formatter
		static class LabelStringFormatter implements Formatter<Level> {
			public String print(Level level, Locale locale) {
				return String.valueOf(level.intValue());
			}

			public Level parse(String text, Locale locale) throws ParseException {
				return Level.valueOf(Integer.parseInt(text));
			}
		}

		// converter
		static class LevelToStringConverter implements Converter<Level, String> {
			public String convert(Level level) {
				return String.valueOf(level.intValue());
			}
		}

		static class StringToLevelConverter implements Converter<String, Level> {
			public Level convert(String text) {
				return Level.valueOf(Integer.parseInt(text));
			}
		}
	}

	static class User {
		int id;
		String name;
		Level level;
		@DateTimeFormat(pattern = "dd/yy/MM") Date date;

		public Date getDate() {
			return date;
		}

		public void setDate(Date date) {
			this.date = date;
		}

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

		public Level getLevel() {
			return level;
		}

		public void setLevel(Level level) {
			this.level = level;
		}

		@Override
		public String toString() {
			return "User [date=" + date + ", id=" + id + ", level=" + level + ", name=" + name + "]";
		}
	}
}
