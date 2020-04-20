package dev.hyein.lecture.restapisample.config;

import dev.hyein.lecture.restapisample.account.Account;
import dev.hyein.lecture.restapisample.account.AccountRole;
import dev.hyein.lecture.restapisample.account.AccountService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class AppConfig {
    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    // 유저 저장 후 로그인 테스트
    @Bean
    public ApplicationRunner applicationRunner(){
        return new ApplicationRunner() {
            @Autowired
            AccountService accountService;
            @Override
            public void run(ApplicationArguments args) throws Exception {
                Account account = Account.builder()
                                        .email("catsarah3333@gmail.com")
                                        .password("cat")
                                        .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                                        .build();
                accountService.saveAccount(account);
            }
        };
    }

}
