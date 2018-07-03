package sample.spring3._05_proxy;

import java.util.ArrayList;
import java.util.List;

import sample.spring3._03_transaction.User;
import sample.spring3._03_transaction.UserDao;

/**
 * 고립된 TEST 용 Mock Object
 * DB-Access 하지 않고 단위테스트 위함.
 * 
 */
public class MockUserDao implements UserDao {
	List<User> users;

	/**
	 * level update 되는 user 검증용
	 */
	List<User> levelUpdateUsers;

	public MockUserDao() {
		users = new ArrayList<User>();
		levelUpdateUsers = new ArrayList<User>();
	}

	public List<User> getLevelUpdateUsers() {
		return levelUpdateUsers;
	}

	@Override
	public int update(User user) {
		User orgUser = get(user.getId());
		
		System.out.println("Org: " + orgUser);
		System.out.println("New: " + user);

		if (orgUser != null) {
			int index = users.indexOf(orgUser);
			users.remove(index);
			users.add(index, user);

			/*
			 * level update 되는 user 검증용
			 */
			if (!orgUser.getLevel().equals(user.getLevel())) {
				levelUpdateUsers.add(user);
			}

			return 1;
		} else {
			return 0;
		}
	}

	@Override
	public int delete(User user) {
		/*
		 * TEST 에 아직 사용되지 않는 메소드 호출시 Exception 발생으로 알림.
		 */
		throw new UnsupportedOperationException();
	}

	@Override
	public int add(User user) {
		users.add(user);
		return 1;
	}

	/**
	 * 실제 dao 는 db-access 된 데이터이므로, 반환후 db data 의 변경은 없을것이다.
	 * 그와 동일한 TEST 를 위해 User 의 clone 을 반환.
	 */
	@Override
	public User get(String id) {
		User result = null;

		for (User user : users) {
			if (user.getId().equals(id)) {
				result = user;
				break;
			}
		}

		if (result == null) return null;
		else {
			return result.clone();
		}
	}

	@Override
	public int deleteAll() {
		int size = users.size();
		users.clear();
		return size;
	}

	@Override
	public int getCount() {
		return users.size();
	}

	@Override
	public List<User> getAll() {
		/*
		 * 반환받은쪽에서 Iterator 를 시도할때 Iterator 되는 중 Iterator 대상의 변경은 불가능 (java.util.ConcurrentModificationException)
		 * 이경우 users 에 대해 update 가 시도 될 수 있으므로 해당 경우를 방어하기 위해
		 * users 의 복사본을 반환한다. users 의 복사본이 Iterator 되고, update 대상은 users 원본.
		 */
		List<User> list = new ArrayList<User>();

		for (User user : users) {
			list.add(user.clone());
		}

		return list;
	}

}
