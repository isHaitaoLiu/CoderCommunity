package cug.cs.codercommunity.cache;


import cug.cs.codercommunity.dto.HotTopicsDto;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Data
public class HotTopicCache {
    private Map<String, Integer> tags = new HashMap<>();
    private List<String> hots = new ArrayList<>();
    private Integer hotNum = 5;

    public void setHotNum(Integer hotNum) {
        this.hotNum = hotNum;
    }

    public void updateTop(){
        hots.clear();
        PriorityQueue<HotTopicsDto> pq = new PriorityQueue<>(hotNum, Comparator.comparingInt(HotTopicsDto::getPriority));

        tags.forEach((name, priority) -> {
            HotTopicsDto hotTopicsDto = new HotTopicsDto();
            hotTopicsDto.setName(name);
            hotTopicsDto.setPriority(priority);
            pq.offer(hotTopicsDto);
            if (pq.size() > hotNum){
                pq.poll();
            }
        });
        while (!pq.isEmpty()){
            hots.add(0, pq.poll().getName());
        }
        //System.out.println(hots);
    }
}
