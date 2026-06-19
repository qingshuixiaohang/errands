package fun.cyhgraph.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiAssignmentLog implements Serializable {
    private Integer id;
    private Integer orderId;
    private String candidates;       // 候选骑手列表(JSON)
    private Integer selectedRiderId; // 选中的骑手ID
    private String selectionMethod;  // 选择方式: direct/ai_assist
    private String reason;           // 选择理由
    private String riderResponse;    // 骑手响应: pending/accepted/rejected/timeout
    private LocalDateTime responseTime;
    private LocalDateTime assignmentTime;
}
