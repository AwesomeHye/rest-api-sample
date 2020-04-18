package dev.hyein.lecture.restapisample.config;

import dev.hyein.lecture.restapisample.account.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

// Spring Security를 스프링부트 내장 설정이 아닌 커스터마이징 설정으로 적용
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    // Authentication Manager가 적용시킬 두 개 인터페이스 선안
    @Autowired
    AccountService accountService;
    @Autowired
    PasswordEncoder passwordEncoder;


    @Bean
    public TokenStore tokenStore(){
        return new InMemoryTokenStore();
    }

    // AuthorizationServer, ResourceServer 가 참조할 수 있도록 인증 매니저를 재정의 후 빈으로 등록
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    // AuthenticationManager 재정의 메소드
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(accountService)
                .passwordEncoder(passwordEncoder);
    }

    // Security Filter 적용 여부 설정
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().mvcMatchers("/docs/index.html");
        // 정적 리소스 기본 위치 는 security 검사 안 함
        web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }



 /* // Spring Security 로 들어는 오되 익명도 접근할 수 있는 요청으로 인증 처리
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .mvcMatchers("/docs/index.html").anonymous()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).anonymous()
                ;
    }*/

}

