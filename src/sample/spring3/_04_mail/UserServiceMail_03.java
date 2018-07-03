package sample.spring3._04_mail;

import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

/**
 * Spring MailSender 를 DI
 * 
 */
public class UserServiceMail_03 {
	protected MailSender mailSender;

	public void setMailSender(MailSender mailSender) {
		this.mailSender = mailSender;
	}

	protected void sendMailForLevelUpgradeInform(User user) {
		String from = "abc@webnlog.com";
		String subject = "Level Upgrade Infom";
		String text = user.getName() + " 님의 등급이 " + user.getLevel().name() + " 으로 Upgrade 되었습니다.";

		SimpleMailMessage msg = new SimpleMailMessage();
		msg.setTo(user.getEmail());
		msg.setFrom(from);
		msg.setSubject(subject);
		msg.setText(text);

		mailSender.send(msg);
	}
}
