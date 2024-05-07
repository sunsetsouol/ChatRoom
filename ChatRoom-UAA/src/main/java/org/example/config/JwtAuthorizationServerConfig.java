package org.example.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import javax.sql.DataSource;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/2/23
 */
@Configuration
@EnableAuthorizationServer
@RequiredArgsConstructor
public class JwtAuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    @Value("${SIGN_KEY}")
    private String SIGN_KEY ;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final DataSource dataSource;
    private final UserDetailsService userDetailsService;
//    private final TokenStore tokenStore;



    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        //转换器将用户信息和JWT令牌进行转换
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        jwtAccessTokenConverter.setSigningKey(SIGN_KEY);
        return jwtAccessTokenConverter;
    }

    @Bean
    public TokenStore tokenStore() {
        //使用JWT方式生成令牌
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
//                .tokenStore(tokenStore)
                .tokenStore(tokenStore())
                .accessTokenConverter(jwtAccessTokenConverter())
                .authenticationManager(authenticationManager)
                .userDetailsService(userDetailsService);

//        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
//        //令牌生成方式
//        defaultTokenServices.setTokenStore(tokenStore());
//        //是否支持刷新令牌
//        defaultTokenServices.setSupportRefreshToken(true);
//        //是否重复使用刷新令牌（直到过期）
//        defaultTokenServices.setReuseRefreshToken(true);
//        //设置客户端信息
//        defaultTokenServices.setClientDetailsService(endpoints.getClientDetailsService());
//        //控制令牌存储增强策略
//        defaultTokenServices.setTokenEnhancer(endpoints.getTokenEnhancer());
//        //访问令牌的默认有效期（秒为单位）
//        defaultTokenServices.setAccessTokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(30));
//        //刷新令牌的有效性
//        defaultTokenServices.setRefreshTokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(3));
//        //使用配置令牌服务
//        endpoints.tokenServices(defaultTokenServices);
    }

    @Bean
    //声明ClientDetails实现，从数据库中读取客户端信息
    public ClientDetailsService clientDetails() {
        JdbcClientDetailsService jdbcClientDetailsService = new JdbcClientDetailsService(dataSource);
        jdbcClientDetailsService.setPasswordEncoder(passwordEncoder);
        return jdbcClientDetailsService;
    }

    @Override
    //使用数据库方式客户端存储
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(clientDetails());
    }
}
