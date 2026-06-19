package fun.cyhgraph.controller.user;

import fun.cyhgraph.dto.EvaluateSubmitDTO;
import fun.cyhgraph.dto.EvaluateUsefulDTO;
import fun.cyhgraph.exception.EvaluateBusinessException;
import fun.cyhgraph.result.PageResult;
import fun.cyhgraph.result.Result;
import fun.cyhgraph.entity.StoreRatingStats;
import fun.cyhgraph.service.EvaluateService;
import fun.cyhgraph.context.BaseContext;
import fun.cyhgraph.vo.EvaluatePageVO;
import fun.cyhgraph.vo.EvaluateVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/evaluate")
public class EvaluateController {

    @Autowired
    private EvaluateService evaluateService;

    private Long getCurrentUserId() {
        Integer userId = BaseContext.getCurrentId();
        if (userId == null) {
            //throw new EvaluateBusinessException("用户未登录");
            userId = 1;
        }
        return userId.longValue();
    }

    @PostMapping("/submit")
    public Result submit(@Valid @RequestBody EvaluateSubmitDTO dto) {

        Long userId = getCurrentUserId();
        dto.setUserId(userId);
        evaluateService.submitEvaluate(dto);
        return Result.success("评价提交成功");
    }

    @GetMapping("/list")
    public Result<EvaluatePageVO> list(
            @RequestParam Long storeId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "all") String filter) {
        
        Long userId = getCurrentUserId();
        PageResult pageResult = evaluateService.pageQuery(storeId, page, size, filter, userId);
        
        EvaluatePageVO pageVO = EvaluatePageVO.fromPageResult(pageResult, page, size);
        
        return Result.success(pageVO);
    }

    @GetMapping("/stats/{storeId}")
    public Result<StoreRatingStats> stats(@PathVariable Long storeId) {
        StoreRatingStats stats = evaluateService.getStoreStats(storeId);
        return Result.success(stats);
    }

    @PostMapping("/useful")
    public Result<Map<String, Object>> useful(@Valid @RequestBody EvaluateUsefulDTO dto) {
        Long userId = getCurrentUserId();
        Map<String, Object> result = evaluateService.toggleUseful(dto.getEvaluateId(), userId, dto.getUseful());
        return Result.success(result);
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        evaluateService.deleteEvaluate(id, userId);
        return Result.success("删除成功");
    }
}