package sample.spring3._12_ioc;

/**
 * 빈을 생성하는 기능을 제공하는 다른 방법 - 팩토리빈과 팩토리메소드 를 통해 생성
 * 
 * FactoryBean 인터페이스
 * new 나 리플렉션 API 을 이용해 생성자 호출하는 방식으로 만들수없는 오브젝트를 빈으로 등록하기 위해
 * FactoryBean 인터페이스를 구현해서 다이내믹 프록시를 생성하는 getObject() 를 구현하고 이를 팩토리 빈으로 등록
 * 
 * Static Factory Method
 * <bean id="counter" class="CounterClass" factory-method="createInstance" />
 * 해당 빈의 생성은 CounterClass.createInstance() 메소드의 반환값으로 한다.
 * 
 * Instance Factory Method
 * <bean id="logFactory" class="LogFactory" />
 * <bean id="log" factory-bean="logFactory" factory-method="createLog" />
 * 스테틱 메소드가 아닌 오브젝트의 일반 메소드를 이용해 빈 오브젝트를 생성 할 수도 있다.
 * 
 * "@Bean" 메소드
 * 메소드에 정의된 @Bean 도 일종의 Factory Method 방법이다.
 * 
 */
public class _08_FactoryBeanFactoryMethod {

}
