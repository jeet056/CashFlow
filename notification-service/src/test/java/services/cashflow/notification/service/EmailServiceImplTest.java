package services.cashflow.notification.service;

import services.cashflow.notification.domain.NotificationType;
import services.cashflow.notification.domain.Recipient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;

import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

    @InjectMocks
    private EmailServiceImpl emailService;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private Environment env;

    @Captor
    private ArgumentCaptor<MimeMessage> captor;

    @BeforeEach
    void setup() {
        when(mailSender.createMimeMessage())
                .thenReturn(new MimeMessage(Session.getDefaultInstance(new Properties())));
    }

    @Test
    void shouldSendBackupEmail() throws MessagingException, IOException {
        // given
        final String subject = "subject";
        final String text = "text";
        final String attachment = "attachment.json";

        Recipient recipient = new Recipient();
        recipient.setAccountName("test");
        recipient.setEmail("test@test.com");

        when(env.getProperty(NotificationType.BACKUP.getSubject())).thenReturn(subject);
        when(env.getProperty(NotificationType.BACKUP.getText())).thenReturn(text);
        when(env.getProperty(NotificationType.BACKUP.getAttachment())).thenReturn(attachment);

        // when
        emailService.send(NotificationType.BACKUP, recipient, "{\"name\":\"test\"}");

        // then
        verify(mailSender).send(captor.capture());
        MimeMessage message = captor.getValue();
        assertThat(message.getSubject()).isEqualTo(subject);
        assertThat(message.getAllRecipients()[0].toString()).isEqualTo(recipient.getEmail());
    }

    @Test
    void shouldSendRemindEmail() throws MessagingException, IOException {
        // given
        final String subject = "subject";
        final String text = "text";

        Recipient recipient = new Recipient();
        recipient.setAccountName("test");
        recipient.setEmail("test@test.com");

        when(env.getProperty(NotificationType.REMIND.getSubject())).thenReturn(subject);
        when(env.getProperty(NotificationType.REMIND.getText())).thenReturn(text);

        // when
        emailService.send(NotificationType.REMIND, recipient, null);

        // then
        verify(mailSender).send(captor.capture());
        MimeMessage message = captor.getValue();
        assertThat(message.getSubject()).isEqualTo(subject);
        assertThat(message.getAllRecipients()[0].toString()).isEqualTo(recipient.getEmail());
    }
}
