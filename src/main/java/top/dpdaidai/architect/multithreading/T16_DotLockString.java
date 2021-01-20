package top.dpdaidai.architect.multithreading;

/**
 *  由于字符串的存储和重用问题, 导致对String加锁是非常危险的.
 *  1  需要避免对运行中生成的String加锁, 因为这时的String是存在堆中的, 不会被重用.
 *      就会出现多个同样字面量的String
 *      这个可以用String.intern(), 将该String移动到常量池中
 *  2  业务中的某个String字面量, 可能依赖的某个类库中也有这个String字面量
 *      如果都在常量池中, 那么就是同一个对象, 如果不经意的锁定了,
 *      可能出现奇怪的阻塞情况
 *
 *
 * @Author chenpantao
 * @Date 1/20/21 9:37 PM
 * @Version 1.0
 */
public class T16_DotLockString {

    public static void main(String[] args) {
//        String类型的值通常存储于常量池或JVM堆中。
//
//        字符串变量赋值时，赋值符右边：
//
//        1、只有字符串常量，那么变量存放在常量池里面。
//
//        2、new出来的字符串是存放在 堆里面。
//
//        3、对字符串进行拼接（+）操作时，分两种情况：
//
//        A、表达式右边是纯字符串常量，那么存放在常量池里面。
//
//        B、表达式右边如果存在字符串引用，那么就存放在JVM堆里面。

        String a = "aaa";  // 常量池
        String b = "bbb";  // 常量池
        String c = a + b;  // JVM堆
        String d = "aaa" + "bbb";  // 常量池
        String e = "aaabbb";  // 常量池

        System.out.println(c == d); // false
        System.out.println(d == e);// true
        System.out.println(c.intern() == d); // true

//        通过上面理解，当对String加锁的时候,需要保证当前加锁的String是唯一的。
//
//        如果要对String加锁,最好是加锁String.intern()方法。
//
//        String.intern()：判断该字符串是否存在常量池中,如果存在直接获取,不存在将当前字符串放到常量池中。

        String param = "type";
        String num = "1";
        String token = param + num;
        synchronized (token.intern()) {
            // 执行同步方法
        }

    }

    String s1 = "Hello";
    String s2 = "Hello";

    void m1() {
        synchronized(s1) {

        }
    }

    void m2() {
        synchronized(s2) {

        }
    }


}
