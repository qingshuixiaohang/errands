package fun.cyhgraph.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class StoreRatingStats {
    private Long storeId;
    private Double totalRating;
    private Integer totalCount;
    private Double tasteRating;
    private Double serviceRating;
    private Double environmentRating;
    private Double costPerformanceRating;
    private Integer highCount;
    private Integer middleCount;
    private Integer lowCount;
    private Integer imageCount;
    private LocalDateTime updateTime;
}