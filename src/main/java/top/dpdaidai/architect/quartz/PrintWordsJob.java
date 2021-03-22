package top.dpdaidai.architect.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * https://blog.csdn.net/noaman_wgs/article/details/80984873
 *
 * @Author chenpantao
 * @Date 3/22/21 11:16 AM
 * @Version 1.0
 */
public class PrintWordsJob implements Job {


    /**
     *
     * @param jobExecutionContext JobExecutionContext中包含了Quartz运行时的环境以及Job本身的详细数据信息。
     *                              当Schedule调度执行一个Job的时候，就会将JobExecutionContext传递给该Job的execute()中，
     *                              Job就可以通过JobExecutionContext对象获取信息。
     * @throws JobExecutionException
     */
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        System.out.println(jobExecutionContext.getJobDetail().getJobDataMap().get("jobDetail1"));
        System.out.println(jobExecutionContext.getTrigger().getJobDataMap().get("trigger1"));
        String printTime = new SimpleDateFormat("yy-MM-dd HH-mm-ss").format(new Date());
        System.out.println("PrintWordsJob start at:" + printTime + ", prints: Hello Job-" + new Random().nextInt(100));

    }
}
