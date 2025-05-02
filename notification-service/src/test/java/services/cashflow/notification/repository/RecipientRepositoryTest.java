package services.cashflow.notification.repository;

import com.google.common.collect.ImmutableMap;
import services.cashflow.notification.domain.Frequency;
import services.cashflow.notification.domain.NotificationSettings;
import services.cashflow.notification.domain.NotificationType;
import services.cashflow.notification.domain.Recipient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
class RecipientRepositoryTest {

    @Autowired
    private RecipientRepository repository;

    @Test
    void shouldFindByAccountName() {
        NotificationSettings remind = new NotificationSettings();
        remind.setActive(true);
        remind.setFrequency(Frequency.WEEKLY);
        remind.setLastNotified(new Date(0));

        NotificationSettings backup = new NotificationSettings();
        backup.setActive(false);
        backup.setFrequency(Frequency.MONTHLY);
        backup.setLastNotified(new Date());

        Recipient recipient = new Recipient();
        recipient.setAccountName("test");
        recipient.setEmail("test@test.com");
        recipient.setScheduledNotifications(ImmutableMap.of(
                NotificationType.BACKUP, backup,
                NotificationType.REMIND, remind
        ));

        repository.save(recipient);

        Recipient found = repository.findByAccountName(recipient.getAccountName());
        assertThat(found.getAccountName()).isEqualTo(recipient.getAccountName());
        assertThat(found.getEmail()).isEqualTo(recipient.getEmail());
        assertThat(found.getScheduledNotifications()).containsExactlyEntriesOf(recipient.getScheduledNotifications());
    }

    @Test
    void shouldFindReadyForRemindWhenFrequencyIsWeeklyAndLastNotifiedWas8DaysAgo() {
        NotificationSettings remind = new NotificationSettings();
        remind.setActive(true);
        remind.setFrequency(Frequency.WEEKLY);
        remind.setLastNotified(Date.from(LocalDateTime.now().minusDays(8)
            .atZone(ZoneId.systemDefault()).toInstant()));

        Recipient recipient = new Recipient();
        recipient.setAccountName("test");
        recipient.setEmail("test@test.com");
        recipient.setScheduledNotifications(ImmutableMap.of(
                NotificationType.REMIND, remind
        ));

        repository.save(recipient);

        List<Recipient> found = repository.findReadyForRemind();
        assertThat(found).isNotEmpty();
    }

    @Test
    void shouldNotFindReadyForRemindWhenFrequencyIsWeeklyAndLastNotifiedWasYesterday() {
        NotificationSettings remind = new NotificationSettings();
        remind.setActive(true);
        remind.setFrequency(Frequency.WEEKLY);
        remind.setLastNotified(Date.from(LocalDateTime.now().minusDays(1)
            .atZone(ZoneId.systemDefault()).toInstant()));

        Recipient recipient = new Recipient();
        recipient.setAccountName("test");
        recipient.setEmail("test@test.com");
        recipient.setScheduledNotifications(ImmutableMap.of(
                NotificationType.REMIND, remind
        ));

        repository.save(recipient);

        List<Recipient> found = repository.findReadyForRemind();
        assertThat(found).isEmpty();
    }

    @Test
    void shouldNotFindReadyForRemindWhenNotificationIsNotActive() {
        NotificationSettings remind = new NotificationSettings();
        remind.setActive(false);
        remind.setFrequency(Frequency.WEEKLY);
        remind.setLastNotified(Date.from(LocalDateTime.now().minusDays(30)
            .atZone(ZoneId.systemDefault()).toInstant()));

        Recipient recipient = new Recipient();
        recipient.setAccountName("test");
        recipient.setEmail("test@test.com");
        recipient.setScheduledNotifications(ImmutableMap.of(
                NotificationType.REMIND, remind
        ));

        repository.save(recipient);

        List<Recipient> found = repository.findReadyForRemind();
        assertThat(found).isEmpty();
    }

    @Test
    void shouldFindReadyForBackupWhenFrequencyIsQuarterly() {
        NotificationSettings remind = new NotificationSettings();
        remind.setActive(true);
        remind.setFrequency(Frequency.QUARTERLY);
        remind.setLastNotified(Date.from(LocalDateTime.now().minusDays(91)
            .atZone(ZoneId.systemDefault()).toInstant()));

        Recipient recipient = new Recipient();
        recipient.setAccountName("test");
        recipient.setEmail("test@test.com");
        recipient.setScheduledNotifications(ImmutableMap.of(
                NotificationType.BACKUP, remind
        ));

        repository.save(recipient);

        List<Recipient> found = repository.findReadyForBackup();
        assertThat(found).isNotEmpty();
    }
}
