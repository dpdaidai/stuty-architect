package top.dpdaidai.architect.multithreading;

/**
 * @Author chenpantao
 * @Date 1/8/21 11:55 PM
 * @Version 1.0
 */
public class T02_Synchronized {

    private int count = 10;

    private static int staticCount = 10;

    private Object o = new Object();

    // 1  锁定了一个对象
    public void synchronizedObject() {
        synchronized (o) {
            count--;
            System.out.println(Thread.currentThread().getName() +
                    "count = " + count);
        }
    }

    //2  锁定本类的实例化对象
    public void synchronizedThis() {
        synchronized (this) {
            count--;
            System.out.println(Thread.currentThread().getName() +
                    "count = " + count);
        }
    }

    //3  锁定本类得实例化对象得方法, 和2等效
    public synchronized void synchronizedMethod() {
        count--;
        System.out.println(Thread.currentThread().getName() +
                "count = " + count);
    }

    //4  静态方法中锁定类对象
    public synchronized static void synchronizedClassObject() {
        synchronized (T02_Synchronized.class) {
            staticCount--;
            System.out.println(Thread.currentThread().getName() +
                    "staticCount = " + staticCount);
        }
    }

    //5  锁定静态方法 , 等效4
    public synchronized static void synchronizedStaticMethod() {
        staticCount--;
        System.out.println(Thread.currentThread().getName() +
                "staticCount = " + staticCount);
    }

}
