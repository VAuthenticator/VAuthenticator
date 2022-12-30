package it.valeriovaudi.vauthenticator.cache

import it.valeriovaudi.vauthenticator.extentions.toSha256
import org.springframework.data.redis.core.RedisTemplate
import java.time.Duration
import java.util.*

class RedisCacheOperation<K, O>(
    private val cacheName: String,
    private val redisTemplate: RedisTemplate<String, O>
) : CacheOperation<K, O> {
    override fun get(key: K): Optional<O> {
        val keyAsString = getKeyAsStringFor(key)
        val valueFromRedis = redisTemplate.opsForHash<String, O>().get(keyAsString, keyAsString.toSha256())
        return Optional.ofNullable(valueFromRedis)
    }

    override fun put(key: K, value: O, ttl: Duration) {
        val keyAsString = getKeyAsStringFor(key)
        redisTemplate.opsForHash<String, O>().put(keyAsString, keyAsString.toSha256(), value)
        redisTemplate.opsForHash<String, O>().operations.expire(keyAsString, ttl)
    }


    override fun evict(key: K) {
        TODO("Not yet implemented")
    }

    private fun getKeyAsStringFor(key: K) = "${cacheName}_${key.toString()}"
}