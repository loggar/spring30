package sample.spring3._20_annotation_mvc;

/**
 * ValidationUtils.rejectIfEmpty(errors, "name", "field.requield");
 * 등에서 세번째 파라미터는 messages.properties 파일의 프로퍼티 key 값이다.
 * 이 키 값은 MessageCodeResolver 를 통해 더 상세한 메시지 키 값으로 확장된다.
 * 
 * MessageCodeResolver 인 DefaultMessageCodeResolver 가 메시지 키 값을 확장하는 방식은 다음과 같다.
 * 1. 에러코드.모델이름.필드이름 field.requield.user.name
 * 2. 에러코드.필드이름 field.requield.name
 * 3. 에러코드.타입이름 field.requield.User
 * 4. 에러코드 field.requield
 * 위의 순서로 우선순위가 높다.
 * 
 * 검증 작업중에 발견한 오류가 특정 필드에 속한것이 아니라면 reject() 를 사용해 모델 레벨의 글로벌 에러로 등록할수있다.
 * 이때는 필드 이름이 없으므로 DefaultMessageCodeResolver 는 다음과같은 후보 키를 만들어 준다.
 * 1. 에러코드.모델이름 invalid.data.user
 * 2. 에러코드 invalid.data
 * 
 * JSR-303 의 애노테이션을 이용한 검증 작업에서는 에러코드가 애노테이션 이름으로 지정된다는것(@제외) 을 제외하고는 위와 같다.
 * 
 * MessageSource 의 등록
 * 
 * <pre><code>
 *     <bean id="messageSource" class="org.springframework.context.support.ResourceBundleMessageSource">
 *         <property name="basename" value="messages" />
 *     </bean>
 * </code></pre>
 * 
 * ValidationUtils.rejectIfEmpty(errors, "name", Object[] {0, 10}, "field.requield");
 * 세번째 파라미터는 메시지의 파라미터로 사용 할 수 있다.
 * 
 * 디폴트 메시지 
 *     메시지 키 후보 값이 모두 message.properties 에 등록되어 있지 않을 때 사용할 디폴트 에러 메시지를 등록해 둘 수 있다.
 *     rejectValue("name", "field.required", null, "입력해주세요");
 *     위 rejectValue() 메소드의 네번째 파라미터가 디폴트 메시지이다. 생략하거나 null 로 넣을수도 있다.
 *     
 * Locale
 *     LocaleResolver 에 의해 결정된 현재의 지역정보가 적용된다.
 *     Locale.KOREAN messages_ko.properties
 *     Locale.ENGLISH message_en.properties
 *     로케일 정보에 해당하는 파일이 없다면 messages.properties
 * 
 */
public class _13_MessageCodeResolverTest {

}
