package com.example.upsider_kadai.aggregates.auth.user;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;

    @Transactional
    public User register(String companyName, String name, String email, String password) {
        boolean alreadyExists = this.userRepository.existsByEmail(email);
        if (alreadyExists) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_CONTENT, "This email has already been registered.");
        }
        // TODO encode password
        var user = User.create(companyName, name, email, password);
        return this.userRepository.save(user);
    }
}
