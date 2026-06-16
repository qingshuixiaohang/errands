package fun.cyhgraph.mapper;

import fun.cyhgraph.entity.StoreRatingStats;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface StoreRatingStatsMapper {
    // 获取店铺统计信息
    StoreRatingStats selectByStoreId(@Param("storeId") Long storeId);
    
    // 重新计算并更新店铺统计信息 (核心复杂 SQL)
    int calculateAndUpdateStats(@Param("storeId") Long storeId);
}