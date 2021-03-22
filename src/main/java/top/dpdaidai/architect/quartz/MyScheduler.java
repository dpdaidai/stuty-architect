package top.dpdaidai.architect.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 转载
 * https://blog.csdn.net/noaman_wgs/article/details/80984873
 * (1) Job和JobDetail
 * JobDetail用来绑定Job，为Job实例提供许多属性：
 *      name
 *      group
 *      jobClass
 *      jobDataMap
 * JobDetail绑定指定的Job，每次Scheduler调度执行一个Job的时候，首先会拿到对应的Job，
 * 然后创建该Job实例，再去执行Job中的execute()的内容，任务执行结束后，关联的Job对象实例会被释放，且会被JVM GC清除。
 * 为什么设计成JobDetail + Job，不直接使用Job
 *      JobDetail定义的是任务数据，而真正的执行逻辑是在Job中。
 *      这是因为任务是有可能并发执行，如果Scheduler直接使用Job，就会存在对同一个Job实例并发访问的问题。
 *      而JobDetail & Job 方式，Sheduler每次执行，都会根据JobDetail创建一个新的Job实例，这样就可以规避并发访问的问题。
 *
 * (2)JobExecutionContext
 *      JobExecutionContext中包含了Quartz运行时的环境以及Job本身的详细数据信息。
 *      当Schedule调度执行一个Job的时候，就会将JobExecutionContext传递给该Job的execute()中，
 *      Job就可以通过JobExecutionContext对象获取信息。
 *
 * (3)JobDataMap
 *      JobDataMap实现了JDK的Map接口，可以以Key-Value的形式存储数据。
 *      JobDetail、Trigger都可以使用JobDataMap来设置一些参数或信息，
 *      Job执行execute()方法的时候，JobExecutionContext可以获取到JobExecutionContext中的信息：
 *
 * (4)Trigger
 *      Trigger是Quartz的触发器，会去通知Scheduler何时去执行对应Job。
 *          new Trigger().startAt(): 表示触发器首次被触发的时间;
 *          new Trigger().endAt(): 表示触发器结束触发的时间;
 *      SimpleTrigger
 *          SimpleTrigger可以实现在一个指定时间段内执行一次作业任务或一个时间段内多次执行作业任务。
 *      CronTrigger
 *          CronTrigger功能非常强大，是基于日历的作业调度，而SimpleTrigger是精准指定间隔，
 *          所以相比SimpleTrigger，CroTrigger更加常用。CroTrigger是基于Cron表达式的
 *
 * @Author chenpantao
 * @Date 3/22/21 11:18 AM
 * @Version 1.0
 */
public class MyScheduler {

    public static void main(String[] args) throws SchedulerException, InterruptedException {
        //1 创建调度器
        StdSchedulerFactory stdSchedulerFactory = new StdSchedulerFactory();
        Scheduler scheduler = stdSchedulerFactory.getScheduler();

        //2 创建JobDetail实例, 并与PrintWorksJod绑定
        JobDetail jobDetail = JobBuilder.newJob(PrintWordsJob.class)
                .usingJobData("jobDetail1", "这个Job用来测试的")
                .withIdentity("printWordsJob", "group1").build();

        //3 构建Trigger实例 , 每秒执行一次, 重复10
        //3.1 SimpleTrigger使用示例
//        Trigger trigger = TriggerBuilder.newTrigger().withIdentity("triggerName1", "triggerGroup1")
//                .usingJobData("trigger1", "这是jobDetail1的trigger")
//                .startNow()//立即生效
//                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
//                        .withIntervalInSeconds(1)//每隔1s执行一次
//                        .withRepeatCount(10))
//                .build();//一直执行

        //3.2 CronTrigger使用示例
        CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger1", "triggerGroup1")
                .usingJobData("trigger1", "这是jobDetail1的trigger")
                .startNow()//立即生效
//                .startAt(startDate)
//                .endAt(endDate)
                .withSchedule(CronScheduleBuilder.cronSchedule("* 30 10 ? * 1/5 2018"))
                .build();

        //4 执行
        Date date = scheduler.scheduleJob(jobDetail, trigger);
        System.out.println(date);
        System.out.println("--------scheduler start ! ------------");
        scheduler.start();

        //睡眠
        TimeUnit.MINUTES.sleep(1);
        scheduler.shutdown();
        System.out.println("--------scheduler shutdown ! ------------");

    }
}
