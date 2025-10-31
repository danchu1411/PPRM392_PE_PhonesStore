package com.dangc.prm92_pe_phonesstore;

import android.app.Application;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import com.dangc.prm92_pe_phonesstore.data.entity.User;
import com.dangc.prm92_pe_phonesstore.data.repository.UserRepository;
import com.dangc.prm92_pe_phonesstore.viewmodel.AuthViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.concurrent.Future;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AuthViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private Application application;

    @Mock
    private UserRepository userRepository;

    private AuthViewModel authViewModel;

    @Before
    public void setUp() {
        authViewModel = new AuthViewModel(application, userRepository);
    }

    @Test
    public void login_success_should_save_session() throws Exception {
        // Arrange
        String email = "test@example.com";
        String password = "password";
        User fakeUser = new User("Test", email, password);
        Future<User> fakeFuture = mock(Future.class);

        when(userRepository.login(email, password)).thenReturn(fakeFuture);
        when(fakeFuture.get()).thenReturn(fakeUser);

        // Act
        authViewModel.login(email, password);

        // Allow time for the background thread to execute
        Thread.sleep(500);

        // Assert
        verify(userRepository).saveLoginSession(fakeUser);
    }
}