package kr.gooroom.gpms.common.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;

import kr.gooroom.gpms.common.service.EmailService;

@Component
public class EmailServiceImpl implements EmailService {

	@Autowired
    public JavaMailSender emailSender;
	
	@Override
	public void sendSimpleMessage(String to, String subject, String text) {
		try {
			
            SimpleMailMessage smessage = new SimpleMailMessage();
            smessage.setTo(to);
            smessage.setFrom("GPMS_HELP@gooroom.kr");
            smessage.setSubject(subject);
            smessage.setText(text);
            
            MimeMessagePreparator mailMessage = mimeMessage -> {
                MimeMessageHelper message = new MimeMessageHelper(
                        mimeMessage, true, "UTF-8");
                message.setFrom("GPMS_HELP@gooroom.kr", "GPMS ADMIN");
                message.setTo(to);
                message.setSubject(subject);
                message.setText(text, true);
            };

            emailSender.send(mailMessage);
        } catch (MailException exception) {
            exception.printStackTrace();
        }
		
	}

}
