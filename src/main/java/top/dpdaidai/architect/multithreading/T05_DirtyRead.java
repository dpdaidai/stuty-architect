package top.dpdaidai.architect.multithreading;

import java.util.concurrent.TimeUnit;

/**
 * 模拟银行账户
 * 对写方法加锁 , 对读方法不加锁
 * 这理应产生脏读的可能性
 * 1  先设置账户的余额
 * 2  在设置成功前 , 读取账户余额 , 则产生脏读现象
 * 3  对对象加锁 或者 对读的方法也加上锁 , 可成功读取
 *
 * @Author chenpantao
 * @Date 1/12/21 9:52 PM
 * @Version 1.0
 */
public class T05_DirtyRead {


    public static void main(String[] args) {

        Account account = new Account();
        new Thread(() ->
                account.set("zhangsan", 100.0)
        ).start();

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(account.getBalance("zhangsan"));

    }


}

class Account {

    String name;
    double balance;

    public synchronized void set(String name, double balance) {
        this.name = name;

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        this.balance = balance;

    }

    public
//    synchronized
    double getBalance(String name) {
        return this.balance;
    }

}
