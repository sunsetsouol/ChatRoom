package org.example.feign.client;

import org.example.pojo.bo.MessageBO;
import org.example.pojo.vo.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/5/22
 */
@FeignClient(value = "onmessage")
public interface MessageClient {

//    @PostMapping("/message/public")
//    Result publicMessage(MessageBO messageBO);
}
