package top.dpdaidai.architect.quartz.quartzAndSpringboot;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

/**
 * @Author chenpantao
 * @Date 3/22/21 11:43 PM
 * @Version 1.0
 */
@Component
public class StartOfDayJob extends QuartzJobBean {
//    private StudentService studentService;

//    @Autowired
//    public StartOfDayJob(StudentService studentService) {
//        this.studentService = studentService;
//    }

    private Scheduler scheduler;


    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext)
            throws JobExecutionException {
        // 任务的具体逻辑
        System.out.println("job 执行");

    }
}
