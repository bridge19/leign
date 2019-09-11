package io.github.bridge.leign.service;

import io.github.bridge.leign.annotation.Header;
import io.github.bridge.leign.annotation.Param;
import lombok.Data;

@Data
public class MyObject {

    @Param
    private String name;
    @Header
    private String sex;
    private String body1;
    private String body2;
}
