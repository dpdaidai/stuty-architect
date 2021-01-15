package top.dpdaidai.architect.multithreading;

/**
 * 由于cpu指令重排序 , 导致语句2和语句4 在语句1和语句3之前执行 ,
 * 最后得到结果 x=0 , y=0
 *
 * 执行结果 : 第16510次 (0,0）
 *
 * @Author chenpantao
 * @Date 1/14/21 8:52 PM
 * @Version 1.0
 */
public class T10_Reorder {
    private static int x = 0, y = 0;
    private static int a = 0, b = 0;

    public static void main(String[] args) throws InterruptedException {
        int i = 0;
        for (; ; ) {
            i++;
            x = 0;
            y = 0;
            a = 0;
            b = 0;
            Thread one = new Thread(new Runnable() {
                public void run() {
                    //由于线程one先启动，下面这句话让它等一等线程two. 读着可根据自己电脑的实际性能适当调整等待时间.
                    shortWait(100000);
                    a = 1;    //语句1
                    x = b;    //语句2
                }
            });

            Thread other = new Thread(new Runnable() {
                public void run() {
                    b = 1;    //语句3
                    y = a;    //语句4
                }
            });
            one.start();
            other.start();
            one.join();
            other.join();
            String result = "第" + i + "次 (" + x + "," + y + "）";
            if (x == 0 && y == 0) {
                System.err.println(result);
                break;
            } else {
                System.out.println(result);
            }
        }
    }


    public static void shortWait(long interval) {
        long start = System.nanoTime();
        long end;
        do {
            end = System.nanoTime();
        } while (start + interval >= end);
    }
}


