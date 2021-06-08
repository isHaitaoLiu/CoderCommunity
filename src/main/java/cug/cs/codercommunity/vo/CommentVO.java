package cug.cs.codercommunity.vo;

import cug.cs.codercommunity.model.User;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Data
public class CommentVO {
    private Integer id;
    private Integer parentId;
    private Integer type;
    private Integer commentator;
    private Long gmtCreate;
    private Long gmtModified;
    private Integer likeCount;
    private String content;
    private User user;
}
