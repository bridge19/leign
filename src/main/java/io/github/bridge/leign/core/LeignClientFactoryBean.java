package io.github.bridge.leign.core;

import io.github.bridge.leign.core.proxy.LeignClientRegistry;
import org.springframework.beans.factory.FactoryBean;

public class LeignClientFactoryBean<T> implements FactoryBean<T> {
    private Class<T> leignClientInterface;

    private LeignClientRegistry leignClientRegistry = new LeignClientRegistry();
    public LeignClientFactoryBean() {
        //intentionally empty 
    }

    public LeignClientFactoryBean(Class<T> leignClientInterface) {
        this.leignClientInterface = leignClientInterface;
    }

    @Override
    public T getObject() throws Exception {
        return leignClientRegistry.getLeignClient(leignClientInterface);
    }

    @Override
    public Class<?> getObjectType() {
        return leignClientInterface;
    }
}
