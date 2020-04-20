package dev.hyein.lecture.restapisample.account;

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

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class AccountServiceTest {

    @Autowired
    AccountService accountService;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    AccountRepository accountRepository;
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void findByUserName(){
        //Given
        String email = "catsarah3333@gmail.com";
        String password = "root";
        Account account = Account.builder()
                                .email(email)
                                .password(password)
                                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                                .build();
        accountService.saveAccount(account);

        //when
        UserDetailsService userDetailsService  = accountService;
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        //then
        assertThat(passwordEncoder.matches(password, userDetails.getPassword())).isTrue();
    }

    @Test(expected = UsernameNotFoundException.class)
    public void findUserNameExpected(){
        accountService.loadUserByUsername("none@gmail.com"); // throw UsernameNotFoundException
    }

    @Test
    public void findUserNameTryCatch(){
        String userName = "none@gmail.com";
        try {
            accountService.loadUserByUsername(userName); // throw UsernameNotFoundException
            fail("supported to be failed but succeeded"); // 이 라인에 진입하는 것은 유저가 존재한단 뜻이므로 테스트 FAIL 처리
        } catch (Exception e) {
            assertThat(e instanceof UsernameNotFoundException).isTrue();
            assertThat(e.getMessage()).containsSequence(userName); // containsSequence(): 문자열들이 순서대로 위치하는지 체크
        }
    }

    @Test
    public void findUserNameExpectedException() {
        String userName = "none@gmail.com";
        //expect
        //에러발생하는 코드 위쪽에 써줘야한다.
        expectedException.expect(UsernameNotFoundException.class);
        expectedException.expectMessage(Matchers.containsString(userName));

        //given
        accountService.loadUserByUsername(userName);
    }

    }