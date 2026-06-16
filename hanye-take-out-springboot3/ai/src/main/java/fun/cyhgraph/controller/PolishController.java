package fun.cyhgraph.controller;
import fun.cyhgraph.dto.AIPolishDTO;
import fun.cyhgraph.result.Result;
import fun.cyhgraph.service.AiEvaluateAssistant;
import fun.cyhgraph.vo.AiPolishVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * makise
 * </p>
 *
 * @author makise
 * @since 2026/4/28
 */
@RestController
public class PolishController {
    @Autowired
    private AiEvaluateAssistant aiEvaluateAssistant;

    @PostMapping("polish")
    public Result<AiPolishVO> polish(@RequestBody AIPolishDTO aiPolishDTO){
        String context = aiPolishDTO.getContext();
        String polish = aiEvaluateAssistant.polish(context);
        AiPolishVO aiPolishVO=new AiPolishVO();
        aiPolishVO.setContext(polish);
        return Result.success(aiPolishVO);
    }
}
