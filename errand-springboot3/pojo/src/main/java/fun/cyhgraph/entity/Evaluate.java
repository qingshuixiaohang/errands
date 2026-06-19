package fun.cyhgraph.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Evaluate {
    private Long id;
    private Long userId;
    private Long storeId;
    private Integer rating;
    private Integer tasteRating;
    private Integer serviceRating;
    private Integer environmentRating;
    private Integer costPerformanceRating;
    private String content;
    private String images; // 数据库存 JSON 字符串
    private LocalDateTime createTime;
    private Integer usefulCount;
    private Integer status;
}