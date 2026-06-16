package fun.cyhgraph.service;

import fun.cyhgraph.dto.EvaluateSubmitDTO;
import fun.cyhgraph.result.PageResult;
import fun.cyhgraph.entity.StoreRatingStats;

import java.util.Map;

public interface EvaluateService {
    void submitEvaluate(EvaluateSubmitDTO dto);
    PageResult pageQuery(Long storeId, Integer page, Integer size, String filter, Long currentUserId);
    StoreRatingStats getStoreStats(Long storeId);
    Map<String, Object> toggleUseful(Long evaluateId, Long userId, Boolean useful);
    void deleteEvaluate(Long id, Long userId);
}