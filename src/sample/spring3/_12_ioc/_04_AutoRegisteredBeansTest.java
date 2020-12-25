package sample.spring3._12_ioc;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;

import org.junit.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.io.ResourceLoader;

/**
 * 스프링 컨테이너 기본 등록 빈
 * 
 */
public class _04_AutoRegisteredBeansTest {
	@Test
	public void autoRegisteredBean() {
		ApplicationContext ac = new AnnotationConfigApplicationContext(SystemBean.class);
		SystemBean bean = ac.getBean(SystemBean.class);
		assertThat(bean.applicationContext, is(ac));

		System.out.println("[osname] " + bean.osname);
		System.out.println("[path] " + bean.path);

		System.out.println("[systemProperties] " + bean.systemProperties);
		System.out.println("[systemEnvironment] " + bean.systemEnvironment);

		org.springframework.core.io.Resource resource = bean.resourceLoader.getResource("WEB-INF/web.xml");
		System.out.println("[resource.exists()] " + resource.exists());
	}

	/**
	 * applicationContext, beanFactory, resourceLoader, applicationEventPublisher, systemProperties, systemEnvironment 위 bean 들은 스프링 컨데이너가 초기화 과정에서 기본적으로 등록해준 빈이다. 자주 사용하지는 않겠지만 간혹 필요할 때가 있으니 기억해둔다.
	 * 
	 */
	static class SystemBean {
		@Resource ApplicationContext applicationContext;
		@Autowired BeanFactory beanFactory;
		@Autowired ResourceLoader resourceLoader;
		@Autowired ApplicationEventPublisher applicationEventPublisher;

		@Value("#{systemProperties['os.name']}") String osname;
		@Value("#{systemEnvironment['Path']}") String path;

		@Resource Properties systemProperties;
		@SuppressWarnings("rawtypes")
		@Resource Map systemEnvironment;
	}

}
