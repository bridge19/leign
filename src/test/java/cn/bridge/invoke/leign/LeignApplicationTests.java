package cn.bridge.invoke.leign;

import io.github.bridge.leign.LeignApplication;
import io.github.bridge.leign.service.ClientTestService;
import io.github.bridge.leign.service.ClientTestService2;
import io.github.bridge.leign.service.MyObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LeignApplication.class)
public class LeignApplicationTests {

    @Autowired
    private ClientTestService clientTestService;


    @Autowired
    private ClientTestService2 clientTestService2;

    @Test
    public void contextLoads() {
        MyObject myObject = new MyObject();
        myObject.setName("xxxx");
        myObject.setSex("male");
        myObject.setBody1("body1");
        myObject.setBody2("odya1");
       String out = clientTestService.hello("xuyong",999999,myObject);
        System.out.println(out);
    }

    @Test
    public void hello2Test() {
        MyObject myObject = new MyObject();
        myObject.setName("xxxx");
        myObject.setSex("male");
        myObject.setBody1("body1");
        myObject.setBody2("odya1");
        String out = clientTestService2.hello("xuyong",999999,myObject);
        System.out.println(out);
    }
}
