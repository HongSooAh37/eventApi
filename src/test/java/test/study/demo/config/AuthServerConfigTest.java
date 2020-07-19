package test.study.demo.config;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import test.study.demo.accounts.AccountService;
import test.study.demo.common.AppProperties;
import test.study.demo.common.BaseControllerTest;
import test.study.demo.common.TestDescription;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthServerConfigTest extends BaseControllerTest {

    @Autowired
    AccountService accountService;

    @Autowired
    AppProperties appProperties;

    @Test
    @TestDescription("인증 토큰을 발급 받는 테스트")
    public void getAuthToken() throws Exception {

/*        Account sua = Account.builder()
                .email(appProperties.getUserUsername())
                .password(appProperties.getUserPassword())
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();
        this.accountService.saveAccount(sua);*/

        this.mockMvc.perform(post("/oauth/token")
                        .with(httpBasic(appProperties.getClientId(),appProperties.getClientSecret()))
                        .param("username",appProperties.getUserUsername())     // 이부분을 정의 안해줄 경우 : missing  400 error 발생
                        .param("password",appProperties.getUserPassword())
                        .param("grant_type","password"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("access_token").exists())
                ;
    }

}