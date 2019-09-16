package io.github.bridge.leign.core.proxy;

import java.util.HashMap;
import java.util.Map;

public class LeignClientRegistry {
    private final Map<Class<?>, LeignClientFactory<?>> LeignClientCache = new HashMap<Class<?>, LeignClientFactory<?>>();;

    public <T> T getLeignClient(Class<T> type) {
        //从map中取出已经注册的mapper接口代理对象的工厂
        LeignClientFactory<T> leignClientFactory = (LeignClientFactory<T>) LeignClientCache.get(type);
        if(leignClientFactory == null){
            leignClientFactory = new LeignClientFactory(type);
            LeignClientCache.put(type,leignClientFactory);
        }
        //创建并返回mapper接口的代码对象
        return leignClientFactory.newInstance();
    }
}
