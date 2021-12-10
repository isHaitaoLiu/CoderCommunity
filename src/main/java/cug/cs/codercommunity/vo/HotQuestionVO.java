package cug.cs.codercommunity.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: codercommunity
 * @description: 热门话题展示类
 * @author: Sakura
 * @create: 2021-12-09 16:25
 **/

@Data
@NoArgsConstructor
public class HotQuestionVO {
    private Integer id;
    private String title;
    private double score;
}
