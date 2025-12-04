package com.example.upsider_kadai.aggregates.auth.user;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.example.upsider_kadai.core.AbstractApiTests;

import tools.jackson.databind.json.JsonMapper;

public class UserTests extends AbstractApiTests {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JsonMapper jsonMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    @AfterEach
    void cleanUp() {
        this.userRepository.deleteAll();
    }

    @Test
    void test_registerUser_withValidInfo_shouldSucceed() throws Exception {
        // when
        var reqBody = new RequestRegisterUser("upsider", "taro", "upsider@gmail.com", "letmein");
        ResultActions res = this.mockMvc.perform(post("/users/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.jsonMapper.writeValueAsBytes(reqBody)));

        // then
        res.andExpectAll(
            status().isOk(),
            content().contentType(MediaType.APPLICATION_JSON));
        User user = this.jsonMapper.readValue(res.andReturn().getResponse().getContentAsByteArray(), User.class);
        assertSoftly(softly -> {
            softly.assertThat(user.getCompanyName()).isEqualTo(reqBody.companyName());
            softly.assertThat(user.getName()).isEqualTo(reqBody.name());
            softly.assertThat(user.getEmail()).isEqualTo(reqBody.email());
            softly.assertThat(user.getEncodedPassword()).isNotEqualTo(reqBody.password());
            softly.assertThat(this.passwordEncoder.matches(reqBody.password(), user.getEncodedPassword())).isTrue();
        });
    }

    public static int registerUser(MockMvc mockMvc, JsonMapper jsonMapper, RequestRegisterUser request) throws Exception {
        var reqBody = new RequestRegisterUser(request.companyName(), request.name(), request.email(), request.password());
        ResultActions res = mockMvc.perform(post("/users/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonMapper.writeValueAsBytes(reqBody)));
        User user = jsonMapper.readValue(res.andReturn().getResponse().getContentAsByteArray(), User.class);
        return user.getId();
    }
}
