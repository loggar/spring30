package sample.spring3._19_resolver_etc;

/**
 * 현재는 apache Commons 의 FileUpload 라이브러리를 사용하는 CommonsMulipartResolver 한가지만 지원된다.
 * 
 * <pre><code>
 * <bean id="multipartResolver" class="org.springframework.web.multipart.commons.CommonsMultipartResolver">
 *     <property name="maxUploadSize" value="100000" />
 * </bean>
 * </code></pre>
 * 
 * 컨트롤러는 HttpServletRequest 를 MultipartHttpServletRequest 로 캐스팅 후에
 * 멀티파트 정보를 가진 MultipartFile 오브젝트를 가져와 사용 할 수 있다.
 * 
 */
public class MultipartResolverTest {
	
}
