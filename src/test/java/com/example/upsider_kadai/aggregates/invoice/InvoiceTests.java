package com.example.upsider_kadai.aggregates.invoice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistrar;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.example.upsider_kadai.aggregates.auth.login.LoginRepository;
import com.example.upsider_kadai.aggregates.auth.login.LoginTests;
import com.example.upsider_kadai.aggregates.auth.login.RequestLogin;
import com.example.upsider_kadai.aggregates.auth.user.RequestRegisterUser;
import com.example.upsider_kadai.aggregates.auth.user.UserRepository;
import com.example.upsider_kadai.aggregates.auth.user.UserTests;
import com.example.upsider_kadai.aggregates.invoice.InvoiceTests.InvoiceTestConfig;
import com.example.upsider_kadai.core.AbstractApiTests;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

@Import(InvoiceTestConfig.class)
public class InvoiceTests extends AbstractApiTests {
    
    @TestConfiguration(proxyBeanMethods = false)
    static class InvoiceTestConfig {
        @Bean
        DynamicPropertyRegistrar invoiceTestProperties() {
            return props -> {
                props.add("aggregates.invoice.fee-rate", () -> 4);
                props.add("aggregates.invoice.tax-rate", () -> 10);
            };
        }
    }

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private LoginRepository loginRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JsonMapper jsonMapper;

    @BeforeEach
    @AfterEach
    void cleanUp() {
        this.invoiceRepository.deleteAll();
        this.loginRepository.deleteAll();
        this.userRepository.deleteAll();
    }

    @Test
    void test_createInvoice_withValidInput_shouldSucceed() throws Exception {
        // given
        var email = "upsider@gmail.com";
        var password = "letmein";
        var requestRegister = new RequestRegisterUser("upsider", "upsider", email, password);
        int userId = UserTests.registerUser(this.mockMvc, this.jsonMapper, requestRegister);
        var requestLogin = new RequestLogin(email, password);
        String token = LoginTests.login(mockMvc, jsonMapper, requestLogin);

        // when
        var paymentAmount = BigDecimal.valueOf(10000);
        var paymentDueDate = LocalDate.now().plusDays(1);
        var reqBody = new RequestCreateInvoice(paymentAmount, paymentDueDate);
        ResultActions res = this.mockMvc.perform(post("/invoices")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + token)
            .content(this.jsonMapper.writeValueAsBytes(reqBody)));
        Invoice invoice = this.jsonMapper.readValue(res.andReturn().getResponse().getContentAsByteArray(), Invoice.class);

        // then
        res.andExpectAll(
            status().isOk(),
            content().contentType(MediaType.APPLICATION_JSON));
        assertSoftly(softly -> {
            softly.assertThat(invoice.getUserId()).isEqualTo(userId);
            softly.assertThat(invoice.getIssueDate()).isEqualTo(LocalDate.now());
            softly.assertThat(invoice.getPaymentAmount()).isEqualByComparingTo(paymentAmount);
            softly.assertThat(invoice.getFee()).isEqualByComparingTo(BigDecimal.valueOf(400));
            softly.assertThat(invoice.getFeeRate()).isEqualByComparingTo(BigDecimal.valueOf(4));
            softly.assertThat(invoice.getTaxAmount()).isEqualByComparingTo(BigDecimal.valueOf(40));
            softly.assertThat(invoice.getTaxRate()).isEqualByComparingTo(BigDecimal.valueOf(10));
            softly.assertThat(invoice.getPaymentDueDate()).isEqualTo(paymentDueDate);
            softly.assertThat(invoice.getTotalAmount()).isEqualByComparingTo(BigDecimal.valueOf(10440));
        });
    }

    @Test
    void test_searchInvoices_withOwnJwt_shouldReturnExpectedList() throws Exception {
        // given
        var email1 = "upsider@gmail.com";
        var password1 = "letmein";
        var requestRegister1 = new RequestRegisterUser("upsider", "upsider", email1, password1);
        int userId1 = UserTests.registerUser(this.mockMvc, this.jsonMapper, requestRegister1);
        var requestLogin1 = new RequestLogin(email1, password1);
        String token1 = LoginTests.login(mockMvc, jsonMapper, requestLogin1);

        var email2 = "wan@gmail.com";
        var password2 = "letmein";
        var requestRegister2 = new RequestRegisterUser("oracle", "wan", email2, password2);
        int userId2 = UserTests.registerUser(this.mockMvc, this.jsonMapper, requestRegister2);
        var requestLogin2 = new RequestLogin(email2, password2);
        String token2 = LoginTests.login(mockMvc, jsonMapper, requestLogin2);

        var today = LocalDate.now();
        var request11 = new RequestCreateInvoice(BigDecimal.valueOf(105.5), today.plusDays(5));
        var invoice11 = createInvoice(mockMvc, jsonMapper, request11, token1);
        var request12 = new RequestCreateInvoice(BigDecimal.valueOf(705.34), today.plusDays(1));
        var invoice12 = createInvoice(mockMvc, jsonMapper, request12, token1);
        var request13 = new RequestCreateInvoice(BigDecimal.valueOf(4705), today.plusDays(6));
        var invoice13 = createInvoice(mockMvc, jsonMapper, request13, token1);
        var request14 = new RequestCreateInvoice(BigDecimal.valueOf(666), today.plusDays(2));
        var invoice14 = createInvoice(mockMvc, jsonMapper, request14, token1);
        var request15 = new RequestCreateInvoice(BigDecimal.valueOf(6454), today.plusDays(4));
        var invoice15 = createInvoice(mockMvc, jsonMapper, request15, token1);
        var request21 = new RequestCreateInvoice(BigDecimal.valueOf(807.5), today.plusDays(3));
        var invoice21 = createInvoice(mockMvc, jsonMapper, request21, token2);

        // when
        var reqBody = new RequestSearchInvoices(userId1, today.plusDays(2), today.plusDays(5), 0, 10);
        ResultActions res = this.mockMvc.perform(post("/invoices/search")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + token1)
            .content(this.jsonMapper.writeValueAsBytes(reqBody)));
        List<Invoice> resBody = this.jsonMapper.readValue(res.andReturn().getResponse().getContentAsByteArray(), new TypeReference<List<Invoice>>(){});

        // then
        res.andExpectAll(
            status().isOk(),
            content().contentType(MediaType.APPLICATION_JSON));
        assertThat(resBody)
            .usingRecursiveFieldByFieldElementComparator(RecursiveComparisonConfiguration.builder()
                .withComparatorForType(BigDecimal::compareTo, BigDecimal.class)
                .build())
            .containsExactly(invoice14, invoice15, invoice11);
    }

    public static Invoice createInvoice(MockMvc mockMvc, JsonMapper jsonMapper, RequestCreateInvoice request, String token) throws Exception {
        ResultActions res = mockMvc.perform(post("/invoices")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + token)
            .content(jsonMapper.writeValueAsBytes(request)));
        return jsonMapper.readValue(res.andReturn().getResponse().getContentAsByteArray(), Invoice.class);
    }
}
