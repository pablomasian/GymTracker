package es.udc.fi.dc.fd.rest.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

class JwtFilterUnitTest {

    @Mock
    private JwtGenerator jwtGenerator;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private AutoCloseable mocks;

    private JwtFilter jwtFilter;

    @BeforeEach
    void setUp() throws Exception {
        mocks = MockitoAnnotations.openMocks(this);
        jwtFilter = new JwtFilter();
        // inject mock jwtGenerator via reflection
        Field f = JwtFilter.class.getDeclaredField("jwtGenerator");
        f.setAccessible(true);
        f.set(jwtFilter, jwtGenerator);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() throws Exception {
        SecurityContextHolder.clearContext();
        mocks.close();
    }

    @Test
    void whenNoAuthorizationHeader_thenContinueFilterChainAndNoAuthSet() throws Exception {
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNull();
    }

    @Test
    void whenValidBearer_thenSetRequestAttributesAndAuthentication() throws Exception {
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer my-token");
        JwtInfo info = new JwtInfo(42L, "alice", "USER");
        when(jwtGenerator.getInfo("my-token")).thenReturn(info);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(request, times(1)).setAttribute("serviceToken", "my-token");
        verify(request, times(1)).setAttribute("userId", 42L);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNotNull();
        assertThat(auth.getName()).isEqualTo("alice");
        assertThat(auth.getAuthorities()).anyMatch(a -> a.getAuthority().equals("ROLE_USER"));
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void whenJwtGeneratorThrows_thenRespondUnauthorizedAndDoNotContinue() throws Exception {
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer bad-token");
        when(jwtGenerator.getInfo("bad-token")).thenThrow(new RuntimeException("bad"));

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(filterChain, never()).doFilter(request, response);
    }

}
