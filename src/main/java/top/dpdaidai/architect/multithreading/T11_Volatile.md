## Java并发编程：volatile关键字解析 --- 笔记
转载自 [Matrix海子](https://www.cnblogs.com/dolphin0520/) -- [Java并发编程：volatile关键字解析](https://www.cnblogs.com/dolphin0520/p/3920373.html)

由于volatile关键字是与Java的内存模型有关的，因此在讲述volatile关键之前，
我们先来了解一下与内存模型相关的概念和知识，然后分析了volatile关键字的实现原理，
最后给出了几个使用volatile关键字的场景。

####    一.内存模型的相关概念
程序在运行过程中，会将运算需要的数据从主存复制一份到CPU的高速缓存当中，
那么CPU进行计算时就可以直接从它的高速缓存读取数据和向其中写入数据，当运算结束之后，
再将高速缓存中的数据刷新到主存当中。举个简单的例子，比如下面的这段代码：

```
i = i + 1;
```
　当线程执行这个语句时，会先从主存当中读取i的值，然后复制一份到高速缓存当中，
然后CPU执行指令对i进行加1操作，然后将数据写入高速缓存，最后将高速缓存中i最新的值刷新到主存当中。 


  这个代码在单线程中运行是没有任何问题的，但是在多线程中运行就会有问题了。
如果一个变量在多个CPU中都存在缓存（一般在多线程编程时才会出现），那么就可能存在缓存不一致的问题。
  
 为了解决缓存不一致性问题，通常来说有以下2种解决方法：

　　1.    通过在总线加LOCK#锁的方式    
在早期的CPU当中，是通过在总线上加LOCK#锁的形式来解决缓存不一致的问题。
因为CPU和其他部件进行通信都是通过总线来进行的，如果对总线加LOCK#锁的话，
也就是说阻塞了其他CPU对其他部件访问（如内存），从而使得只能有一个CPU能使用这个变量的内存。
比如上面例子中 如果一个线程在执行 i = i +1，如果在执行这段代码的过程中，在总线上发出了LCOK#锁的信号，
那么只有等待这段代码完全执行完毕之后，其他CPU才能从变量i所在的内存读取变量，然后进行相应的操作。
这样就解决了缓存不一致的问题。
     
但是上面的方式会有一个问题，由于在锁住总线期间，其他CPU无法访问内存，导致效率低下。

　　2.    通过缓存一致性协议   
所以就出现了缓存一致性协议。最出名的就是Intel 的MESI协议，
MESI协议保证了每个缓存中使用的共享变量的副本是一致的。
它核心的思想是：当CPU写数据时，如果发现操作的变量是共享变量，即在其他CPU中也存在该变量的副本，
会发出信号通知其他CPU将该变量的缓存行置为无效状态，因此当其他CPU需要读取这个变量时，
发现自己缓存中缓存该变量的缓存行是无效的，那么它就会从内存重新读取。

这2种方式都是硬件层面上提供的方式。

#### 二.并发编程中的三个概念
1. 原子性      
一个事务内的多个操作 , 对外显示为一个原子 . 外部看到的要么是没有执行 , 要么是完全执行成功的情况.
比如从账户A向账户B转1000元，那么必然包括2个操作：从账户A减去1000元，往账户B加上1000元。
所以这2个操作必须要具备原子性才能保证不出现一些意外的问题。



    
    
    
2. 可见性  
可见性是指当多个线程访问同一个变量时，一个线程修改了这个变量的值，其他线程能够立即看得到修改的值。
    ```
    //线程1执行的代码
    int i = 0;
    i = 10;
     
    //线程2执行的代码
    j = i;
    ```
    假若执行线程1的是CPU1，执行线程2的是CPU2。由上面的分析可知，当线程1执行 i =10这句时，
    会先把i的初始值加载到CPU1的高速缓存中，然后赋值为10，那么在CPU1的高速缓存当中i的值变为10了，
    却没有立即写入到主存当中。
    
    此时线程2执行 j = i，它会先去主存读取i的值并加载到CPU2的缓存当中，注意此时内存当中i的值还是0，
    那么就会使得j的值为0，而不是10.
    
    这就是可见性问题，线程1对变量i修改了之后，线程2没有立即看到线程1修改的值。


3. 有序性  
    即程序执行的顺序按照代码的先后顺序执行。
    +   指令重排序，一般来说，处理器为了提高程序运行效率，可能会对输入代码进行优化，
    +   它不保证程序中各个语句的执行先后顺序同代码中的顺序一致，但是它会保证程序最终执行结果和代码顺序执行的
    结果是一致的。
    +   处理器在进行重排序时是会考虑指令之间的数据依赖性，
    +   指令重排序不会影响单个线程的执行，但是会影响到线程并发执行的正确性。

###### 也就是说，要想并发程序正确地执行，必须要保证原子性、可见性以及有序性。只要有一个没有被保证，就有可能会导致程序运行不正确。

####  三.Java内存模型

在Java虚拟机规范中试图定义一种Java内存模型（Java Memory Model，JMM）
来屏蔽各个硬件平台和操作系统的内存访问差异，以实现让Java程序在各种平台下都能达到一致的内存访问效果。
。注意，为了获得较好的执行性能，Java内存模型并没有限制执行引擎使用处理器的寄存器或者高速缓存来提升指令执行速度，也没有限制编译器对指令进行重排序。
也就是说，在java内存模型中，也会存在缓存一致性问题和指令重排序的问题。

Java内存模型规定所有的变量都是存在主存当中（类似于前面说的物理内存），每个线程都有自己的工作内存（类似于前面的高速缓存）。线程对变量的所有操作都必须在工作内存中进行，
而不能直接对主存进行操作。并且每个线程不能访问其他线程的工作内存。

举个简单的例子：在java中，执行下面这个语句：

```
i  = 10;
```

执行线程必须先在自己的工作线程中对变量i所在的缓存行进行赋值操作，然后再写入主存当中。
而不是直接将数值10写入主存当中。

###### 那么Java语言 本身对 原子性、可见性以及有序性提供了哪些保证呢？
1.  原子性     
    反应到内存一致中的表现为 : 
    ```
    1  声明 volatile int i = 0;
    2  线程1读取i的值写入到i的工作区
    3  线程1修改变量 i 的值, 将结果写入工作区                                                                   
    4  线程1将变量i的值, 写入到主内存中
    5  2,3,4这三个动作必须对别的线程展示为一个原子 , 不然就有问题
    ```
2.  可见性     
    当一个共享变量被volatile修饰时，它会保证修改的值会立即被更新到主存，当有其他线程需要读取时，
    它会去内存中读取新值。

    而普通的共享变量不能保证可见性，因为普通共享变量被修改之后，什么时候被写入主存是不确定的，
    当其他线程去读取时，此时内存中可能还是原来的旧值，因此无法保证可见性。
    
3.  有序性     
    在Java内存模型中，允许编译器和处理器对指令进行重排序，但是重排序过程不会影响到单线程程序的执行，
    却会影响到多线程并发执行的正确性。
    
    在Java里面，可以通过volatile关键字来保证一定的“有序性”（具体原理在下一节讲述）。
    另外可以通过synchronized和Lock来保证有序性，很显然，synchronized和Lock保证每个时刻是有一个
    线程执行同步代码，相当于是让线程顺序执行同步代码，自然就保证了有序性。
    
    另外，Java内存模型具备一些先天的“有序性”，即不需要通过任何手段就能够得到保证的有序性，
    这个通常也称为 happens-before 原则。如果两个操作的执行次序无法从happens-before原则推导出来，
    那么它们就不能保证它们的有序性，虚拟机可以随意地对它们进行重排序。
    
    
    
####  四.深入剖析volatile关键字

1.  volatile关键字的两层语义

    一旦一个共享变量（类的成员变量、类的静态成员变量）被volatile修饰之后，那么就具备了两层语义：
    
    1）保证了不同线程对这个变量进行操作时的可见性，即一个线程修改了某个变量的值，
这新值对其他线程来说是立即可见的。
    
    2）禁止进行指令重排序。

    ```
    //线程1
    boolean stop = false;
    while(!stop){
        doSomething();
    }
     
    //线程2
    stop = true;
    ```
　　这段代码是很典型的一段代码，很多人在中断线程时可能都会采用这种标记办法。但是事实上，
这段代码会完全运行正确么？即一定会将线程中断么？不一定，也许在大多数时候，这个代码能够把线程中断，
但是也有可能会导致无法中断线程（虽然这个可能性很小，但是只要一旦发生这种情况就会造成死循环了）。

　　下面解释一下这段代码为何有可能导致无法中断线程。在前面已经解释过，
每个线程在运行过程中都有自己的工作内存，那么线程1在运行的时候，会将stop变量的值拷贝
一份放在自己的工作内存当中。

　　那么当线程2更改了stop变量的值之后，但是还没来得及写入主存当中，线程2转去做其他事情了，
那么线程1由于不知道线程2对stop变量的更改，因此还会一直循环下去。

　　但是用volatile修饰之后就变得不一样了：

　　第一：使用volatile关键字会强制将修改的值立即写入主存；

　　第二：使用volatile关键字的话，当线程2进行修改时，会导致线程1的工作内存中缓存变量stop的缓存行无效
（反映到硬件层的话，就是CPU的L1或者L2缓存中对应的缓存行无效）；

　　第三：由于线程1的工作内存中缓存变量stop的缓存行无效，所以线程1再次读取变量stop的值时会去主存读取。

　　那么在线程2修改stop值时（当然这里包括2个操作，修改线程2工作内存中的值，然后将修改后的值写入内存），
会使得线程1的工作内存中缓存变量stop的缓存行无效，然后线程1读取时，发现自己的缓存行无效，
它会等待缓存行对应的主存地址被更新之后，然后去对应的主存读取最新的值。

　　那么线程1读取到的就是最新的正确的值。

2.  volatile保证原子性吗？
如下示例 : 
```
public class Test {
    public volatile int inc = 0;
     
    public void increase() {
        inc++;
    }
     
    public static void main(String[] args) {
        final Test test = new Test();
        for(int i=0;i<10;i++){
            new Thread(){
                public void run() {
                    for(int j=0;j<1000;j++)
                        test.increase();
                };
            }.start();
        }
         
        while(Thread.activeCount()>1)  //保证前面的线程都执行完
            Thread.yield();
        System.out.println(test.inc);
    }
}
```
事实上运行它会发现每次运行结果都不一致，都是一个小于10000的数字。

> 1.    volatile仅能保证可见性 , 并不等于加上读锁 .   
> 2.    线程1在读取inc的值后, 线程2, 线程3 都可以再次读取inc的值.   
> 3.    线程1在修改inc的值后, 是会造成其余线程共享变量的缓存失效 ,     
> 4.    但其余线程可能也运算完毕 , 同样对inc值进行修改, 覆盖了线程1的运算结果. 导致线程1的运算结果丢失.    
> 5.    采用synchronized, Lock 和 AtomicInteger 都可以得到正确的结果.

3. volatile能保证有序性吗？     
    volatile关键字禁止指令重排序有两层意思：   
    
    1）当程序执行到volatile变量的读操作或者写操作时，在其前面的操作的更改肯定全部已经进行，
    且结果已经对后面的操作可见；在其后面的操作肯定还没有进行；   
    
    2）在进行指令优化时，不能将在对volatile变量访问的语句放在其后面执行，
    也不能把volatile变量后面的语句放到其前面执行。
    
4.  volatile的原理和实现机制        
    前面讲述了源于volatile关键字的一些使用，下面我们来探讨一下volatile到底如何保证可见性和禁止指令重排序的。    
    下面这段话摘自《深入理解Java虚拟机》：
    
    “观察加入volatile关键字和没有加入volatile关键字时所生成的汇编代码发现，加入volatile关键字时，
    会多出一个lock前缀指令”
    
    lock前缀指令实际上相当于一个内存屏障（也成内存栅栏），内存屏障会提供3个功能：
    
    1）它确保指令重排序时不会把其后面的指令排到内存屏障之前的位置，也不会把前面的指令排到内存屏障的后面；
    即在执行到内存屏障这句指令时，在它前面的操作已经全部完成；
    
    2）它会强制将对缓存的修改操作立即写入主存；
    
    3）如果是写操作，它会导致其他CPU中对应的缓存行无效。

#### 五.使用volatile关键字的场景
仅当满足以下条件时 , 才应该使用volatile变量
+   对变量的写入操作不依赖变量的当前值, 或者能保证只有一个线程更新变量的值
+   该变量不会与其它状态量一起纳入不变性条件中
+   在访问变量时不需要加锁

下面举例 :

1.  状态标记量
```
volatile boolean flag = false;
 
while(!flag){
    doSomething();
}
 
public void setFlag() {
    flag = true;
}
```
```
volatile boolean inited = false;
//线程1:
context = loadContext();  
inited = true;            
 
//线程2:
while(!inited ){
sleep()
}
doSomethingwithconfig(context);
```

2.  double check
```
class Singleton{
    private volatile static Singleton instance = null;
     
    private Singleton() {
         
    }
     
    public static Singleton getInstance() {
        if(instance==null) {
            synchronized (Singleton.class) {
                if(instance==null)
                    instance = new Singleton();
            }
        }
        return instance;
    }
}
```