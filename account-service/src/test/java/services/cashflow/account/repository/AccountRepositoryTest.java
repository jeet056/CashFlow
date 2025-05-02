package services.cashflow.account.repository;

import services.cashflow.account.domain.Account;
import services.cashflow.account.domain.Currency;
import services.cashflow.account.domain.Item;
import services.cashflow.account.domain.Saving;
import services.cashflow.account.domain.TimePeriod;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
class AccountRepositoryTest {

    @Autowired
    private AccountRepository repository;

    @Test
    void shouldFindAccountByName() {
        // given
        Account stub = getStubAccount();
        repository.save(stub);

        // when
        Account found = repository.findByName(stub.getName());

        // then
        assertThat(found)
            .isNotNull()
            .satisfies(account -> {
                assertThat(account.getLastSeen()).isEqualTo(stub.getLastSeen());
                assertThat(account.getNote()).isEqualTo(stub.getNote());
                assertThat(account.getIncomes()).hasSameSizeAs(stub.getIncomes());
                assertThat(account.getExpenses()).hasSameSizeAs(stub.getExpenses());
                assertThat(account.getSaving())
                    .usingRecursiveComparison()
                    .isEqualTo(stub.getSaving());
            });
    }

    private Account getStubAccount() {
        Saving saving = new Saving();
        saving.setAmount(new BigDecimal("1500.00"));
        saving.setCurrency(Currency.USD);
        saving.setInterest(new BigDecimal("3.32"));
        saving.setDeposit(true);
        saving.setCapitalization(false);

        Item vacation = new Item();
        vacation.setTitle("Vacation");
        vacation.setAmount(new BigDecimal("3400.00"));
        vacation.setCurrency(Currency.EUR);
        vacation.setPeriod(TimePeriod.YEAR);
        vacation.setIcon("tourism");

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

        Account account = new Account();
        account.setName("test");
        account.setNote("test note");
        account.setLastSeen(new Date());
        account.setSaving(saving);
        account.setExpenses(Arrays.asList(grocery, vacation));
        account.setIncomes(Arrays.asList(salary));

        return account;
    }
}
