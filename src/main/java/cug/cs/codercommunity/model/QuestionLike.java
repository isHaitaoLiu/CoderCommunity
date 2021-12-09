package cug.cs.codercommunity.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @program: codercommunity
 * @description: 点赞实体类
 * @author: Sakura
 * @create: 2021-11-28 20:45
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionLike {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private Integer questionId;
    private Integer status;
    private Date gmtCreate;
    private Date gmtModified;
}
