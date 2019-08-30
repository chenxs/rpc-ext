package com.hill.rpc.ext.core.utils.test;

/**
 * 〈一句话功能简述〉<br>
 * Description: SuperClass
 *
 * @author hillchen
 * @create 2019/8/30 10:32
 */
public class SuperClass {
    private transient volatile boolean initialized;

    public boolean isInitialized() {
        return initialized;
    }
}