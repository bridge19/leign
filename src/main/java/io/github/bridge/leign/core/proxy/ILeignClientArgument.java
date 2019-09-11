package io.github.bridge.leign.core.proxy;

import io.github.bridge.leign.enums.VariableType;

import java.util.Map;

public interface ILeignClientArgument {
    void parse();
    void getParameters(Object value, Map<String,String> target);
    void getHeaders(Object value, Map<String,String> target);
    VariableType getVariableType();
    String getVariableName();
}
