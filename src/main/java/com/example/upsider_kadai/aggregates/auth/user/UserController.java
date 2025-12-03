package com.example.upsider_kadai.aggregates.auth.user;

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
public class UserController {
    
    private final UserService userService;

    @PostMapping(ContextFilter.USER_REGISTRATION_ENDPOINT)
    public ResponseEntity<User> register(@RequestBody @Valid RequestRegister requset) {
        var entity = this.userService.register(requset.companyName(), requset.name(), requset.email(), requset.password());
        return ResponseEntity.ok(entity);
    }
}
