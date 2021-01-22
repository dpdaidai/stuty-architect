package top.dpdaidai.architect.multithreading;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 *
 * 通过反射 , 调用unsafe类, 指定再内存中修改值
 *
 * @Author chenpantao
 * @Date 1/22/21 11:02 PM
 * @Version 1.0
 */
public class T19_HelloUnsafe {
    static class M {
        private M() {
        }

        int i = 0;
    }

    public static void main(String[] args) throws InstantiationException, NoSuchFieldException, IllegalAccessException {
        Field f = Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        Unsafe unsafe = (Unsafe) f.get(null);
        M m = (M) unsafe.allocateInstance(M.class);
        m.i = 9;
        System.out.println(m.i);
    }

}
