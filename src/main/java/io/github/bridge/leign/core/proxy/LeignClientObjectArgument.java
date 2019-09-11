package io.github.bridge.leign.core.proxy;

import io.github.bridge.leign.annotation.Header;
import io.github.bridge.leign.annotation.Param;
import io.github.bridge.leign.annotation.QueryBody;
import io.github.bridge.leign.enums.VariableType;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Data
@Slf4j
public class LeignClientObjectArgument implements ILeignClientArgument {
    private Class clazz;
    private VariableType variableType;
    private String variableName;
    private Parameter parameter;

    private Map<String, Method> paramArgument = new HashMap<>();
    private Map<String, Method> headerArgument = new HashMap<>();
    private Map<String, Method> body = new HashMap<>();

    public LeignClientObjectArgument(Parameter parameter) {
        this.parameter = parameter;
        this.clazz = parameter.getType();
    }

    @Override
    public void parse() {
        Annotation[] annotations = parameter.getAnnotations();
        variableName = parameter.getName();
        if (ArrayUtils.isNotEmpty(annotations)) {
            Annotation annotation = annotations[0];
            if (annotation instanceof QueryBody) {
                variableType = VariableType.QUERYBODY;
            }
        } else {
            Field[] fields = clazz.getDeclaredFields();
            if (ArrayUtils.isNotEmpty(fields)) {
                for (Field field : fields) {
                    Param[] params = field.getAnnotationsByType(Param.class);
                    if (ArrayUtils.isNotEmpty(params)) {
                        Param param = params[0];
                        parseParam(param, field);
                    }else {
                        Header[] headers = field.getAnnotationsByType(Header.class);
                        if (ArrayUtils.isNotEmpty(headers)) {
                            Header header = headers[0];
                            parseHeader(header, field);
                        }else {
                            body.put(field.getName(),convertGetter(field.getName()));
                        }
                    }
                }
                if (paramArgument.size() == 0 && headerArgument.size() == 0) {
                    variableType = VariableType.QUERYBODY;
                }else{
                    variableType = VariableType.MIX;
                }
            }
        }
    }

    private void parseParam(Param param, Field field) {
        String paramKey = param.value();
        if (StringUtils.isEmpty(paramKey)) {
            paramKey = field.getName();
        }
        Method method = convertGetter(field.getName());
        if (method != null) {
            paramArgument.put(paramKey, method);
        }
    }

    private void parseHeader(Header header, Field field) {
        String headerKey = header.value();
        if (headerKey == null) {
            headerKey = field.getName();
        }
        Method method = convertGetter(field.getName());
        if (method != null) {
            headerArgument.put(headerKey, method);
        }
    }

    private Method convertGetter(String fieldName) {
        String str1 = fieldName.substring(0, 1);
        String str2 = fieldName.substring(1, fieldName.length());
        String method_get = "get" + str1.toUpperCase() + str2;
        Method method = null;
        try {
            method = clazz.getMethod(method_get, null);
        } catch (NoSuchMethodException e) {
            log.warn(method_get + " in Class: " + clazz.getName() + " is not found.");
        }
        return method;
    }

    @Override
    public void getParameters(Object value, Map<String, String> target) {
        Iterator<Map.Entry<String, Method>> iterator = paramArgument.entrySet().iterator();
        setVariable(value, target, iterator);
    }

    @Override
    public void getHeaders(Object value, Map<String, String> target) {
        Iterator<Map.Entry<String, Method>> iterator = headerArgument.entrySet().iterator();
        setVariable(value, target, iterator);
    }

    @Override
    public Map<String, Object> getBody(Object value) {

        Map<String,Object> bodyMap = null;
        if(body.size()>0) {
            bodyMap = new HashMap<>();
            Iterator<Map.Entry<String, Method>> iterator = body.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Method> entry = iterator.next();
                String headerKey = entry.getKey();
                Object headerValue = null;
                try {
                    headerValue = entry.getValue().invoke(value, null);
                } catch (IllegalAccessException e) {
                    log.warn("get function error.", e);
                } catch (InvocationTargetException e) {
                    log.warn("get function error.", e);
                }
                bodyMap.put(headerKey, headerValue);
            }
        }
        return bodyMap;
    }

    public void setVariable(Object value, Map<String, String> target, Iterator<Map.Entry<String, Method>> iterator) {
        if (!clazz.getName().equals(value.getClass().getName())) {
            return;
        }
        while (iterator.hasNext()) {
            Map.Entry<String, Method> entry = iterator.next();
            String headerKey = entry.getKey();
            Object headerValue = null;
            try {
                headerValue = entry.getValue().invoke(value, null);
            } catch (IllegalAccessException e) {
                log.warn("get function error.", e);
            } catch (InvocationTargetException e) {
                log.warn("get function error.", e);
            }
            if (headerValue != null && !(headerValue instanceof String)) {
                target.put(headerKey, String.valueOf(headerValue));
            } else {
                target.put(headerKey, (String) headerValue);
            }
        }
    }
}
