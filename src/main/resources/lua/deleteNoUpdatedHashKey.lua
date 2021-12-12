local key = KEYS[1]
local hashkey = KEYS[2]
local value = ARGV[1]

-- 存在这个key
if redis.call("exists", key) == 1 then
    -- 获取hashkey
    local curValue = redis.call("hget", key, hashkey)
    -- 如果没有被更新，则删除并返回true
    if curValue == value then
        redis.call("hdel", key, hashkey)
        return "true"
    else
        return "false"
    end
else
    return "false"
end

