package top.dpdaidai.architect.quartz.quartzAndSpringboot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * @Author chenpantao
 * @Date 3/23/21 12:22 AM
 * @Version 1.0
 */
@RequestMapping("/testQuartz")
@RestController
public class QuartzTestController {

    @Autowired
    LeaveApplicationServiceImpl leaveApplicationService;

    @RequestMapping("/hello")
    public String Hello() {
        LeaveApplication leaveApplication = new LeaveApplication();
        leaveApplication.setId(1);
        leaveApplication.setProposerUsername(1234L);
        leaveApplication.setStartTime(LocalDateTime.of(2021, 3, 25, 0, 0, 17));
        leaveApplication.setEndTime(LocalDateTime.of(2021, 3, 26, 0, 0, 17));
        leaveApplication.setReason("病假");
        leaveApplication.setState("");
        leaveApplication.setDisapprovedReason("");
        leaveApplication.setCheckerUsername(11L);
        leaveApplication.setCheckTime(LocalDateTime.of(2021, 3, 23, 0, 0, 17));
        leaveApplicationService.addJobAndTrigger(leaveApplication);
        return "hello CI test";
    }


}
