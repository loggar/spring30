package sample.spring3._12_ioc;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Provider;
import javax.servlet.ServletException;

import org.junit.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ObjectFactoryCreatingFactoryBean;
import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotatedBeanDefinitionReader;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.mock.web.MockServletContext;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AbstractRefreshableWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class _05_ScopeTest {
	/**
	 * singleton-scope 는 컨텍스트당 한 개의 빈 오브젝트만 만들어지게 한다.
	 * DI, DL(의존객체 조회) 이든 상관없이 한개의 인스턴스만을 리턴한다.
	 */
	@Test
	public void singletonScope() {
		ApplicationContext ac = new AnnotationConfigApplicationContext(SingletonBean.class, SingletonClientBean.class);

		Set<SingletonBean> bean = new HashSet<SingletonBean>();
		bean.add(ac.getBean(SingletonBean.class));
		bean.add(ac.getBean(SingletonBean.class));
		assertThat(bean.size(), is(1));

		bean.add(ac.getBean(SingletonClientBean.class).bean1);
		bean.add(ac.getBean(SingletonClientBean.class).bean2);
		assertThat(bean.size(), is(1));
	}

	static class SingletonBean {
	}

	static class SingletonClientBean {
		@Autowired SingletonBean bean1;
		@Autowired SingletonBean bean2;
	}

	/**
	 * prototype-scope 는 컨테이너에게 빈을 요청할 때마다 매번 새로운 인스턴스를 생성해준다.
	 * 빈 리턴 요청시 매번 새로운 인스턴스가 필요한 경우, 빈 설정을 통하지 않고 직접 오브젝트를 생성(new) 하면 된다.
	 * 하지만 이 오브젝트가 Spring DI 에 연관이 있는 오브젝트라면 그에 해당하는 bean 이나 property 등을 모두 생성시 세팅해줘야하는 불편함이 있다.
	 * 이를 해소하고 DI 방식의 오브젝트 생성을 하기위해 prototype-scope 빈을 사용한다.
	 * 
	 * 아래는 "@Autowired" 나 "@Resource" 를 이용해 ApplicationContext 또는 BeanFactory 를 DI 받은 후
	 * getBean() 으로 PrototypeBean 을 가져오는 예제.
	 */
	@Test
	public void prototypeScope() {
		ApplicationContext ac = new AnnotationConfigApplicationContext(PrototypeBean.class, PrototypeClientBean.class);

		Set<PrototypeBean> bean = new HashSet<PrototypeBean>();
		bean.add(ac.getBean(PrototypeBean.class));
		assertThat(bean.size(), is(1));
		bean.add(ac.getBean(PrototypeBean.class));
		assertThat(bean.size(), is(2));

		bean.add(ac.getBean(PrototypeClientBean.class).bean1);
		assertThat(bean.size(), is(3));
		bean.add(ac.getBean(PrototypeClientBean.class).bean2);
		assertThat(bean.size(), is(4));
	}

	@Component("prototypeBean")
	@Scope("prototype")
	static class PrototypeBean {
	}

	static class PrototypeClientBean {
		@Autowired PrototypeBean bean1;
		@Autowired PrototypeBean bean2;
	}

	/**
	 * ObjectFactory, ObjectFactoryCreatingFactoryBean 을 이용해 PrototypeBean 을 가져온다
	 * 
	 */
	@Test
	public void objectFactory() {
		ApplicationContext ac = new AnnotationConfigApplicationContext(PrototypeBean.class, ObjectFactoryConfig.class);
		@SuppressWarnings("unchecked")
		ObjectFactory<PrototypeBean> factoryBeanFactory = ac.getBean("prototypeBeanFactory", ObjectFactory.class);

		Set<PrototypeBean> bean = new HashSet<PrototypeBean>();
		for (int i = 1; i <= 4; i++) {
			bean.add(factoryBeanFactory.getObject());
			assertThat(bean.size(), is(i));
		}
	}

	@Configuration
	static class ObjectFactoryConfig {
		@Bean
		public ObjectFactoryCreatingFactoryBean prototypeBeanFactory() {
			ObjectFactoryCreatingFactoryBean factoryBean = new ObjectFactoryCreatingFactoryBean();
			factoryBean.setTargetBeanName("prototypeBean");
			return factoryBean;
		}
	}

	/**
	 * ServiceLocatorFactoryBean 을 이용해 PrototypeBean 을 가져온다
	 */
	@Test
	public void serviceLocatorFactoryBean() {
		ApplicationContext ac = new AnnotationConfigApplicationContext(PrototypeBean.class, ServiceLocatorConfig.class);
		PrototypeBeanFactory factory = ac.getBean(PrototypeBeanFactory.class);

		Set<PrototypeBean> bean = new HashSet<PrototypeBean>();
		for (int i = 1; i <= 4; i++) {
			bean.add(factory.getPrototypeBean());
			assertThat(bean.size(), is(i));
		}
	}

	interface PrototypeBeanFactory {
		PrototypeBean getPrototypeBean();
	}

	@Configuration
	static class ServiceLocatorConfig {
		@Bean
		public ServiceLocatorFactoryBean prototypeBeanFactory() {
			ServiceLocatorFactoryBean factoryBean = new ServiceLocatorFactoryBean();
			factoryBean.setServiceLocatorInterface(PrototypeBeanFactory.class);
			return factoryBean;
		}
	}

	/**
	 * Provider<T> 를 이용한 PrototypeBean DL
	 * Provider 인터페이스를 "@Resource, @Inject, @Autowire" 중 하나로 DI 설정만 해두면 스프링이 자동으로 <T> 를 구현한 클래스를 DI 해 준다.
	 * 
	 */
	@Test
	public void providerTest() {
		ApplicationContext ac = new AnnotationConfigApplicationContext(PrototypeBean.class, ProviderClient.class);
		ProviderClient client = ac.getBean(ProviderClient.class);

		Set<PrototypeBean> bean = new HashSet<PrototypeBean>();
		for (int i = 1; i <= 4; i++) {
			bean.add(client.prototypeBeanProvider.get());
			assertThat(bean.size(), is(i));
		}
	}

	static class ProviderClient {
		@Resource Provider<PrototypeBean> prototypeBeanProvider;
	}

	/**
	 * 스프링은 singleton, prototype 외에
	 * request, session, globalSession(포틀릿에만 존재), application 의 4가지 스코프를 제공한다. 이 네가지는 모두 웹 환경에서만 의미가 있다.
	 * 이중 application 을 제외하고는 모두 싱글톤과 다르게 독립적인 상태를 저장하고 사용하는데 필요하다(클라이언트마다 다른 인스턴스가 필요하다)
	 */
	@Test
	public void requestScope() throws ServletException, IOException {
		@SuppressWarnings("unused")
		MockServletConfig ctx = new MockServletConfig(new MockServletContext(), "spring");
		DispatcherServlet ds = new AnnotationConfigDispatcherServlet(HelloController.class, HelloService.class, RequestBean.class, BeanCounter.class);
		ds.init(new MockServletConfig());

		BeanCounter counter = ds.getWebApplicationContext().getBean(BeanCounter.class);

		ds.service(new MockHttpServletRequest("GET", "/hello"), this.response);
		assertThat(counter.addCounter, is(2));
		assertThat(counter.size(), is(1));

		ds.service(new MockHttpServletRequest("GET", "/hello"), this.response);
		assertThat(counter.addCounter, is(4));
		assertThat(counter.size(), is(2));

		for (String name : ((AbstractRefreshableWebApplicationContext) ds.getWebApplicationContext()).getBeanFactory().getRegisteredScopeNames()) {
			System.out.println(name);
		}
	}

	@SuppressWarnings("serial")
	static class AnnotationConfigDispatcherServlet extends DispatcherServlet {
		private Class<?>[] classes;

		public AnnotationConfigDispatcherServlet(Class<?>... classes) {
			super();
			this.classes = classes;
		}

		protected WebApplicationContext createWebApplicationContext(ApplicationContext parent) {
			AbstractRefreshableWebApplicationContext wac = new AbstractRefreshableWebApplicationContext() {
				protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory)
						throws BeansException, IOException {
					AnnotatedBeanDefinitionReader reader = new AnnotatedBeanDefinitionReader(beanFactory);
					reader.register(classes);
				}
			};
			wac.setServletContext(getServletContext());
			wac.setServletConfig(getServletConfig());
			wac.refresh();
			return wac;
		}
	}

	MockHttpServletResponse response = new MockHttpServletResponse();

	@RequestMapping("/")
	static class HelloController {
		@Autowired HelloService helloService;
		@Autowired Provider<RequestBean> requestBeanProvider;
		@Autowired BeanCounter beanCounter;

		@SuppressWarnings("unchecked")
		@RequestMapping("hello")
		public String hello() {
			beanCounter.addCounter++;
			beanCounter.add(requestBeanProvider.get());
			helloService.hello();
			return "";
		}
	}

	static class HelloService {
		@Autowired Provider<RequestBean> requestBeanProvider;
		@Autowired BeanCounter beanCounter;

		@SuppressWarnings("unchecked")
		public void hello() {
			beanCounter.addCounter++;
			beanCounter.add(requestBeanProvider.get());
		}
	}

	@Scope(value = "request")
	static class RequestBean {
	}

	@SuppressWarnings({ "serial", "rawtypes" })
	static class BeanCounter extends HashSet {
		int addCounter = 0;
	};
}
