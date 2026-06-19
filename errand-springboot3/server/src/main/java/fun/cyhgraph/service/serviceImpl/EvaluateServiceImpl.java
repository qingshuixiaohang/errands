package fun.cyhgraph.service.serviceImpl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import fun.cyhgraph.exception.EvaluateBusinessException;
import fun.cyhgraph.mapper.EvaluateMapper;
import fun.cyhgraph.mapper.EvaluateUsefulMapper;
import fun.cyhgraph.mapper.StoreRatingStatsMapper;
import fun.cyhgraph.dto.EvaluateSubmitDTO;
import fun.cyhgraph.result.PageResult;
import fun.cyhgraph.vo.EvaluateVO;
import fun.cyhgraph.vo.EvaluatePageVO;
import fun.cyhgraph.entity.Evaluate;
import fun.cyhgraph.entity.EvaluateUseful;
import fun.cyhgraph.entity.StoreRatingStats;
import fun.cyhgraph.service.EvaluateService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Service
public class EvaluateServiceImpl implements EvaluateService {

    @Autowired
    private EvaluateMapper evaluateMapper;
    @Autowired
    private StoreRatingStatsMapper statsMapper;
    @Autowired
    private EvaluateUsefulMapper usefulMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitEvaluate(EvaluateSubmitDTO dto) {
        Evaluate evaluate = new Evaluate();
        BeanUtils.copyProperties(dto, evaluate);
        
        if (dto.getImages() != null && !dto.getImages().isEmpty()) {
            evaluate.setImages(JSON.toJSONString(dto.getImages()));
        }
        
        evaluateMapper.insertEvaluate(evaluate);
        statsMapper.calculateAndUpdateStats(dto.getStoreId());
    }

    @Override
    public PageResult pageQuery(Long storeId, Integer page, Integer size, String filter, Long currentUserId) {
        PageHelper.startPage(page, size);
        
        List<Evaluate> evaluates = evaluateMapper.selectListByCondition(storeId, filter);
        PageInfo<Evaluate> pageInfo = new PageInfo<>(evaluates);
        
        List<EvaluateVO> voList = evaluates.stream().map(eval -> {
            EvaluateVO vo = new EvaluateVO();
            BeanUtils.copyProperties(eval, vo);
            
            if (eval.getImages() != null) {
                try {
                    vo.setImages(JSON.parseObject(eval.getImages(), new TypeReference<List<String>>(){}));
                } catch (Exception e) {
                    vo.setImages(new ArrayList<>());
                }
            }
            
            String userIdStr = eval.getUserId() != null ? eval.getUserId().toString() : "";
            String userName = "用户" + (userIdStr.length() >= 4 ? userIdStr.substring(0, 4) : userIdStr) + "***";
            vo.setUserName(userName);
            
            if (currentUserId != null) {
                int isUseful = usefulMapper.checkIsUseful(eval.getId(), currentUserId);
                vo.setIsUseful(isUseful > 0);
            } else {
                vo.setIsUseful(false);
            }
            
            EvaluateVO.DetailRating detailRating = new EvaluateVO.DetailRating();
            detailRating.setTaste(eval.getTasteRating());
            detailRating.setService(eval.getServiceRating());
            detailRating.setEnvironment(eval.getEnvironmentRating());
            detailRating.setCostPerformance(eval.getCostPerformanceRating());
            vo.setDetailRating(detailRating);
            
            return vo;
        }).collect(Collectors.toList());

        return new PageResult(pageInfo.getTotal(), voList);
    }

    @Override
    public StoreRatingStats getStoreStats(Long storeId) {
        return statsMapper.selectByStoreId(storeId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> toggleUseful(Long evaluateId, Long userId, Boolean useful) {
        Evaluate evaluate = evaluateMapper.selectById(evaluateId);
        if (evaluate == null) {
            throw new EvaluateBusinessException("评价不存在");
        }
        
        if (useful) {
            EvaluateUseful eu = new EvaluateUseful();
            eu.setEvaluateId(evaluateId);
            eu.setUserId(userId);
            if (usefulMapper.insert(eu) > 0) {
                evaluateMapper.updateUsefulCount(evaluateId, 1);
            }
        } else {
            if (usefulMapper.delete(evaluateId, userId) > 0) {
                evaluateMapper.updateUsefulCount(evaluateId, -1);
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        evaluate = evaluateMapper.selectById(evaluateId);
        if (evaluate != null) {
            result.put("usefulCount", evaluate.getUsefulCount());
        } else {
            result.put("usefulCount", 0);
        }
        int isUseful = usefulMapper.checkIsUseful(evaluateId, userId);
        result.put("isUseful", isUseful > 0);
        
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteEvaluate(Long id, Long userId) {
        Evaluate evaluate = evaluateMapper.selectById(id);
        if (evaluate == null) {
            throw new EvaluateBusinessException("评价不存在");
        }
        if (!evaluate.getUserId().equals(userId)) {
            throw new EvaluateBusinessException("无权删除他人的评价");
        }
        
        evaluateMapper.deleteById(id, userId);
        statsMapper.calculateAndUpdateStats(evaluate.getStoreId());
    }
}