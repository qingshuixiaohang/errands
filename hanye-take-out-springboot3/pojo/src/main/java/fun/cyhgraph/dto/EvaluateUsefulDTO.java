package fun.cyhgraph.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EvaluateUsefulDTO {
    @NotNull(message = "评价ID不能为空")
    private Long evaluateId;
    
    @NotNull(message = "点赞状态不能为空")
    private Boolean useful;
}