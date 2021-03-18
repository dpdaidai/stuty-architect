package top.dpdaidai.architect;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ArchitectApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    public void test1() {
        String a = "a";
        String b = "b";

        assert "ab".equals(a + b);
        System.out.println("========== test 1 finished ============");
    }
}
