package fun.cyhgraph.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentLogVO implements Serializable {
    private Integer logId;
    private Integer orderId;
    private String orderNumber;
    private BigDecimal orderAmount;
    private String address;
    private String consignee;
    private String selectedRiderName;
    private String selectionMethod;    // direct/ai_assist
    private String reason;
    private String riderResponse;      // pending/accepted/rejected/timeout
    private String orderStatus;        // 订单状态文本
}
