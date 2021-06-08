package cug.cs.codercommunity.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CommentDto {
    private Integer parentId;
    private String content;
    private Integer type;
}
