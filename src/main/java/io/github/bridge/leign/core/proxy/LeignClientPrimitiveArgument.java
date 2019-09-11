package io.github.bridge.leign.core.proxy;

import io.github.bridge.leign.annotation.Header;
import io.github.bridge.leign.annotation.Param;
import io.github.bridge.leign.enums.VariableType;
import lombok.Data;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.Map;

@Data
public class LeignClientPrimitiveArgument implements ILeignClientArgument {
    private boolean useful;
    private Class clazz;
    private Parameter parameter;
    private VariableType variableType;
    private String variableName;

    public LeignClientPrimitiveArgument(Parameter parameter){
        this.parameter = parameter;
    }
    @Override
    public void parse() {
        Annotation[] annotations = parameter.getAnnotations();
        if(!ArrayUtils.isEmpty(annotations)) {
            Annotation annotation = annotations[0];
            if (annotation instanceof Header) {
                variableType = VariableType.HEADER;
                Header header = (Header) annotation;
                variableName = header.value().trim();
                if (StringUtils.isEmpty(variableName)) {
                    variableName = parameter.getName();
                }
            } else if (annotation instanceof Param) {
                variableType = VariableType.PARAMETER;
                Param param = (Param) annotation;
                variableName = param.value().trim();
                if (StringUtils.isEmpty(variableName)) {
                    variableName = parameter.getName();
                }
            } else {
                throw new RuntimeException("illegal annotation type.");
            }
        }else{
            variableType = VariableType.QUERYBODY;
            variableName = parameter.getName();
        }
    }

    public void getParameters(Object value, Map<String, String> target) {
        if (variableType != VariableType.PARAMETER)
            return;
        String strValue = null;
        if (value instanceof String) {
            strValue = (String) value;
        } else {
            strValue = String.valueOf(value);
        }
        target.put(variableName, strValue);
    }

    @Override
    public void getHeaders(Object value, Map<String, String> target) {
        if (variableType != VariableType.HEADER)
            return;
        String strValue = null;
        if (value instanceof String) {
            strValue = (String) value;
        } else {
            value = String.valueOf(value);
        }
        target.put(variableName, strValue);
    }
}
