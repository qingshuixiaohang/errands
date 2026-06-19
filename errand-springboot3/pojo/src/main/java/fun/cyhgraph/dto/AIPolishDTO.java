package fun.cyhgraph.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * <p>
 * makise
 * </p>
 *
 * @author makise
 * @since 2026/4/28
 */
@Data
public class AIPolishDTO {
    @NotBlank(message = "评价草稿不能为空哦")
    String context;
}
