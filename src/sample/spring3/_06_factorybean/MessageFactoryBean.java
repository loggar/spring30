package sample.spring3._06_factorybean;

import org.springframework.beans.factory.FactoryBean;

/**
 * spring FactoryBean 이용
 * 팩토리 빈을 applicationContext 에 등록할 때 id="message" class="sample.spring3._09_factorybean.MessageFactoryBean"
 * 의 방식으로 등록한다. 이때 message bean 설정으로 생성되는 Class 는 MessageFactoryBean 이 아닌 MessageFactoryBean.getObject() 의 반환값이다.
 * 
 */
public class MessageFactoryBean implements FactoryBean<Message> {
	String text;

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public Message getObject() throws Exception {
		return Message.getInstance(text);
	}

	@Override
	public Class<?> getObjectType() {
		return Message.class;
	}

	/**
	 * Message.getInstance(text) 는 늘 새로운 인스턴스를 new 해서 반환하므로
	 * getObject 를 통해 받는 Message 인스턴스는 싱글톤이 아니다.
	 */
	@Override
	public boolean isSingleton() {
		return false;
	}

}
