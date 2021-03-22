package top.dpdaidai.architect.quartz.quartzAndSpringboot;

import com.alibaba.druid.pool.DruidDataSource;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * https://segmentfault.com/a/1190000022552084
 *
 * @Author chenpantao
 * @Date 3/22/21 11:27 PM
 * @Version 1.0
 */
@Configuration
public class QuartzConfig {

    @Bean
    public JobDetail jobDetail() {
        JobDetail jobDetail = JobBuilder.newJob(StartOfDayJob.class)
                .withIdentity("start_of_day", "start_of_day")
                .storeDurably()
                .build();
        return jobDetail;
    }

    @Bean
    public Trigger trigger() {
        Trigger trigger = TriggerBuilder.newTrigger()
                .forJob(jobDetail())
                .withIdentity("start_of_day", "start_of_day")
                .startNow()
                // 每天0点执行
                .withSchedule(CronScheduleBuilder.cronSchedule("0 * * * * ?"))
                .build();
        return trigger;
    }

    @Value("${spring.datasource.master.url}")
    private String url;

    @Value("${spring.datasource.master.username}")
    private String username;

    @Value("${spring.datasource.master.password}")
    private String password;

    @Value("${spring.datasource.master.driverClassName}")
    private String driverClassName;

    //配置主数据源
    @Bean(name = "masterDataSource")
    @Primary //指定为默认数据源
    public DataSource masterDataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }

    @Bean(name = "schedulerFactoryBean")
    public SchedulerFactoryBean scheduler(@Qualifier("masterDataSource") DataSource dataSource) {

        //quartz参数
        Properties prop = new Properties();
        //配置实例
        prop.put("org.quartz.scheduler.instanceName", "myScheduler");//实例名称
        prop.put("org.quartz.scheduler.instanceId", "AUTO");
        //线程池配置
        prop.put("org.quartz.threadPool.threadCount", "50");//线程池大小
        //JobStore配置
        prop.put("org.quartz.jobStore.class", "org.quartz.impl.jdbcjobstore.JobStoreTX");
        prop.put("org.quartz.jobStore.tablePrefix", "QRTZ_");
        prop.put("org.quartz.jobStore.misfireThreshold", "900000");//trigger被认为失败之前，scheduler能够承受的下一次触发时间（单位毫秒）

        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setDataSource(dataSource);
        factory.setQuartzProperties(prop);
        factory.setSchedulerName("MyScheduler");//数据库中存储的名字
        //QuartzScheduler 延时启动，应用启动5秒后 QuartzScheduler 再启动
        factory.setStartupDelay(5);

        //factory.setApplicationContextSchedulerContextKey("applicationContextKey");
        //可选，QuartzScheduler 启动时更新己存在的Job，这样就不用每次修改targetObject后删除qrtz_job_details表对应记录了
        factory.setOverwriteExistingJobs(true);
        //设置自动启动，默认为true
        factory.setAutoStartup(true);

        return factory;
    }
}
