package org.example.onmessage.controller;

import com.alibaba.fastjson.JSON;
import org.example.pojo.bo.MessageBO;
import org.example.onmessage.handler.ws.GlobalWsMap;
import org.example.pojo.vo.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/5/22
 */
@RestController
@RequestMapping("/message")
public class MessageController {
    @PostMapping("/public")
    public Result publicMessage(@RequestBody MessageBO messageBO) {
        GlobalWsMap.sendText(messageBO.getFromUserId(), JSON.toJSONString(messageBO));
        return Result.success();
    }

}
