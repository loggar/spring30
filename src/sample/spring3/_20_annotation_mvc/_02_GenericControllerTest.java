package sample.spring3._20_annotation_mvc;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import org.junit.Test;
import org.springframework.web.bind.annotation.RequestMapping;

import sample.spring3._16_webtest.AbstractDispatcherServletTest;

/**
 * 제너릭스와 매핑정보 상속을 이용한 컨트롤러 예시
 *     제너릭스를 통해 여러 컨트롤러 클래스들의 URL 매핑의 관례를 구성하였다.
 */
public class _02_GenericControllerTest extends AbstractDispatcherServletTest {
	@Test
	public void gencon() throws ServletException, IOException {
		setClasses(UserController.class, MemberController.class);
		runService("/user/add").assertViewName("user/add");
		runService("/user/update").assertViewName("user/update");
		runService("/user/view").assertViewName("user/view");
		runService("/user/delete").assertViewName("user/delete");
		runService("/user/list").assertViewName("user/list");

		runService("/member/add").assertViewName("member/add");
		runService("/member/update").assertViewName("member/update");
		runService("/member/view").assertViewName("member/view");
		runService("/member/delete").assertViewName("member/delete");
		runService("/member/list").assertViewName("member/list");
	}

	abstract static class GenericController<T, P, S> {
		S service;

		@RequestMapping("/add")
		public void add(T entity) {
		}

		@RequestMapping("/update")
		public void update(T entity) {
		}

		@RequestMapping("/view")
		public T view(P id) {
			return (T) null;
		}

		@RequestMapping("/delete")
		public void delete(P id) {
		}

		@RequestMapping("/list")
		public List<T> list() {
			return (List<T>) null;
		}
	}

	@RequestMapping("/user")
	static class UserController extends GenericController<User, Integer, UserService> {
	}

	static class User {
		int id;
		String name;
	}

	static class UserService {
	}

	@RequestMapping("/member")
	static class MemberController extends GenericController<Member, Integer, MemberService> {
	}

	static class Member {
		int id;
		String name;
	}

	static class MemberService {
	}

}
