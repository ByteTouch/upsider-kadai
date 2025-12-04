package com.example.upsider_kadai.aggregates.auth.login;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.upsider_kadai.aggregates.auth.login.Login.AuthResult;
import com.example.upsider_kadai.aggregates.auth.user.User;
import com.example.upsider_kadai.aggregates.auth.user.UserRepository;
import com.example.upsider_kadai.core.context.ContextFilter;
import com.example.upsider_kadai.core.context.JwtPayload;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.json.JsonMapper;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginService {

    private static JWSSigner signer;

    private final LoginRepository loginRepository;

    private final UserRepository userRepository;

    private final JsonMapper jsonMapper;

    static {
        try {
            signer = new MACSigner(ContextFilter.HMAC_SECRET);
        } catch (KeyLengthException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public ResponseLogin login(String email, String password) {
        var user = this.userRepository.findByEmail(email)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email is not registered."));
        // TODO encode password
        try {
            if (password.equals(user.getEncodedPassword())) {
                String accessToken = this.generateJwsToken(user);
                this.saveLogin(user, AuthResult.SUCCESS);
                return new ResponseLogin(accessToken);
            } else {
                this.saveLogin(user, AuthResult.FAILURE);
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email or password is incorrect.");
            }
        } catch (Exception ex) {
            var message = "An error occurred during login.";
            log.error(message, ex);
            this.saveLogin(user, AuthResult.INTERNAL_ERROR);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, message);
        }
    }

    private String generateJwsToken(User user) throws JOSEException {
        var payload = new JwtPayload(user.getId(), user.getCompanyName(), user.getName(), user.getEmail());
        var jwsObject = new JWSObject(new JWSHeader(JWSAlgorithm.HS256), new Payload(this.jsonMapper.writeValueAsBytes(payload)));
        jwsObject.sign(signer);
        return jwsObject.serialize();
    }

    private void saveLogin(User user, AuthResult authResult) {
        var login = Login.create(user.getId(), authResult);
        this.loginRepository.save(login);
    }
}
