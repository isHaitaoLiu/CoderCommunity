package cug.cs.codercommunity;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.Arrays;

@Slf4j
@SpringBootTest
class CodercommunityApplicationTests {

    @Autowired
    RedisTemplate<String,Object> redisTemplate;
    @Test
    void contextLoads() {
        RedisScript<Boolean> redisScript = RedisScript.of(new ClassPathResource("lua/deleteNoUpdatedHashKey.lua"), Boolean.class);
        Boolean execute = redisTemplate.execute(redisScript, Arrays.asList("MAP", "key1"), 1);
        log.info("----- {} ----", execute);
    }

}
