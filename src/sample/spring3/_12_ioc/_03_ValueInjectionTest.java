package sample.spring3._12_ioc;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;

public class _03_ValueInjectionTest {
	/**
	 * "@Value" 는 주요 용도는 자바 코드 외부의 리소스나 환경정보에 담긴 값을 사용하도록 지정해주는 데 있다.
	 */
	@Test
	public void valueInjection() {
		AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(BeanSP.class, ConfigSP.class, DatabasePropertyPlaceHolder.class);
		BeanSP bean = ac.getBean(BeanSP.class);
		assertThat(bean.name, is("Windows 7"));
		assertThat(bean.username, is("Spring"));
		assertThat(bean.osname, is("Windows 7"));

		assertThat(bean.hello.name, is("Spring"));
	}

	static class BeanSP {
		@Value("#{systemProperties['os.name']}") String name;
		@Value("${database.username}") String username;
		@Value("${os.name}") String osname;
		@Autowired Hello hello;
	}

	static class ConfigSP {
		@Bean
		public Hello hello(@Value("${database.username}") String username) {
			Hello hello = new Hello();
			hello.name = username;
			return hello;
		}
	}

	static class Hello {
		String name;
	}

	static class DatabasePropertyPlaceHolder extends PropertyPlaceholderConfigurer {
		public DatabasePropertyPlaceHolder() {
			this.setLocation(new ClassPathResource("database.properties", getClass()));
		}
	}

	/**
	 * <context:property-placeholder location="classpath:sample/spring3/_12_ioc/database.properties"/>
	 * 을 통해 PropertyPlaceholderConfigurer 를 구성한 후,
	 * 
	 * @ImportResource("/sample/spring3/_12_ioc/properties.xml") 방식으로 resource 전달.
	 */
	@Test
	public void importResource() {
		AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(ConfigIR.class);
		BeanSP bean = ac.getBean(BeanSP.class);

		assertThat(bean.name, is("Windows 7"));
		assertThat(bean.username, is("Spring"));
	}

	@ImportResource("/sample/spring3/_12_ioc/properties.xml")
	@Configuration
	static class ConfigIR {
		@Bean
		public BeanSP beanSp() {
			return new BeanSP();
		}

		@Bean
		public Hello hello() {
			return new Hello();
		}
	}

	/**
	 * "@Value" 설정시의 타입 변환
	 * "@Value" 의 엘리먼트는 항상 String 이어야한다.
	 * 
	 * 아래 예제 외에 지원하는 변환타입
	 * Charset, Class, Currency, InputStream, Locale, Pattern, Resource, Timezone, URI, URL ...
	 */
	@Test
	public void propertyEditor() {
		AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(BeanPE.class);
		BeanPE bean = ac.getBean(BeanPE.class);
		assertThat(bean.charset, is(Charset.forName("UTF-8")));
		assertThat(bean.intarr, is(new int[] { 1, 2, 3 }));
		assertThat(bean.flag, is(true));
		assertThat(bean.rate, is(1.2));
		assertThat(bean.file.exists(), is(true));
	}

	static class BeanPE {
		@Value("UTF-8") Charset charset;
		@Value("1,2,3") int[] intarr;
		@Value("true") boolean flag;
		@Value("1.2") double rate;
		@Value("classpath:sample/spring3/_12_ioc/properties.xml") File file;
	}

	/**
	 * XML 에서의 컬렉션 세팅
	 * List, Set, Map, Properties 등의 bean 에 값을 주입할때,
	 * <list><value>, <map><entry>, <props><prop> ,<list><ref> 등의 방식으로 컬렉션 내용을 정의 할 수 있다.
	 * 
	 * 컬렉션을 프로퍼티의 값으로 선언하는게 아닌 독립적인 bean 으로 만들 수도 있다. 이때는 해당 <bean> 설정의 id 값이 bean 의 이름이 된다.
	 * 이런경우 util 스키마의 전용 태그를 이용한다.
	 * <util:list>, <util:set>, <util:map>, <util:properties>
	 * 
	 * <util:properties> 는 XML 에 컬렉션의 내용을 직접 등록하는 대신 프로퍼티 파일을 지정 할 수 있다.
	 * <util:propreties id="settings" location="classpath:sttings/settings.properties" />
	 */
	@Test
	public void collectionInject() {
		ApplicationContext ac = new GenericXmlApplicationContext(new ClassPathResource("collection.xml", getClass()));
		BeanC bean = ac.getBean(BeanC.class);

		assertThat(bean.nameList.size(), is(3));
		assertThat(bean.nameList.get(0), is("Spring"));
		assertThat(bean.nameList.get(1), is("IoC"));
		assertThat(bean.nameList.get(2), is("DI"));

		assertThat(bean.nameSet.size(), is(3));

		assertThat(bean.ages.get("Kim"), is(30));
		assertThat(bean.ages.get("Lee"), is(35));
		assertThat(bean.ages.get("Ahn"), is(40));

		assertThat((String) bean.settings.get("username"), is("Spring"));
		assertThat((String) bean.settings.get("password"), is("Book"));

		assertThat(bean.beans.size(), is(2));
	}

	static class BeanC {
		List<String> nameList;

		public void setNameList(List<String> names) {
			this.nameList = names;
		}

		Set<String> nameSet;

		public void setNameSet(Set<String> nameSet) {
			this.nameSet = nameSet;
		}

		Map<String, Integer> ages;

		public void setAges(Map<String, Integer> ages) {
			this.ages = ages;
		}

		Properties settings;

		public void setSettings(Properties settings) {
			this.settings = settings;
		}

		@SuppressWarnings("rawtypes") List beans;

		public void setBeans(@SuppressWarnings("rawtypes") List beans) {
			this.beans = beans;
		}
	}
}
