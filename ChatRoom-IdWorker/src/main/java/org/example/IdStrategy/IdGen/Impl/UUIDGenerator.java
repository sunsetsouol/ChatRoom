package org.example.IdStrategy.IdGen.Impl;

import org.example.IdStrategy.IdGen.IdGenerator;
import org.example.IdStrategy.IdType.IdType;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/2/18
 */
@Component
public class UUIDGenerator extends IdGenerator {

    @Override
    public String getStringId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    @Override
    public String getType() {
        return IdType.STRING.type;
    }
}
