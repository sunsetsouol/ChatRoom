package org.example.user.config;

import org.example.filter.TokenAuthorizationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/5/3
 */
@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
    @Value("${SIGN_KEY}")
    private String SIGN_KEY;

//    @Autowired
//    private TokenStore tokenStore;
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources
//                .tokenStore(tokenStore)
                .tokenStore(tokenStore())
                .stateless(true);
    }
    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(jwtAccessTokenConverter());
    }
    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setSigningKey(SIGN_KEY);
        return converter;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                //这个配置是对客户端的权限进行判断,目前不用配置
                //.antMatchers("/**").access("#oauth2.hasScope('all')")

                //这个表示公共资源,全部需要以/public/....进行url命名即可(即不用带token)
                .mvcMatchers("/*/public/**").permitAll()
                .mvcMatchers("/*/inner/**").permitAll()
                .mvcMatchers("/ws/**").permitAll()
                .mvcMatchers("/login/*").anonymous()
                .mvcMatchers("/*/register/**").anonymous()
                //这个表示需要携带token
                .antMatchers("/**").authenticated()
                //关闭跨站攻击
                .and().csrf().disable()
                //不使用session进行存储
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.addFilterBefore(new TokenAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
    }
}
