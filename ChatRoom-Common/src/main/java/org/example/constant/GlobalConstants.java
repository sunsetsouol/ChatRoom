package org.example.constant;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/5/5
 */
public interface GlobalConstants {
    String TOKEN_HEAD = "Authorization";

    String TOKEN_PREFIX = "Bearer ";

    Integer MAX_FRIEND = 1000;

    Integer VIRTUAL_COUNT = 5;


    String LISTEN_WS_SERVICE_NAME = "onmessage";
    String WEBSOCKET_ENDPOINT_PATH = "/ws";

    String JSONTOKEN = "jsonToken";
    String LAST_GLOBAL_MESSAGE_ID = "lastGlobalMessageId";
    String DEVICE_TYPE = "Device";
}
