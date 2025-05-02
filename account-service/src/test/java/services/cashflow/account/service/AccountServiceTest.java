package services.cashflow.account.service;

import services.cashflow.account.client.AuthServiceClient;
import services.cashflow.account.client.StatisticsServiceClient;
import services.cashflow.account.domain.*;
import services.cashflow.account.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @InjectMocks
    private AccountServiceImpl accountService;

    @Mock
    private StatisticsServiceClient statisticsClient;

    @Mock
    private AuthServiceClient authClient;

    @Mock
    private AccountRepository repository;

    @Test
    void shouldFindByName() {
        // given
        final Account account = new Account();
        account.setName("test");

        when(accountService.findByName(account.getName())).thenReturn(account);

        // when
        Account found = accountService.findByName(account.getName());

        // then
        assertThat(found).isEqualTo(account);
    }

    @Test
    void shouldFailWhenNameIsEmpty() {
        assertThatThrownBy(() -> accountService.findByName(""))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldCreateAccountWithGivenUser() {
        // given
        User user = new User();
        user.setUsername("test");

        // when
        Account account = accountService.create(user);

        // then
        assertThat(account)
            .satisfies(acc -> {
                assertThat(acc.getName()).isEqualTo(user.getUsername());
                assertThat(acc.getSaving())
                    .satisfies(saving -> {
                        assertThat(saving.getAmount()).isZero();
                        assertThat(saving.getCurrency()).isEqualTo(Currency.getDefault());
                        assertThat(saving.getInterest()).isZero();
                        assertThat(saving.getDeposit()).isFalse();
                        assertThat(saving.getCapitalization()).isFalse();
                    });
                assertThat(acc.getLastSeen()).isNotNull();
            });

        verify(authClient).createUser(user);
        verify(repository).save(account);
    }

    @Test
    void shouldSaveChangesWhenUpdatedAccountGiven() {
        // given
        Account update = createTestAccount();
        Account account = new Account();

        when(accountService.findByName("test")).thenReturn(account);

        // when
        accountService.saveChanges("test", update);

        // then
        assertThat(account)
            .usingRecursiveComparison()
            .ignoringFields("lastSeen")
            .isEqualTo(update);
        assertThat(account.getLastSeen()).isNotNull();

        verify(repository).save(account);
        verify(statisticsClient).updateStatistics("test", account);
    }

    @Test
    void shouldFailWhenNoAccountsExistedWithGivenName() {
        // given
        final Account update = new Account();
        update.setIncomes(Arrays.asList(new Item()));
        update.setExpenses(Arrays.asList(new Item()));

        when(accountService.findByName("test")).thenReturn(null);

        // when/then
        assertThatThrownBy(() -> accountService.saveChanges("test", update))
            .isInstanceOf(IllegalArgumentException.class);
    }

    private Account createTestAccount() {
        Item grocery = new Item();
        grocery.setTitle("Grocery");
        grocery.setAmount(new BigDecimal("10.00"));
        grocery.setCurrency(Currency.USD);
        grocery.setPeriod(TimePeriod.DAY);
        grocery.setIcon("meal");

        Item salary = new Item();
        salary.setTitle("Salary");
        salary.setAmount(new BigDecimal("9100.00"));
        salary.setCurrency(Currency.USD);
        salary.setPeriod(TimePeriod.MONTH);
        salary.setIcon("wallet");

        Saving saving = new Saving();
        saving.setAmount(new BigDecimal("1500.00"));
        saving.setCurrency(Currency.USD);
        saving.setInterest(new BigDecimal("3.32"));
        saving.setDeposit(true);
        saving.setCapitalization(false);

        Account account = new Account();
        account.setName("test");
        account.setNote("test note");
        account.setIncomes(Arrays.asList(salary));
        account.setExpenses(Arrays.asList(grocery));
        account.setSaving(saving);

        return account;
    }
}
