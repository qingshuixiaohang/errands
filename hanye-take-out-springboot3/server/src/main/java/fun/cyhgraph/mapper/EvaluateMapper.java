package fun.cyhgraph.mapper;

import fun.cyhgraph.entity.Evaluate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface EvaluateMapper {
    // 插入评价
    int insertEvaluate(Evaluate evaluate);
    
    // 条件查询评价列表 (供 PageHelper 拦截)
    List<Evaluate> selectListByCondition(@Param("storeId") Long storeId, @Param("filter") String filter);
    
    // 根据ID查询评价
    Evaluate selectById(@Param("id") Long id);
    
    // 删除评价 (逻辑删除或物理删除)
    int deleteById(@Param("id") Long id, @Param("userId") Long userId);
    
    // 更新点赞数
    int updateUsefulCount(@Param("id") Long id, @Param("num") int num);
}