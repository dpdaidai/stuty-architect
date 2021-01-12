## 总结
####    线程的概念
####    启动方式
####    常用方法
####    synchronized 不可锁的对象
+   String常量
+   Integer , Long 等基础类型的包装对象

####    线程同步
+   synchronized
    +   锁的是对象, 不是方法
    +   this 锁定的也是本对象
    +   synchronized (T02_Synchronized.class) , 静态方法中的锁定的是 class对象
    +   锁升级
        +   偏向锁
        +   自旋锁
        +   重量级锁
        
