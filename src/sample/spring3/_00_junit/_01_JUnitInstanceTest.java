package sample.spring3._00_junit;

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItem;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

/**
 * JUnit 이 매 "@Test" 마다 새로운 테스트Instance 를 생성하는지 에 대한 JUnit TEST
 * 
 */
public class _01_JUnitInstanceTest {
	static Set<_01_JUnitInstanceTest> testObjects = new HashSet<_01_JUnitInstanceTest>();

	@Test
	public void test1() {
		assertThat(testObjects, not(hasItem(this)));
		testObjects.add(this);
	}

	@Test
	public void test2() {
		assertThat(testObjects, not(hasItem(this)));
		testObjects.add(this);
	}

	@Test
	public void test3() {
		assertThat(testObjects, not(hasItem(this)));
		testObjects.add(this);
	}
}
