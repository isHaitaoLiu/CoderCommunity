package cug.cs.codercommunity.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: codercommunity
 * @description: 点赞实体类
 * @author: Sakura
 * @create: 2021-11-28 20:45
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Like {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private Integer questionId;
    private Integer status;
    private Long gmtCreate;
    private Long gmtModified;
}
