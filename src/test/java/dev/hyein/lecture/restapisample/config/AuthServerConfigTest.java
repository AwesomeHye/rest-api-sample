package dev.hyein.lecture.restapisample.config;

import dev.hyein.lecture.restapisample.account.Account;
import dev.hyein.lecture.restapisample.account.AccountRepository;
import dev.hyein.lecture.restapisample.account.AccountRole;
import dev.hyein.lecture.restapisample.account.AccountService;
import dev.hyein.lecture.restapisample.common.AppProperties;
import dev.hyein.lecture.restapisample.common.BaseControllerTest;
import dev.hyein.lecture.restapisample.common.TestDescription;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class AuthServerConfigTest extends BaseControllerTest {
    @Autowired
    AccountService accountService;

    @Autowired
    AppProperties appProperties;

    @Test
    @TestDescription("인증 토큰을 발급받는 테스트")
    public void getAuthToken() throws Exception{

        mockMvc.perform(post("/oauth/token")
                        .with(httpBasic(appProperties.getClientId(), appProperties.getClientSecret())) // http basic 인증 사용
                        .param("grant_type", "password")
                        .param("username", appProperties.getUserUsername())
                        .param("password", appProperties.getUserPassword()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("access_token").exists())
                .andExpect(jsonPath("token_type").exists())
                .andExpect(jsonPath("expires_in").exists())
                .andExpect(jsonPath("scope").exists());
    }
}