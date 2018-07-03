package sample.spring3._13_jdbc;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.sql.SQLException;

import javax.naming.NamingException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * SimpleDao 에 JNDI/WAS DB POOL 방식의 데이터소스 DI
 * 
 * <jee:jndi-lookup id="dataSource" jndi-name="jdbc/DefaultDS" />
 * 위는 WAS 환경에서 "jdbc/DefaultDS" 라는 이름을 가진 JNDI 를 dataSource 에 DI 하는 구문이다.
 * 그런데 위 방법으로는 WAS 환경이 아닌 테스트 환경에서 테스트 할수없다.
 * 그래서 테스트 환경에서는 JVM 레벨에서 JNDI 오브젝트를 바인딩해주는 SimpleNamingContextBuilder 를 임시 구성해서 테스트한다.
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "test-jndi-datasource.xml")
public class _02_DataSourceTest {
	@Autowired SimpleDao simplDao;

	@BeforeClass
	public static void init() throws IllegalStateException, NamingException, SQLException {
		SimpleNamingContextBuilder builder = new SimpleNamingContextBuilder();
		SimpleDriverDataSource ds = new SimpleDriverDataSource(new com.mysql.jdbc.Driver(), "jdbc:mysql://localhost:3306/springbook?characterEncoding=UTF-8", "springbook", "springbookpw");
		builder.bind("jdbc/DefaultDS", ds);
		builder.activate();
	}

	@Test
	public void jndiDataSource_test() {
		simplDao.deleteAll();
		assertThat(simplDao.rowCount(), is(0));
	}
}
