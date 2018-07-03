package sample.spring3._12_ioc;

/**
 * bean 의 초기화 방법과 제거 방법
 * 
 * 초기화: 빈 오브젝트의 생성시
 * 1. 초기화 콜백 인터페이스: InitializingBean 인터페이스를 구현해서 빈을 작성. InitializingBean.afterPropertiesSet() 메소드는 말그대로 프로퍼티 설정까지 마친후에 자동으로 호출된다.
 * 2. XML: init-method
 * 3. TargetClass @PostConstruct 메소드
 * 4. 메소드에 @Bean 정의시 @Bean(init-method="method-name") 지정
 * 
 * 제거: 컨테이너가 종료 될 때
 * 1. 제거 콜백 인터페이스: DisposableBean 인터페이스를 구현해서 destroy() 구현.
 * 2. XML: destroy-method
 * 3. TargetClass @PreDestroy 메소드
 * 4. 메소드에 @Bean 정의시 @Bean(destroyMethod="method-name") 지정
 * 
 */
public class _07_BeanLifeCycleTest {

}
