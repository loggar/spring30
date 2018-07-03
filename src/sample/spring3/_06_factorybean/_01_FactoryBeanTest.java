package sample.spring3._06_factorybean;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import sample.spring3._06_factorybean.Message;
import sample.spring3._06_factorybean.MessageFactoryBean;

/**
 * ApplicationContext 로부터 message bean 을 가져와 FactoryBean 이 생성하는 인스턴스를 확인
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/sample/spring3/_06_factorybean/test-FactoryBean.xml")
public class _01_FactoryBeanTest {
	/*
	 * ApplicationContext 의 bean 설정만이 Spring bean 으로 등록되는것이 아니다.
	 * ApplicationContext 자체도 Spring Bean 이다.
	 */
	@Autowired ApplicationContext context;

	@Test
	public void getMessageFromFactoryBean() {
		Object message = context.getBean("message");
		assertThat(message, is(Message.class));

		Message message2 = (Message) message;
		assertThat(message2.getText(), is("TextSample"));
	}

	/**
	 * 드물지만 FactoryBean 이 만들어주는 타겟 Bean Object 가 아닌 FactoryBean 자체를 가져오고 싶은경우
	 * 스프링은 '&' 를 bean 이름 앞에 붙여주면 FactoryBean 을 반환해준다.
	 */
	@Test
	public void getFactoryBean() {
		Object factoryBean = context.getBean("&message");
		assertThat(factoryBean, is(MessageFactoryBean.class));
	}

}
