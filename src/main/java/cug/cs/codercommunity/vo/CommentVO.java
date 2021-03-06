package cug.cs.codercommunity.vo;

import cug.cs.codercommunity.model.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@NoArgsConstructor
@Data
public class CommentVO {
    private Integer id;
    private Integer parentId;
    private Integer type;
    private Integer commentator;
    private Date gmtCreate;
    private Date gmtModified;
    private Integer likeCount;
    private String content;
    private User user;
    //当前用户对该评论的点赞状态
    private Integer likeStatus;
}
