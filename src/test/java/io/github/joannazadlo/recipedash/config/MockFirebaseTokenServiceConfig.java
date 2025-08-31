package io.github.joannazadlo.recipedash.config;

import io.github.joannazadlo.recipedash.service.FirebaseTokenService;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

@TestConfiguration
@Profile("test-e2e")
public class MockFirebaseTokenServiceConfig {

    @Bean
    public FirebaseTokenService firebaseTokenService() throws FirebaseAuthException {
        FirebaseTokenService mock = mock(FirebaseTokenService.class);

        FirebaseToken fakeToken = mock(FirebaseToken.class);
        when(fakeToken.getUid()).thenReturn("user123");
        when(fakeToken.getEmail()).thenReturn("test@test.com");

        when(mock.verifyIdToken(anyString())).thenReturn(fakeToken);

        return mock;
    }
}
