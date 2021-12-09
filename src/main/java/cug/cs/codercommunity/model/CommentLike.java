package cug.cs.codercommunity.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @program: codercommunity
 * @description: 评论点赞表
 * @author: Sakura
 * @create: 2021-12-05 21:11
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentLike {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private Integer commentId;
    private Integer status;
    private Date gmtCreate;
    private Date gmtModified;
}
