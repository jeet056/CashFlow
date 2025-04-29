package com.piggymetrics.notification.service;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.piggymetrics.notification.domain.Frequency;
import com.piggymetrics.notification.domain.NotificationSettings;
import com.piggymetrics.notification.domain.NotificationType;
import com.piggymetrics.notification.domain.Recipient;
import com.piggymetrics.notification.repository.RecipientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecipientServiceImplTest {

    @InjectMocks
    private RecipientServiceImpl recipientService;

    @Mock
    private RecipientRepository repository;

    @Test
    void shouldFindByAccountName() {
        Recipient recipient = new Recipient();
        recipient.setAccountName("test");

        when(repository.findByAccountName(recipient.getAccountName())).thenReturn(recipient);
        Recipient found = recipientService.findByAccountName(recipient.getAccountName());

        assertThat(found).isEqualTo(recipient);
    }

    @Test
    void shouldFailToFindRecipientWhenAccountNameIsEmpty() {
        assertThatThrownBy(() -> recipientService.findByAccountName(""))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldSaveRecipient() {
        NotificationSettings remind = new NotificationSettings();
        remind.setActive(true);
        remind.setFrequency(Frequency.WEEKLY);
        remind.setLastNotified(null);

        NotificationSettings backup = new NotificationSettings();
        backup.setActive(false);
        backup.setFrequency(Frequency.MONTHLY);
        backup.setLastNotified(new Date());

        Recipient recipient = new Recipient();
        recipient.setEmail("test@test.com");
        recipient.setScheduledNotifications(ImmutableMap.of(
                NotificationType.BACKUP, backup,
                NotificationType.REMIND, remind
        ));

        Recipient saved = recipientService.save("test", recipient);

        verify(repository).save(recipient);
        assertThat(saved.getScheduledNotifications().get(NotificationType.REMIND).getLastNotified())
            .isNotNull();
        assertThat(saved.getAccountName()).isEqualTo("test");
    }

    @Test
    void shouldFindReadyToNotifyWhenNotificationTypeIsBackup() {
        List<Recipient> recipients = ImmutableList.of(new Recipient());
        when(repository.findReadyForBackup()).thenReturn(recipients);

        List<Recipient> found = recipientService.findReadyToNotify(NotificationType.BACKUP);
        assertThat(found).isEqualTo(recipients);
    }

    @Test
    void shouldFindReadyToNotifyWhenNotificationTypeIsRemind() {
        List<Recipient> recipients = ImmutableList.of(new Recipient());
        when(repository.findReadyForRemind()).thenReturn(recipients);

        List<Recipient> found = recipientService.findReadyToNotify(NotificationType.REMIND);
        assertThat(found).isEqualTo(recipients);
    }

    @Test
    void shouldMarkAsNotified() {
        NotificationSettings remind = new NotificationSettings();
        remind.setActive(true);
        remind.setFrequency(Frequency.WEEKLY);
        remind.setLastNotified(null);

        Recipient recipient = new Recipient();
        recipient.setAccountName("test");
        recipient.setEmail("test@test.com");
        recipient.setScheduledNotifications(ImmutableMap.of(
                NotificationType.REMIND, remind
        ));

        recipientService.markNotified(NotificationType.REMIND, recipient);
        
        assertThat(recipient.getScheduledNotifications().get(NotificationType.REMIND).getLastNotified())
            .isNotNull();
        verify(repository).save(recipient);
    }
}