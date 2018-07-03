package sample.spring3._20_annotation_mvc;

import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import sample.spring3._08_aop_tx.Level;
import sample.spring3._16_webtest.AbstractDispatcherServletTest;

/**
 * 바인딩을 위한 PropertyEditor 의 설정은 결국 내부 인스턴스 필드를 필요로하는 특성때문에 스프링의 싱글톤 빈으로 존재하기 어렵다는 단점을 극복하기 힘들다.
 * 스프링에는 Converter(혹은 GernericConveroer) 라는 인터페이스가 있는데 이를 통해 변환을 구현 할 수 있다.
 * 이렇게 구현된 컨버터를 ConversionService 에 필요한 컨버터들을 등록하여 사용 할 수 있다.
 * ConversionService 는 "@InitBinder" 를 통해 필요한 경우 수동 등록할 수도 있고
 * ConfigurationWebBindingInitializer 를 통해 일괄 등록하여 사용 할 수도 있다.
 * 어차피 컨버터는 싱글톤이라서 모든 컨트롤러의 WebDataBinder 에 적용해도 별 문제가 되지 않는다.
 * 
 */
public class _08_ConverterTest extends AbstractDispatcherServletTest {
	/*
	 * GenericConversionService 를 상속받은 MyConvertsionService 를 만들어 여기에 사용자의 컨버터를 등록하고(addConverter())
	 * MyConvertsionService 를 "@InitBinder" 를 통해 컨트롤에 등록 (setConversionService()) 하여 사용하는 방법
	 */
	@Test
	public void inheritedGenericConversionService() throws ServletException, IOException {
		setClasses(SearchController.class, MyConvertsionService.class);
		initRequest("/user/search.do").addParameter("level", "1");
		runService();
		assertModel("level", Level.BASIC);
	}

	@Controller
	public static class SearchController {
		@Autowired ConversionService conversionService;

		@InitBinder
		public void initBinder(WebDataBinder dataBinder) {
			dataBinder.setConversionService(this.conversionService);
		}

		@RequestMapping("/user/search")
		public void search(@RequestParam Level level, Model model) {
			model.addAttribute("level", level);
		}
	}

	static class MyConvertsionService extends GenericConversionService {
		{
			this.addConverter(new LevelToStringConverter());
			this.addConverter(new StringToLevelConverter());
		}
	}
	
	/*
	@SuppressWarnings({ "unchecked", "rawtypes" })
	static class MyConversionServiceFactoryBean extends ConversionServiceFactoryBean {
		{
			this.setConverters(new LinkedHashSet(Arrays.asList(
					new Converter[] { new LevelToStringConverter(), new StringToLevelConverter() })));
		}
	}
	*/

	public static class LevelToStringConverter implements Converter<Level, String> {
		public String convert(Level level) {
			return String.valueOf(level.intValue());
		}
	}

	public static class StringToLevelConverter implements Converter<String, Level> {
		public Level convert(String text) {
			return Level.valueOf(Integer.parseInt(text));
		}
	}

	/*
	 * ConfigurationWebBindingInitializer 를 통해 싱글톤 빈으로 일괄 등록되어있는 컨버터를 사용한 예
	 */
	@Test
	public void compositeGenericConversionService() throws ServletException, IOException {
		setRelativeLocations("conversionservice.xml");
		setClasses(SearchController.class);
		initRequest("/user/search.do").addParameter("level", "1");
		runService();
		assertModel("level", Level.BASIC);
	}
}
