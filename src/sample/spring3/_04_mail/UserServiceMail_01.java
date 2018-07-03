package sample.spring3._04_mail;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * javax.mail.Message, javax.mail.Session, javax.mail.Transport 를 직접 이용하는 추상화되지 않은 mail service
 * 
 */
public class UserServiceMail_01 {
	protected void sendMailForLevelUpgradeInform(User user) {
		String host = "localhost";
		String from = "abc@webnlog.com";
		String subject = "Level Upgrade Infom";
		String text = user.getName() + " 님의 등급이 " + user.getLevel().name() + " 으로 Upgrade 되었습니다.";

		Properties props = new Properties();
		props.put("mail.smtp.host", host);

		Session s = Session.getInstance(props, null);
		MimeMessage msg = new MimeMessage(s);

		try {
			msg.setFrom(new InternetAddress(from));
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(user.getEmail()));
			msg.setSubject(subject);
			msg.setText(text);
			Transport.send(msg);
		} catch (AddressException e) {
			throw new RuntimeException(e);
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}
}
