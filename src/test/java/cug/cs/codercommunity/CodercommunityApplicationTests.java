package cug.cs.codercommunity;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@Slf4j
@SpringBootTest
class CodercommunityApplicationTests {

    @Autowired
    RedisTemplate<String,Object> redisTemplate;
    @Test
    void contextLoads() {
        Long increment = redisTemplate.opsForHash().increment("11", "2", 1);
        log.info("{}", increment);
    }

}
