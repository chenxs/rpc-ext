package cn.hill4j.rpcext.core.rpcext.dubbo.enums;

/**
 * 2019/8/28 14:22 <br>
 * Description: 负载均衡类型
 *
 * @author hillchen
 */
public enum LoadbalanceType {
    /**
     * 随机
     */
    RANDOM("random"),
    /**
     * 轮询
     */
    ROUNDROBIN("roundrobin"),
    /**
     *最少活跃调用数
     */
    LEASTACTIVE("leastactive");
    private String type;

    LoadbalanceType(String type) {
        this.type = type;
    }

    /**
     *
     * @return type
     */
    public String getType() {
        return type;
    }
}
