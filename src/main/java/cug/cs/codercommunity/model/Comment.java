package cug.cs.codercommunity.model;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
@Data
public class Comment {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private Integer parentId;
    private Integer type;
    private Integer commentator;
    private Date gmtCreate;
    private Date gmtModified;
    private Integer likeCount;
    private String content;
}
