package org.example.feign.client;

import org.example.pojo.dto.UserAuthority;
import org.example.pojo.vo.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/4/29
 */
//@FeignClient(value = "userservice", fallbackFactory = UserClientFallbackFactory.class)
@FeignClient(value = "userservice")
public interface UserClient {

    @GetMapping("/user/inner/getAuthority")
    Result<UserAuthority> getAuthorityByPhone(@RequestParam("phone") String phone);

}
