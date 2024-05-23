package org.example.gateway.hashring;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/5/20
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RealNode implements Node, Serializable {
    private static final long serialVersionUID = 5410221835105700427L;

    private String ip;


    @Override
    public String getKey() {
        return ip;
    }

    @Override
    public String toString() {
        return getKey();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RealNode that = (RealNode) o;
        return Objects.equals(ip, that.ip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ip);
    }

    public static RealNode fromJSONObject(JSONObject jsonObject) {
        return new RealNode(jsonObject.getString("ip"));
    }
}
