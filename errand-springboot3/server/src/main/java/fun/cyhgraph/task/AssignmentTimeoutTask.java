package fun.cyhgraph.task;

import fun.cyhgraph.service.AIAssignmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AssignmentTimeoutTask {

    @Autowired
    private AIAssignmentService aiAssignmentService;

    @Scheduled(cron = "0/10 * * * * ?")
    public void checkTimeout() {
        aiAssignmentService.checkTimeout();
    }
}
