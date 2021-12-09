package cug.cs.codercommunity.model;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class User {
    @TableId(value="id",type= IdType.AUTO)
    private Integer id;
    private String accountId;
    private String name;
    private String token;
    private Date gmtCreate;
    private Date gmtModified;
    private String avatarUrl;
}
