package sample.spring3._01_dao;

import java.sql.Connection;
import java.sql.SQLException;

import org.springframework.jdbc.datasource.SimpleDriverDataSource;

public class ConnectionMakerMysqlXml implements ConnectionMaker {
	private SimpleDriverDataSource dataSource;

	private String driverClass;
	private String url;
	private String username;
	private String password;

	public ConnectionMakerMysqlXml() {

	}

	@SuppressWarnings("unchecked")
	@Override
	public Connection makeConnection() throws ClassNotFoundException, SQLException {
		dataSource = new SimpleDriverDataSource();
		dataSource.setDriverClass((Class<? extends java.sql.Driver>) Class.forName(driverClass));
		dataSource.setUrl(url);
		dataSource.setUsername(username);
		dataSource.setPassword(password);

		return dataSource.getConnection();
	}

	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
