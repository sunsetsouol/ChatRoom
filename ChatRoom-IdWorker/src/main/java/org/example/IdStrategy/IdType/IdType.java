package org.example.IdStrategy.IdType;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/2/18
 */
public enum IdType {

    LONG("long"),
    STRING("string");

    public final String type;

    IdType(String type) {
        this.type = type;
    }
}
