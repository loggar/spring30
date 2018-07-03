package sample.spring3._13_jdbc;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * SimpleDao 에 org.springframework.jdbc.datasource.SimpleDriverDataSource 방식의 데이터소스 DI
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "test-simpledriverdatasource.xml")
public class _01_DataSourceTest {
	@Autowired SimpleDao simplDao;

	@Test
	public void simpleDriverDataSource_test() {
		simplDao.deleteAll();
		assertThat(simplDao.rowCount(), is(0));
	}
}
