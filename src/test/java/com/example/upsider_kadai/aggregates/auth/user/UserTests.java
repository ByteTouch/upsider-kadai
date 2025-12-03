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
import org.springframework.test.web.servlet.ResultActions;

import com.example.upsider_kadai.core.AbstractApiTests;

import tools.jackson.databind.json.JsonMapper;

class UserTests extends AbstractApiTests {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JsonMapper jsonMapper;

    @BeforeEach
    @AfterEach
    void cleanUp() {
        this.userRepository.deleteAll();
    }

    @Test
    void register() throws Exception {
        // when
        var reqBody = new RequestRegister("upsider", "taro", "upsider@gmail.com", "letmein");
        ResultActions res = this.mockMvc.perform(post("/users/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(this.jsonMapper.writeValueAsBytes(reqBody)));

        // then
        res.andExpectAll(
            status().isOk(),
            content().contentType(MediaType.APPLICATION_JSON));
        User resBody = this.jsonMapper.readValue(res.andReturn().getResponse().getContentAsByteArray(), User.class);
        assertSoftly(softly -> {
            softly.assertThat(resBody.getCompanyName()).isEqualTo(reqBody.companyName());
            softly.assertThat(resBody.getName()).isEqualTo(reqBody.name());
            softly.assertThat(resBody.getEmail()).isEqualTo(reqBody.email());
            // TODO encoded password
        });
    }
}
