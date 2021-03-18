package top.dpdaidai.architect.ci;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author chenpantao
 * @Date 3/18/21 11:52 AM
 * @Version 1.0
 */
@RequestMapping("/test")
@RestController
public class TestController {

    @RequestMapping("/hello")
    public String Hello(){
        return "hello CI test";
    }


}
