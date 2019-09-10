package io.github.bridge.leign.core.proxy;

import io.github.bridge.leign.core.LeignClientMethod;
import org.springframework.cglib.proxy.InvocationHandler;

import java.lang.reflect.Method;
import java.util.Map;

public class LeignClientProxy<T>  implements InvocationHandler {

    private final Class<T> mapperInterface;
    private final Map<Method, LeignClientMethod> methodCache;

    public LeignClientProxy(Class<T> mapperInterface, Map<Method, LeignClientMethod> methodCache){
        this.mapperInterface = mapperInterface;
        this.methodCache = methodCache;
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        LeignClientMethod leignClientMethod = methodCache.get(method);

        if(leignClientMethod == null){
            leignClientMethod = new LeignClientMethod(method);
            leignClientMethod.init();
        }
        System.out.println(mapperInterface.getName() +" invoked.");
        return leignClientMethod.execute(args);
    }
}
