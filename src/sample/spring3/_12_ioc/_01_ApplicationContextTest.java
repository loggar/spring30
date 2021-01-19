package sample.spring3._12_ioc;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import sample.spring3._12_ioc.bean.AnnotatedHello;
import sample.spring3._12_ioc.bean.AnnotatedHelloConfig;
import sample.spring3._12_ioc.bean.Hello;
import sample.spring3._12_ioc.bean.Printer;
import sample.spring3._12_ioc.bean.StringPrinter;

public class _01_ApplicationContextTest {
	private String basePath = StringUtils.cleanPath(ClassUtils.classPackageAsResourcePath(getClass())) + "/";

	/**
	 * TEST ApplicationContext
	 */
	@Test
	public void registerBean() {
		StaticApplicationContext ac = new StaticApplicationContext();
		ac.registerSingleton("hello1", Hello.class);

		Hello hello1 = ac.getBean("hello1", Hello.class);
		assertThat(hello1, is(notNullValue()));

		BeanDefinition helloDef = new RootBeanDefinition(Hello.class);
		helloDef.getPropertyValues().addPropertyValue("name", "Spring");
		ac.registerBeanDefinition("hello2", helloDef);

		Hello hello2 = ac.getBean("hello2", Hello.class);
		assertThat(hello2.sayHello(), is("Hello Spring"));
		assertThat(hello1, is(not(hello2)));

		assertThat(ac.getBeanFactory().getBeanDefinitionCount(), is(2));
	}

	/**
	 * BeanDefinition
	 */
	@Test
	public void registerBeanWithDependency() {
		StaticApplicationContext ac = new StaticApplicationContext();

		ac.registerBeanDefinition("printer", new RootBeanDefinition(StringPrinter.class));

		BeanDefinition helloDef = new RootBeanDefinition(Hello.class);
		helloDef.getPropertyValues().addPropertyValue("name", "Spring");
		helloDef.getPropertyValues().addPropertyValue("printer", new RuntimeBeanReference("printer"));
		ac.registerBeanDefinition("hello", helloDef);

		Hello hello = ac.getBean("hello", Hello.class);
		hello.print();

		assertThat(ac.getBean("printer").toString(), is("Hello Spring"));
	}

	/**
	 * GenericApplicationContext
	 */
	@Test
	public void genericApplicationContext() {
		GenericApplicationContext ac = new GenericApplicationContext();

		XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(ac);
		reader.loadBeanDefinitions("sample/spring3/_12_ioc/genericApplicationContext.xml");

		ac.refresh();

		Hello hello = ac.getBean("hello", Hello.class);
		hello.print();

		assertThat(ac.getBean("printer").toString(), is("Hello Spring"));
	}

	/**
	 * GenericXmlApplicationContext
	 */
	@Test
	public void genericXmlApplicationContext() {
		GenericApplicationContext ac = new GenericXmlApplicationContext(basePath + "genericApplicationContext.xml");

		Hello hello = ac.getBean("hello", Hello.class);
		hello.print();

		assertThat(ac.getBean("printer").toString(), is("Hello Spring"));
	}

	/**
	 * 하위 계층의 ApplicationContext 는 GenericXmlApplicationContext 로 선언될 수 없다.
	 * GenericApplicationContext(parent) 생성자, XmlBeanDefinitionReader 등의 세부설정이 필수이기 때문.
	 */
	@Test(expected = BeanCreationException.class)
	public void createContextWithoutParent() {
		@SuppressWarnings("unused")
		ApplicationContext child = new GenericXmlApplicationContext(basePath + "childContext.xml");
	}

	/**
	 * ApplicationContext 의 계층구조
	 * 하위 ApplicationContext 에게 높은 bean 설정 우선순위가 있으며, 없으면 직계부모의 ApplicationContext 탐색.
	 */
	@Test
	public void contextHierachy() {
		ApplicationContext parent = new GenericXmlApplicationContext(basePath + "parentContext.xml");

		GenericApplicationContext child = new GenericApplicationContext(parent);
		XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(child);
		reader.loadBeanDefinitions(basePath + "childContext.xml");
		child.refresh();

		Printer printer = child.getBean("printer", Printer.class);
		assertThat(printer, is(notNullValue()));

		Hello hello = child.getBean("hello", Hello.class);
		assertThat(hello, is(notNullValue()));

		hello.print();
		assertThat(printer.toString(), is("Hello Child"));
	}

	/**
	 * AnnotationConfigApplicationContext
	 * "@Component("beanName)" 가 붙은 클래스를 스캔할 패키지를 지정
	 * 생성과 동시에 자동으로 스캔 및 등록이 이루어진다.
	 * 
	 * XML 설정을 하지 않다보니 상세한 메타정보 항목을 지정 할 수 없다.
	 * 하나의 클래스가 하나의 bean 에 등록 될 수있다. (두개 이상의 이름 및 상세설정으로 등록 될 수 없다.)
	 * 
	 */
	@Test
	public void simpleBeanScanning() {
		ApplicationContext ctx = new AnnotationConfigApplicationContext("sample.spring3._12_ioc.bean");
		AnnotatedHello hello = ctx.getBean("annotatedHello", AnnotatedHello.class);
		assertThat(hello, is(notNullValue()));
	}

	/**
	 * XML 을 이용한 빈 스캐너 등록
	 * AspectJ pointcut Expression 방법이나 정규표현식같은 이름 패턴을 이용한 context:component-scan 빈 스캐너
	 */
	@Test
	public void filteredBeanScanning() {
		ApplicationContext ctx = new GenericXmlApplicationContext(basePath + "filteredScanningContext.xml");
		Hello hello = ctx.getBean("hello", Hello.class);
		assertThat(hello, is(notNullValue()));
	}

	/**
	 * Java Code 에 의한 Bean 등록
	 * "@Configuration" 클래스의 "@Bean" 메소드
	 * 빈의 이름은 첫 글자를 소문자로 바꾼 것이 사용
	 */
	@Test
	public void configurationBean() {
		ApplicationContext ctx = new AnnotationConfigApplicationContext(AnnotatedHelloConfig.class);

		AnnotatedHello hello = ctx.getBean("annotatedHello", AnnotatedHello.class);
		assertThat(hello, is(notNullValue()));

		AnnotatedHelloConfig config = ctx.getBean("annotatedHelloConfig", AnnotatedHelloConfig.class);
		assertThat(config, is(notNullValue()));

		assertThat(config.annotatedHello(), is(sameInstance(hello)));
		assertThat(config.annotatedHello(), is(config.annotatedHello()));

		System.out.println(ctx.getBean("systemProperties").getClass());
	}

	/**
	 * XML: constructor-arg 프로퍼티 설정을 이용한 생성자 주입
	 */
	@Test
	public void constructorArgName() {
		ApplicationContext ac = new GenericXmlApplicationContext(basePath + "constructorInjection.xml");

		Hello hello = ac.getBean("hello", Hello.class);
		hello.print();

		assertThat(ac.getBean("printer").toString(), is("Hello Spring"));
	}

	/**
	 * XML: Autowire 를 이용한 프로퍼티 자동 주입.
	 */
	@Test
	public void autowire() {
		ApplicationContext ac = new GenericXmlApplicationContext(basePath + "autowire.xml");

		Hello hello = ac.getBean("hello", Hello.class);
		hello.print();

		assertThat(ac.getBean("printer").toString(), is("Hello Spring"));
	}

}
