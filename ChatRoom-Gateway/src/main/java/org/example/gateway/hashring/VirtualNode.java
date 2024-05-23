package org.example.gateway.hashring;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author yinjunbiao
 * @version 1.0
 * @date 2024/5/20
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class VirtualNode<T extends Node> implements Node, Serializable {
    private static final long serialVersionUID = 5410221835105700427L;

    T physicalNode;

    Integer replicaIndex;

    @Override
    public String getKey() {
        return physicalNode.getKey() + "-" +  replicaIndex;
    }

    /**
     * 是否作为某个真实节点的虚拟副本节点
     *
     * @param anyPhysicalNode 任何真实节点
     * @return 是/否
     */
    public boolean isVirtualOf(T anyPhysicalNode) {
        return anyPhysicalNode.getKey().equals(this.physicalNode.getKey());
    }
    /**
     * 获取当前虚拟节点的真实节点
     *
     * @return 真实节点
     */
    public T getPhysicalNode() {
        return this.physicalNode;
    }

    public static VirtualNode fromJSON(JSONObject jsonObject) {
        return new VirtualNode(RealNode.fromJSONObject(jsonObject.getJSONObject("physicalNode")), jsonObject.getInteger("replicaIndex"));
    }
}
