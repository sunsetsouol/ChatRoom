package org.example.IdStrategy.IdGen;

import org.example.IdStrategy.IdType.IdType;
import org.springframework.stereotype.Component;
/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/2/15
 */
@Component
public abstract class IdGenerator {

    public long getLongId(){
        return 0;
    }

    public String  getStringId(){
        return "";
    }

    public abstract String getType();

    public Object getId(){
        if (IdType.LONG.type.equals(getType())){
            return getLongId();
        }else if (IdType.STRING.type.equals(getType())){
            return getStringId();
        }
        return null;
    }
}
