package com.example.upsider_kadai.aggregates.auth.login;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import com.example.upsider_kadai.aggregates.auth.user.RequestRegister;
import com.example.upsider_kadai.aggregates.auth.user.UserRepository;
import com.example.upsider_kadai.core.AbstractApiTests;

import tools.jackson.databind.json.JsonMapper;

class LoginTests extends AbstractApiTests {
    
    @Autowired
    private LoginRepository loginRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JsonMapper jsonMapper;

    @BeforeEach
    @AfterEach
    void cleanUp() {
        this.loginRepository.deleteAll();
        this.userRepository.deleteAll();
    }

    @Test
    void login() throws Exception {
        // given
        var email = "upsider@gmail.com";
        var password = "letmein";
        this.registerUser(email, password);

        // when
        var reqBody = new RequestLogin(email, password);
        var res = this.mockMvc.perform(post("/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.jsonMapper.writeValueAsBytes(reqBody)));
        
        // then
        res.andExpectAll(
            status().isOk(),
            content().contentType(MediaType.APPLICATION_JSON));
        ResponseLogin resBody = this.jsonMapper.readValue(res.andReturn().getResponse().getContentAsByteArray(), ResponseLogin.class);
        String[] tokenParts = resBody.accessToken().split("\\.");
        assertThat(tokenParts).hasSize(3);

        assertThat(Arrays.copyOf(tokenParts, 2)).allSatisfy(s -> {
            assertThat(s).isBase64();
        });
        // 3rd part is base64-url
    }

    private void registerUser(String email, String password) throws Exception {
        var reqBody = new RequestRegister("upsider", "taro", email, password);
        this.mockMvc.perform(post("/users/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.jsonMapper.writeValueAsBytes(reqBody)));
    }
}
