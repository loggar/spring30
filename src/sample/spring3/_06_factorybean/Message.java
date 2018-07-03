package sample.spring3._06_factorybean;

/**
 * 다음과같은 private 생성자 Class 는 직접 Spring bean 으로 등록해서 사용 할 수 없다.
 * factorybean 을 이용해야 한다.(MessageFactoryBean 참고)
 * 
 */
public class Message {
	String text;

	private Message(String text) {
		this.text = text;
	}

	public String getText() {
		return this.text;
	}

	public static Message getInstance(String text) {
		return new Message(text);
	}
}
