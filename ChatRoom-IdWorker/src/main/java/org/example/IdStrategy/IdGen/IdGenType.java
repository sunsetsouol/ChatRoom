package org.example.IdStrategy.IdGen;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/2/15
 */

public enum IdGenType {
    SNOWFLAKE("snowflake"),
    UUID("uuid"),
//    TICKET_SERVERS("ticketServers")
    ;


    public final String type;

    IdGenType(String type) {
        this.type = type;
    }

}
