package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.exception.BusinessException;
import org.example.feign.client.UserClient;
import org.example.pojo.dto.UserAuthority;
import org.example.pojo.vo.Result;
import org.example.pojo.vo.ResultStatusEnum;
import org.example.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/2/23
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService, UserService {

    private final UserClient userClient;


    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Result<UserAuthority> authorityByPhone = userClient.getAuthorityByPhone(s);
        if (authorityByPhone.getCode() != ResultStatusEnum.SUCCESS.code()) {
            throw new BusinessException(authorityByPhone.getCode(), authorityByPhone.getMessage());
        }
        return authorityByPhone.getData();
    }
    /**
     * 登录成功会调用loadUserByUsername方法，这里返回的是UserDetails的实现类UserAuthority
     * 然后要成定向到http://localhost:8080/oauth/authorize?client_id=client&response_type=code&redirect_uri=http://www.baidu.com进行授权
     * 授权之后会得到一个code，然后再通过code获取token
     */
}
