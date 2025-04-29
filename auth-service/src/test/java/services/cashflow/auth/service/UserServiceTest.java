package services.cashflow.auth.service;

import services.cashflow.auth.domain.User;
import services.cashflow.auth.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository repository;

    @Test
    void shouldCreateUser() {
        User user = new User();
        user.setUsername("name");
        user.setPassword("password");

        userService.create(user);
        verify(repository, times(1)).save(user);
    }

    @Test
    void shouldFailWhenUserAlreadyExists() {
        User user = new User();
        user.setUsername("name");
        user.setPassword("password");

        when(repository.findById(user.getUsername())).thenReturn(Optional.of(new User()));
        
        assertThrows(IllegalArgumentException.class, () -> 
            userService.create(user)
        );
    }
}
