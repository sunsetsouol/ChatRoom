package org.example.gateway.hashring;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/5/20
 */
public interface Node {

    @JsonIgnore
    String getKey();
}
