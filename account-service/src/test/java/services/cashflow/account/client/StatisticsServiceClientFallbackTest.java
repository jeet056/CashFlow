package services.cashflow.account.client;

import services.cashflow.account.domain.Account;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "feign.hystrix.enabled=true"
})
@ExtendWith(OutputCaptureExtension.class)
class StatisticsServiceClientFallbackTest {

    @Autowired
    private StatisticsServiceClient statisticsServiceClient;

    @Test
    void shouldLogErrorWhenUpdateStatisticsFails(CapturedOutput output) {
        // when
        statisticsServiceClient.updateStatistics("test", new Account());

        // then
        assertThat(output.getOut())
            .contains("Error during update statistics for account: test");
    }
}
