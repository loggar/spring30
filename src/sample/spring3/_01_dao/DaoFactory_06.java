package sample.spring3._01_dao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DaoFactory_06 {
	@Bean
	public UserDaoInterface_01 userDao() {
		return new UserDao_06(connectionMaker());
	}

	@Bean
	protected ConnectionMaker connectionMaker() {
		return new ConnectionMakerMysql();
	}
}
