package sample.spring3._04_mail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.mail.SimpleMailMessage;

/**
 * 사용자 레벨관리 업데이트가 Transaction 에 의해 rollback 되었을경우, 미리 메일을 발송해버리는 상황을 방지하기 위해 Transaction 관리 기능이 있는 mail sender 를 제작하여 이용한다. (MailSenderTransaction)
 * 
 */
public class UserServiceMail_04 {
	protected MailSenderTransaction mailSenderTransaction;

	public void setMailSenderTransaction(MailSenderTransaction mailSenderTransaction) {
		this.mailSenderTransaction = mailSenderTransaction;
	}

	protected int sendMailForLevelUpgradeInform(User[] users) {
		List<User> userList = new ArrayList<User>(Arrays.asList(users));
		return sendMailForLevelUpgradeInform(userList);
	}

	protected int sendMailForLevelUpgradeInform(List<User> users) {
		String from = "abc@webnlog.com";
		String subject = "Level Upgrade Infom";

		boolean isCommit = true;

		for (User user : users) {
			if (user.getLevel() == null) {
				isCommit = false;
				break;
			}
			
			SimpleMailMessage msg = new SimpleMailMessage();
			msg.setTo(user.getEmail());
			msg.setFrom(from);
			msg.setSubject(subject);
			msg.setText(user.getName() + " 님의 등급이 " + user.getLevel().name() + " 으로 Upgrade 되었습니다.");

			mailSenderTransaction.addMsg(msg);
		}

		if (isCommit) {
			int sendResult = mailSenderTransaction.sendAll();
			mailSenderTransaction.clearMsg();
			return sendResult;
		}
		
		mailSenderTransaction.clearMsg();
		return 0;
	}

}
