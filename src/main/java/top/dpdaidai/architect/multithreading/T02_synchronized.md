## synchronized 的底层实现 ( 视频观点, 未读书证实 )

####  JDK早期的 重量级实现
向OS申请锁


####  后来的改进


####  HOT SPOT 锁升级的概念
sync (Object o)
1.  mark word 线程1 的 ID (使用偏向锁) 
2.  如果有线程2争用 , 升级为自旋锁(Atomic), 还是占用CPU的 
3.  线程2旋10次后, 还得不到锁, 升级为重量级锁 , 
    向操作系统申请锁, 然后线程2进入等待状态
   
#### 总结 
加锁的代码执行时间短, 并等待线程少的时候可以用自旋锁,  </br>
反之使用系统锁

## 稍后对synchronized的实现深入了解
[深入理解Java并发之synchronized实现原理](https://blog.csdn.net/javazejian/article/details/72828483)
