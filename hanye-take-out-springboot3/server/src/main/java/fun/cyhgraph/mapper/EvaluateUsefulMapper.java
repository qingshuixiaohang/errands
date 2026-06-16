package fun.cyhgraph.mapper;

import fun.cyhgraph.entity.EvaluateUseful;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface EvaluateUsefulMapper {
    int insert(EvaluateUseful useful);
    int delete(@Param("evaluateId") Long evaluateId, @Param("userId") Long userId);
    int checkIsUseful(@Param("evaluateId") Long evaluateId, @Param("userId") Long userId);
}