package fun.cyhgraph.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class EvaluateUseful {
    private Long evaluateId;
    private Long userId;
    private LocalDateTime createTime;
}