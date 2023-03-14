package com.nfs.nfshackathon;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class BaseZsetDao<T> {

  @Autowired private RedisTemplate<String, Object> redisTemplate;

  protected Set<T> fetchAll(String key) {
    return fetchRange(key, 0, 0);
  }

  protected Set<T> fetchAllDesc(String key) {
    return fetchRangeDesc(key, 0, 0);
  }

  protected Set<T> fetchRange(String key, int from, int to) {
    Set<Object> data = redisTemplate.opsForZSet().range(key, from, to - 1);
    if (CollectionUtils.isEmpty(data)) {
      return Collections.emptySet();
    }
    return data.stream()
        .map(value -> (T) value)
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  protected Set<T> fetchRangeDesc(String key, int from, int to) {
    Set<Object> data = redisTemplate.opsForZSet().reverseRange(key, from, to - 1);
    if (CollectionUtils.isEmpty(data)) {
      return Collections.emptySet();
    }
    return data.stream()
        .map(value -> (T) value)
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  protected Set<Pair<T, Double>> fetchRangeDescWithScore(String key, int from, int to) {
    Set<ZSetOperations.TypedTuple<Object>> data =
        redisTemplate.opsForZSet().reverseRangeWithScores(key, from, to - 1);
    if (CollectionUtils.isEmpty(data)) {
      return Collections.emptySet();
    }
    return data.stream()
        .map(tuple -> Pair.of((T) tuple.getValue(), tuple.getScore()))
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  protected Boolean save(String key, T value, double score) {
    return redisTemplate.opsForZSet().add(key, value, score);
  }

  protected Boolean delete(String key) {
    return redisTemplate.unlink(key);
  }

  protected void renameKey(String oldKey, String newKey) {
    redisTemplate.rename(oldKey, newKey);
  }

  protected Long deleteValueZset(String key, String value) {
    return redisTemplate.opsForZSet().remove(key, value);
  }
}
