package services.cashflow.auth.repository;

import services.cashflow.auth.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
class UserRepositoryTest {

    @Autowired
    private UserRepository repository;

    @Test
    void shouldSaveAndFindUserByName() {
        User user = new User();
        user.setUsername("name");
        user.setPassword("password");
        repository.save(user);

        Optional<User> found = repository.findById(user.getUsername());
        assertTrue(found.isPresent());
        assertEquals(user.getUsername(), found.get().getUsername());
        assertEquals(user.getPassword(), found.get().getPassword());
    }
}
