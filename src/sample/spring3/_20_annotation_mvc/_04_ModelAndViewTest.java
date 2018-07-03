package sample.spring3._20_annotation_mvc;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 자동으로 추가되는 모델 오브젝트
 *     "@ModelAttribute 모델 오브젝트 또는 커맨드 오브젝트("@RequestParam" 으로 처리하지 못하는 복합오브젝트 포함)
 *     "@ModelAttribute 메소드의 반환값
 *     Map, Model, ModelMap 파라미터
 *     BindingResult
 *     
 * 컨트롤러의 반환 Type
 *     ModelAndView
 *         Spring3.0 이전 버전에서 대표적으로 사용된 반환타입으로, "@MVC" 방식에서는 더 편리한 방법이 많아 자주 사용되지않는다. 모델과 뷰 정보를 동시에 담고있다.
 *     String
 *         뷰 이름을 반환한다. 모델정보는 모델 맵 파라미터(Model, ModelMap)로 가져와 추가해 주는 방법을 사용한다.
 *     Void
 *         RequestToViewNameResolver 전략을 통해 자동생성되는 뷰 이름이 사용된다. URL 과 뷰 이름이 일관되게 통일 할 수 있는다면 고려해 볼만 하다.
 *     모델 오브젝트
 *         반환할 오브젝트가 하나뿐이라면 단순 오브젝트를 반환할 수있다. <code>return someUser;</code> RequestToViewNameResolver 가 뷰 이름을 결정한다.
 *     Map, Model, ModelMap
 *         이 세 타입의 오브젝트가 반환되면 그 형태 그대로의 모델이 반환된다.
 *     View
 *         View 오브젝트 자체도 반환가능
 *     "@ResponseBody"
 *         컨트롤러의 반환값을 메시지컨버터가 변환하여 HttpServletResponse 의 출력 스트림에 넣어버린다.
 *         
 */
public class _04_ModelAndViewTest {
	
	@RequestMapping("/hello")
	@ResponseBody
	public String hello() {
		return "<html><body>Hello</body></html>";
	}
}
