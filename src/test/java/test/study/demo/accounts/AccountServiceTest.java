package test.study.demo.accounts;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class AccountServiceTest {

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void findByUsername(){
        String username = "SUA01@email.com";
        String password = "SUA01";

        Account account = Account.builder()
                .email(username)
                .password(password)
                .roles(Set.of(AccountRole.ADMIN,AccountRole.USER))
                .build();
       // this.accountRepository.save(account);
        this.accountService.saveAccount(account);

        UserDetailsService userDetailsService = accountService;
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        assertThat(this.passwordEncoder.matches(password, userDetails.getPassword())).isTrue();

    }

   /* 1. @Test(expected = UsernameNotFoundException.class)*/
    @Test
    public void findByUsernameFail(){

      /* 1.  String username = "randome@mail.com";
             accountService.loadUserByUsername(username);   */

      /*  2.  String username = "randome@mail.com";

        try {
            accountService.loadUserByUsername(username);
            fail("username No Auth");
        }catch (UsernameNotFoundException e){
            assertThat(e.getMessage()).containsSequence(username);
        }*/

        String username = "randome@email.com";
        expectedException.expect(UsernameNotFoundException.class);
        expectedException.expectMessage(Matchers.containsString(username));

        accountService.loadUserByUsername(username);

    }

}
