package top.dpdaidai.architect.quartz.quartzAndSpringboot;

import org.quartz.*;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @Author chenpantao
 * @Date 3/22/21 11:46 PM
 * @Version 1.0
 */
@Component
public class LeaveStartJob extends QuartzJobBean {
    private Scheduler scheduler;
//    private SystemUserMapperPlus systemUserMapperPlus;
//
//    @Autowired
//    public LeaveStartJob(Scheduler scheduler,
//                         SystemUserMapperPlus systemUserMapperPlus) {
//        this.scheduler = scheduler;
//        this.systemUserMapperPlus = systemUserMapperPlus;
//    }

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext)
            throws JobExecutionException {
        Trigger trigger = jobExecutionContext.getTrigger();
        JobDetail jobDetail = jobExecutionContext.getJobDetail();
        JobDataMap jobDataMap = jobDetail.getJobDataMap();
        // 将添加任务的时候存进去的数据拿出来
        long username = jobDataMap.getLongValue("username");
        LocalDateTime time = LocalDateTime.parse(jobDataMap.getString("time"));

        // 编写任务的逻辑

        // 执行之后删除任务
        try {
            // 暂停触发器的计时
            scheduler.pauseTrigger(trigger.getKey());
            // 移除触发器中的任务
            scheduler.unscheduleJob(trigger.getKey());
            // 删除任务
            scheduler.deleteJob(jobDetail.getKey());
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
}

