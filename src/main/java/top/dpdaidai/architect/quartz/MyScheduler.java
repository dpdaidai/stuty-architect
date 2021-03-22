package top.dpdaidai.architect.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 转载
 * https://blog.csdn.net/noaman_wgs/article/details/80984873
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
