package fun.cyhgraph.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class EvaluateSubmitDTO {
    private Long userId;
    
    @NotNull(message = "店铺ID不能为空")
    private Long storeId;
    
    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "评分最小为1")
    @Max(value = 5, message = "评分最大为5")
    private Integer rating;
    
    @Min(value = 1, message = "口味评分最小为1")
    @Max(value = 5, message = "口味评分最大为5")
    private Integer tasteRating;
    
    @Min(value = 1, message = "服务评分最小为1")
    @Max(value = 5, message = "服务评分最大为5")
    private Integer serviceRating;
    
    @Min(value = 1, message = "环境评分最小为1")
    @Max(value = 5, message = "环境评分最大为5")
    private Integer environmentRating;
    
    @Min(value = 1, message = "性价比评分最小为1")
    @Max(value = 5, message = "性价比评分最大为5")
    private Integer costPerformanceRating;
    
    @NotBlank(message = "评价内容不能为空")
    private String content;
    
    private List<String> images;
}