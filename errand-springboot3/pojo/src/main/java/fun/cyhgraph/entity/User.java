package fun.cyhgraph.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {

    private Integer id;
    private String name;
    private String openid;
    private String phone;
    private Integer gender;
    private String idNumber;
    private String pic;
    private Integer riderStatus; // 骑手状态 0否 1空闲 2配送中
    private Double rating; // 骑手评分
    private Integer totalOrders; // 骑手完成单数
    private Double currentLat; // GPS纬度
    private Double currentLng; // GPS经度
    private LocalDateTime createTime;

}
