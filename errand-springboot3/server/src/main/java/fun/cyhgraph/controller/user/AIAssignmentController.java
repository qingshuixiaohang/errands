package fun.cyhgraph.controller.user;

import fun.cyhgraph.result.Result;
import fun.cyhgraph.service.AIAssignmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("userAIAssignmentController")
@RequestMapping("/user/assignment")
@Slf4j
public class AIAssignmentController {

    @Autowired
    private AIAssignmentService aiAssignmentService;

    @PutMapping("/accept/{orderId}/{riderId}")
    public Result<String> accept(@PathVariable Integer orderId, @PathVariable Integer riderId) {
        log.info("骑手接受AI派单: orderId={}, riderId={}", orderId, riderId);
        aiAssignmentService.acceptAssignment(orderId, riderId);
        return Result.success();
    }

    @PutMapping("/reject/{orderId}")
    public Result<String> reject(@PathVariable Integer orderId) {
        log.info("骑手拒绝AI派单: orderId={}", orderId);
        aiAssignmentService.rejectAssignment(orderId);
        return Result.success();
    }
}
