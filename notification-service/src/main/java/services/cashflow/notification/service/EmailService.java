package services.cashflow.notification.service;

import services.cashflow.notification.domain.NotificationType;
import services.cashflow.notification.domain.Recipient;

import jakarta.mail.MessagingException;
import java.io.IOException;

public interface EmailService {

	void send(NotificationType type, Recipient recipient, String attachment) throws MessagingException, IOException;

}
