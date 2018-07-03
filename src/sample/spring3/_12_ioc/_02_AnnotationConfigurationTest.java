package sample.spring3._12_ioc;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import sample.spring3._12_ioc.resource.Hello;

public class _02_AnnotationConfigurationTest {
	private String basePath = StringUtils.cleanPath(ClassUtils.classPackageAsResourcePath(getClass())) + "/";

	/**
	 * "@Component("myprinter")" 로 지정하고
	 * 사용 Bean 에서는 "@Resource private Printer printer;" 방식으로 역지정
	 * 이름으로 매핑하도록 유도하는것이 적합하다.
	 * 
	 * 예외적으로 컨텍스트가 자신에 대한 레퍼런스를 직접 제공하는 다음의 경우 "@Resource" 가 타입으로 빈을 찾게 하여도 적합하다.
	 * "@Resource ApplicationContext context;"
	 * 
	 * "@Resource" 와 같은 어노테이션으로 된 의존관계 정보를 이용해 DI 하려면 다음 방법중 한가지를 선택
	 * 1. XML:context:component-scan
	 * 2. XML:context:component-scan
	 * 3. AnnotationConfigApplicationContext | AnnotationConfigWebApplicationContext
	 */
	@Test
	public void atResource() {
		ApplicationContext ac = new GenericXmlApplicationContext(basePath + "resource.xml");

		Hello hello = ac.getBean("hello", Hello.class);

		hello.print();

		assertThat(ac.getBean("myprinter").toString(), is("Hello Spring"));
	}

	/**
	 * "@Autowired" 는 기본적으로 Type 으로 매핑하여 빈을 생성한다.
	 * 다음 3가지 형태로 지정 할 수 있다.
	 * 1. set method 와 field : 해당 필드의 타입을 이용해 후보 빈을 검색
	 * 2. 생성자 : 모든 파라미터 타입에 대해 빈을 검색 주입
	 * 3. 일반 method : XML 을 이용한 bean 설정에서는 가능하지 않은 방법으로, 생성자의 변형 형태로 메소드를 구성해 파라미터의 종류를 유연히 대처하기위해 고안됨.
	 * 일반 method 의 모든 파라미터 타입에 대해 빈을 검색 주입.
	 * 
	 * 타입 검색을 통해 자동와이어링이 실패하더라도 상관없다면, "@Resource(required=false) private Printer printer;" 방식으로 지정한다.
	 * 
	 * "@Autowired" 를 통한 collection 자동 주입
	 * 같은 타입의 빈이 하나 이상 존재할 때 그 빈들을 모두 DI(컬렉션 형태) 할 수 있다.
	 */
	@Test
	public void atAutowiredCollection() {
		ApplicationContext ac = new AnnotationConfigApplicationContext(Client.class, ServiceA.class, ServiceB.class);
		Client client = ac.getBean(Client.class);
		assertThat(client.beanBArray.length, is(2));
		assertThat(client.beanBSet.size(), is(2));
		assertThat(client.beanBMap.entrySet().size(), is(2));
		assertThat(client.beanBList.size(), is(2));
		assertThat(client.beanBCollection.size(), is(2));
	}

	// atAutowiredCollection test
	static class Client {
		@Autowired Set<Service> beanBSet;
		@Autowired Service[] beanBArray;
		@Autowired Map<String, Service> beanBMap;
		@Autowired List<Service> beanBList;
		@Autowired Collection<Service> beanBCollection;
	}

	interface Service {
	}

	static class ServiceA implements Service {
	}

	static class ServiceB implements Service {
	}

	/**
	 * "@Qualifier(한정자)" 는 타입 외의 정보를 추가해서 자동와이어링을 세밀하게 제어 할 수 있는 보조적인 방법이다.
	 * 보통 "@Resource" 는 빈-이름 으로, "@Autowired" 는 빈-타입 으로 검색을 시도하는데 이 두가지 모두 여의치 않을 경우가 있다.
	 * 이런경우 "@Qualifier" 를 지정하여 빈을 세밀화한다.
	 * "@Qualifier" 는 필드나 set method, 혹은 생성자나 method 의 파라미터에만 지정 할 수 있다.
	 * 
	 * 다음은 @Qualifier("mainService") 을 통해 의도적으로 bean 을 지정하는 예 이다.
	 */
	@Test
	public void atQualifier() {
		ApplicationContext ac = new AnnotationConfigApplicationContext(QClient.class, QServiceA.class, QServiceB.class);
		QClient qclient = ac.getBean(QClient.class);
		assertThat(qclient.service, is(QServiceA.class));
	}

	static class QClient {
		@Autowired @Qualifier("mainService") Service service;
	}

	@Qualifier("mainService")
	static class QServiceA implements Service {
	}

	static class QServiceB implements Service {
	}

	/**
	 * "@javax.inject.Qualifier" 로 DIJ qualifier 선언 후 "@javax.inject.Inject" 를 통해 DI
	 * 
	 * @javax.inject.Qualifier : 오직 다른 Qualifier 어노테이션을 정의 하는 용도로만 사용 가능하다.
	 * @javax.inject.Inject : "@Autowire" 와 비슷한 기능을 수행할 수 있으나 required 엘리먼트에 해당하는 선택기능은 없다.
	 * 
	 *                      Springd 의 Qualifier, Inject 과 javax.inject 의 이름이 같은 두 어노테이션들을 함께 사용하는것은 권장하지 않는다.
	 */
	@Test
	public void atInject() {
		ApplicationContext ac = new AnnotationConfigApplicationContext(IClient.class, IServiceA.class, IServiceB.class);
		IClient iclient = ac.getBean(IClient.class);
		assertThat(iclient.service, is(IServiceA.class));
	}

	@Retention(RetentionPolicy.RUNTIME)
	@javax.inject.Qualifier
	// DIJ qualifier
	@interface Main {
	}

	static class IClient {
		@javax.inject.Inject @Main Service service;
	}

	@Main
	static class IServiceA implements Service {
	}

	static class IServiceB implements Service {
	}

}
