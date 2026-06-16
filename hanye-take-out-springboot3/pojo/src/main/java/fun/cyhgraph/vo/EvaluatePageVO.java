package fun.cyhgraph.vo;
import fun.cyhgraph.result.PageResult;
import lombok.Data;
import java.util.List;

@Data
public class EvaluatePageVO {
    private List<EvaluateVO> list;
    private Integer total;
    private Integer page;
    private Integer size;
    private Boolean hasMore;

    public static EvaluatePageVO fromPageResult(PageResult pageResult, Integer page, Integer size) {
        EvaluatePageVO pageVO = new EvaluatePageVO();
        pageVO.setList((List<EvaluateVO>) pageResult.getRecords());
        pageVO.setTotal(pageResult.getTotal().intValue());
        pageVO.setPage(page);
        pageVO.setSize(size);
        // 计算是否有更多数据
        pageVO.setHasMore(page * size < pageResult.getTotal());
        return pageVO;
    }
}