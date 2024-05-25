package org.example.IdStrategy.IdGen;

import cn.hutool.extra.spring.SpringUtil;
import org.example.IdStrategy.IdGen.Impl.SnowFlake;
import org.example.IdStrategy.IdGen.Impl.UUIDGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/2/15
 */
@Component
public class IdGeneratorStrategyFactory {
    private final HashMap<String , IdGenerator> idGeneratorHashMap = new HashMap<>();

//    @Autowired
//    private SnowFlake snowFlake;
//
//    @Autowired
//    private UUIDGenerator uuidGenerator;

//    @Autowired
//    private TickerServers ticketServers;

    @PostConstruct
    protected void init(){
        idGeneratorHashMap.put(IdGenType.SNOWFLAKE.type, SpringUtil.getBean(SnowFlake.class));
        idGeneratorHashMap.put(IdGenType.UUID.type, SpringUtil.getBean(UUIDGenerator.class));
//        idGeneratorHashMap.put(IdGenType.TICKET_SERVERS.type, ticketServers);
    }

    public IdGenerator getIdGeneratorStrategy(String strategyType) {
        return idGeneratorHashMap.get(strategyType);
    }
}
