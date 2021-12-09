package cug.cs.codercommunity.vo;

import cug.cs.codercommunity.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class QuestionVO {
    private Integer id;
    private String title;
    private String description;
    private Date gmtCreate;
    private Date gmtModified;
    private Integer creator;
    private Integer commentCount;
    private Integer viewCount;
    private Integer likeCount;
    private String tag;
    private Integer likeStatus;
    private User user;
}
