package dev.hyein.lecture.restapisample.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AccountService implements UserDetailsService {
    @Autowired
    AccountRepository accountRepository;

    /**
     * 우리가 사용하는 도메인(Account)을 스프링 시큐리티가 정의한 인터페이스(UserDetails)로 변환
     *
     * @param userName
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(userName)
                                            .orElseThrow(() -> new UsernameNotFoundException(userName));
        return new User(account.getEmail(), account.getPassword(), authorities(account.getRoles()));
    }

    /**
     * 스프링 시큐리티가 원하는 인증 객체(GrantedAuthority)로 변환
     * @param roles
     * @return
     */
    private Collection<? extends GrantedAuthority> authorities(Set<AccountRole> roles) {
        return roles.stream()
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r.name()))
                .collect(Collectors.toSet());
    }
}
