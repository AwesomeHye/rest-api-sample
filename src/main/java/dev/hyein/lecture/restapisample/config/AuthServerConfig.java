package dev.hyein.lecture.restapisample.config;

        import dev.hyein.lecture.restapisample.account.AccountService;
        import dev.hyein.lecture.restapisample.common.AppProperties;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.context.annotation.Configuration;
        import org.springframework.security.authentication.AuthenticationManager;
        import org.springframework.security.crypto.password.PasswordEncoder;
        import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
        import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
        import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
        import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
        import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
        import org.springframework.security.oauth2.provider.token.TokenStore;

@Configuration
@EnableAuthorizationServer
public class AuthServerConfig extends AuthorizationServerConfigurerAdapter {
    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AppProperties appProperties;

    // for configure(AuthorizationServerEndpointsConfigurer)
    @Autowired
    AuthenticationManager authenticationManager; // 유저 인증 정보를 가지고 있음
    @Autowired
    AccountService accountService;
    @Autowired
    TokenStore tokenStore;


    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        // client_secret을 인코딩할 password encoder 설정
        security.passwordEncoder(passwordEncoder);
    }

    /**
     * 서버에게 토큰을 요청할 클라이언트 설정
     * @param clients
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient(appProperties.getClientId())
                .secret(passwordEncoder.encode(appProperties.getClientSecret()))
                .authorizedGrantTypes("password", "refresh_token") // 이 인증 서버가 지원할 토큰 설정. refresh_token: auth token받을 때 발급받는 토큰으로 새로운 access token을 발급받는다.
                .scopes("read", "write")
                .accessTokenValiditySeconds(60 * 10)
                .refreshTokenValiditySeconds(60 * 60);
    }


    /**
     * 유저 인증 서버 (이전에 만들어둔 커스터마이징 인증 서버 적용)
     * @param endpoints
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.authenticationManager(authenticationManager)
                .userDetailsService(accountService)
                .tokenStore(tokenStore);
    }
}
