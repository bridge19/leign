package io.github.bridge.leign.core.proxy;

import org.springframework.cglib.proxy.InvocationHandler;

import java.lang.reflect.Method;
import java.util.Map;

public class LeignClientProxy<T>  implements InvocationHandler {

    private final Class<T> leignClientInterface;
    private final Map<Method, LeignClientMethod> methodCache;

    public LeignClientProxy(Class<T> leignClientInterface, Map<Method, LeignClientMethod> methodCache){
        this.leignClientInterface = leignClientInterface;
        this.methodCache = methodCache;
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        LeignClientMethod leignClientMethod = methodCache.get(method);

        if(leignClientMethod == null){
            leignClientMethod = new LeignClientMethod(method);
            leignClientMethod.init();
        }
        System.out.println(leignClientInterface.getName() +" invoked.");
        return leignClientMethod.execute(args);
    }
}
