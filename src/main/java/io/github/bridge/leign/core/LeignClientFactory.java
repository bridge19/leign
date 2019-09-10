package io.github.bridge.leign.core;

import io.github.bridge.leign.core.proxy.LeignClientProxy;
import org.springframework.cglib.proxy.Proxy;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LeignClientFactory<T> {
    //代理的接口
    private final Class<T> mapperInterface;
    //缓存代理的方法
    private final Map<Method, LeignClientMethod> methodCache = new ConcurrentHashMap<>();

    public LeignClientFactory(Class<T> mapperInterface){
        this.mapperInterface = mapperInterface;
    }
    public T newInstance() {
        //创建一个MapperProxy对象,其内部封装了方法拿到接口的代码对象
        final LeignClientProxy<T> mapperProxy = new LeignClientProxy<T>(mapperInterface, methodCache);
        return newInstance(mapperProxy); //调用方法返回接口的代理对象
    }
    protected T newInstance(LeignClientProxy<T> mapperProxy) {
        //底层使用JDK动态创建接口的代理对象
        return (T) Proxy.newProxyInstance(mapperInterface.getClassLoader(), new Class[] { mapperInterface }, mapperProxy);
    }
}
