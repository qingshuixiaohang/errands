package fun.cyhgraph.vo;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class EvaluateVO {
    private Long id;
    private Long userId;
    private String userName; // 需要联表或独立查询
    private Integer rating;
    private DetailRating detailRating;
    private String content;
    private List<String> images; // 返回给前端是 List
    private LocalDateTime createTime;
    private Integer usefulCount;
    private Boolean isUseful; // 当前用户是否点赞

    @Data
    public static class DetailRating {
        private Integer taste;
        private Integer service;
        private Integer environment;
        private Integer costPerformance;
    }
}