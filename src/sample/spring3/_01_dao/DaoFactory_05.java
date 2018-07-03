package sample.spring3._01_dao;

/**
 * Connection Factory 도입
 * 
 */
public class DaoFactory_05 {
	public UserDaoInterface_01 userDao() {
		ConnectionMaker connectionMaker = new ConnectionMakerMysql();
		UserDaoInterface_01 userDao = new UserDao_05(connectionMaker);
		return userDao;
	}
}
