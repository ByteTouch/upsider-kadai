package com.example.upsider_kadai.aggregates.auth.login;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.upsider_kadai.core.context.ContextFilter;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@Validated
@RequiredArgsConstructor
public class LoginController {
    
    private final LoginService loginService;

    @PostMapping(ContextFilter.LOGIN_ENDPOINT)
    public ResponseEntity<ResponseLogin> login(@RequestBody @Valid RequestLogin request) {
        ResponseLogin res = this.loginService.login(request.email(), request.password());
        return ResponseEntity.ok(res);
    }

}
