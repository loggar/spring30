package sample.spring3._05_proxy;

import sample.spring3._03_transaction.User;

public interface UserService {
	public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
	public static final int MIN_RECCOMENDCOUNT_FOR_GOLD = 30;

	int add(User user) throws Exception;

	void upgradeLevels() throws Exception;
}
