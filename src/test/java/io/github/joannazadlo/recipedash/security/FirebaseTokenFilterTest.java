package io.github.joannazadlo.recipedash.security;

import io.github.joannazadlo.recipedash.mapper.UserMapper;
import io.github.joannazadlo.recipedash.model.enums.Role;
import io.github.joannazadlo.recipedash.model.enums.Status;
import io.github.joannazadlo.recipedash.repository.UserRepository;
import io.github.joannazadlo.recipedash.repository.entity.User;
import io.github.joannazadlo.recipedash.service.FirebaseTokenService;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FirebaseTokenFilterTest {

    @Mock
    private FirebaseTokenService firebaseTokenService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private FirebaseTokenFilter filter;

    @Test
    void shouldRejectRequest_WhenNoAuthorizationHeader() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilterInternal(request, response, chain);

        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        assertTrue(response.getContentAsString().contains("Authorization token is missing"));
        verifyNoInteractions(firebaseTokenService, userRepository, userMapper);
    }

    @Test
    void shouldRejectBlockedUser() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer faketoken");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        FirebaseToken firebaseToken = mock(FirebaseToken.class);
        when(firebaseToken.getUid()).thenReturn("user123");
        when(firebaseTokenService.verifyIdToken("faketoken")).thenReturn(firebaseToken);

        User user = User.builder()
                .uid("user123")
                .email("test@test.com")
                .role(Role.USER)
                .status(Status.BLOCKED)
                .build();
        when(userRepository.findById("user123")).thenReturn(Optional.of(user));

        filter.doFilterInternal(request, response, chain);

        assertEquals(HttpServletResponse.SC_FORBIDDEN, response.getStatus());
        assertTrue(response.getContentAsString().contains("User is blocked"));
        verifyNoInteractions(userMapper);
        verify(chain, never()).doFilter(any(), any());
    }

    @Test
    void shouldRejectInvalidToken() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer invalidtoken");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        when(firebaseTokenService.verifyIdToken("invalidtoken")).thenThrow(new RuntimeException("Invalid token"));

        filter.doFilterInternal(request, response, chain);

        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        assertTrue(response.getContentAsString().contains("Invalid or expired token"));
        verifyNoInteractions(userRepository, userMapper);
        verify(chain, never()).doFilter(any(), any());
    }

    @Test
    void shouldRejectUserNotFound() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer validtoken");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        FirebaseToken firebaseToken = mock(FirebaseToken.class);
        when(firebaseToken.getUid()).thenReturn("missingUserId");
        when(firebaseTokenService.verifyIdToken("validtoken")).thenReturn(firebaseToken);

        when(userRepository.findById("missingUserId")).thenReturn(Optional.empty());

        filter.doFilterInternal(request, response, chain);

        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        assertTrue(response.getContentAsString().contains("User not found"));
        verifyNoInteractions(userMapper);
        verify(chain, never()).doFilter(any(), any());
    }
}
