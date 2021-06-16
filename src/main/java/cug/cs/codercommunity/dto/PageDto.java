package cug.cs.codercommunity.dto;

import cug.cs.codercommunity.vo.QuestionVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class PageDto<T> {
    private List<T> data;
    private boolean showPrevious;
    private boolean showFirstPage;
    private boolean showNext;
    private boolean showLastPage;
    private Integer currentPage;
    private List<Integer> pages;
    private Integer totalPage;
}
