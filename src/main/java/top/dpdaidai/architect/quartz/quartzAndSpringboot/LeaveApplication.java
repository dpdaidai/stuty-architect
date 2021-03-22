package top.dpdaidai.architect.quartz.quartzAndSpringboot;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author chenpantao
 * @Date 3/22/21 11:44 PM
 * @Version 1.0
 */
@Data
public class LeaveApplication {
    private Integer id;
    private Long proposerUsername;
    @JsonFormat( pattern = "yyyy-MM-dd HH:mm",timezone="GMT+8")
    private LocalDateTime startTime;
    @JsonFormat( pattern = "yyyy-MM-dd HH:mm",timezone="GMT+8")
    private LocalDateTime endTime;
    private String reason;
    private String state;
    private String disapprovedReason;
    private Long checkerUsername;
    private LocalDateTime checkTime;

    // 省略getter、setter
}

