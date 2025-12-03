package com.example.upsider_kadai.core.context;

import java.io.IOException;
import java.text.ParseException;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import tools.jackson.databind.json.JsonMapper;

@Component
@RequiredArgsConstructor
public class ContextFilter implements Filter {

    public static final String USER_REGISTRATION_ENDPOINT = "/users/register";

    public static final String LOGIN_ENDPOINT = "/login";

    public static final String HMAC_SECRET = "UgV4moJCxcu7KVfYNrnaHHbnYiISiKSBx";

    private static JWSVerifier verifier;

    private final Set<String> NO_JWT_ENDPOINTS = Set.of(USER_REGISTRATION_ENDPOINT, LOGIN_ENDPOINT);

    private final Pattern authorizationPattern = Pattern.compile("^Bearer (?<token>[a-zA-Z0-9-._~+/]+=*)$", Pattern.CASE_INSENSITIVE);

    private final int authorizationErrorStatusCode = HttpStatus.UNAUTHORIZED.value();

    private final JsonMapper jsonMapper;

    static {
        try {
            verifier = new MACVerifier(HMAC_SECRET);
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (request instanceof HttpServletRequest httpRequest && response instanceof HttpServletResponse httpResponse) {
            String authorization = httpRequest.getHeader(HttpHeaders.AUTHORIZATION);
            if (authorization == null) {
                if (NO_JWT_ENDPOINTS.contains(httpRequest.getRequestURI())) {
                    chain.doFilter(request, response);
                    return;
                } else {
                    httpResponse.sendError(authorizationErrorStatusCode);
                    return;
                }
            }
            Matcher matcher = authorizationPattern.matcher(authorization);
            if (! matcher.matches()) {
                httpResponse.sendError(authorizationErrorStatusCode, "Unexpected authorization header format.");
                return;
            }
            String base64Token = matcher.group("token");
            JWSObject jwsObject;
            try {
                jwsObject = JWSObject.parse(base64Token);
                if (! jwsObject.verify(verifier)) {
                    httpResponse.sendError(authorizationErrorStatusCode, "Invalid signature.");
                    return;
                }
            } catch (ParseException | JOSEException ex) {
                httpResponse.sendError(authorizationErrorStatusCode, "Failed to resolve JWS.");
                return;
            }

            JwtPayload payload = this.jsonMapper.readValue(jwsObject.getPayload().toBytes(), JwtPayload.class);

            var context = new RequestContext(
                payload.companyName(),
                payload.name(),
                payload.email()
            );

            try {
                ScopedValue.where(RequestContext.SCOPED_VALUE, context)
                    .call(() -> {
                        chain.doFilter(request, response);
                        return null;
                    });
            } catch (IOException | ServletException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new RuntimeException("Unexpected exception.", ex);
            }
        } else {
            chain.doFilter(request, response);
            return;
        }
    }
}
