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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.example.upsider_kadai.aggregates.auth.user.RequestRegisterUser;
import com.example.upsider_kadai.aggregates.auth.user.UserRepository;
import com.example.upsider_kadai.aggregates.auth.user.UserTests;
import com.example.upsider_kadai.core.AbstractApiTests;

import tools.jackson.databind.json.JsonMapper;

public class LoginTests extends AbstractApiTests {
    
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
    void test_login_withValidCredentials_shouldReturnJws() throws Exception {
        // given
        var email = "upsider@gmail.com";
        var password = "letmein";
        var requestRegister = new RequestRegisterUser("upsider", "upsider", email, password);
        UserTests.registerUser(this.mockMvc, this.jsonMapper, requestRegister);

        // when
        var reqBody = new RequestLogin(email, password);
        ResultActions res = this.mockMvc.perform(post("/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.jsonMapper.writeValueAsBytes(reqBody)));
        
        // then
        res.andExpectAll(
            status().isOk(),
            content().contentType(MediaType.APPLICATION_JSON));
        ResponseLogin responseLogin = this.jsonMapper.readValue(res.andReturn().getResponse().getContentAsByteArray(), ResponseLogin.class);
        String[] tokenParts = responseLogin.accessToken().split("\\.");
        assertThat(tokenParts).hasSize(3);

        assertThat(Arrays.copyOf(tokenParts, 2)).allSatisfy(s -> {
            assertThat(s).isBase64();
        });
        // 3rd part is base64-url
    }

    public static String login(MockMvc mockMvc, JsonMapper jsonMapper, RequestLogin request) throws Exception {
        ResultActions res = mockMvc.perform(post("/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonMapper.writeValueAsBytes(request)));
        ResponseLogin responseLogin = jsonMapper.readValue(res.andReturn().getResponse().getContentAsByteArray(), ResponseLogin.class);
        return responseLogin.accessToken();
    }
}
