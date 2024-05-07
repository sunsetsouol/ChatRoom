//package org.example.fallback;
//
//import feign.hystrix.FallbackFactory;
//import lombok.extern.slf4j.Slf4j;
//import org.example.feign.client.UserClient;
//
///**
// * @author yinjunbiao
// * @version 1.0
// * @date 2024/4/29
// */
//@Slf4j
//public class UserClientFallbackFactory implements FallbackFactory<UserClient> {
//    @Override
//    public UserClient create(Throwable throwable) {
//        return new UserClient() {
//        };
//    }
//}
