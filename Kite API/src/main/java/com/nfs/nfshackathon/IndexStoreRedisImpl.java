package com.nfs.nfshackathon;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class IndexStoreRedisImpl {
    private final RedisTemplate<String, Object> redisTemplate;

    private final Gson gson = new Gson();

    private final String INDEX_DATA = "INDEX_DATA";

    public void saveIndexData(IndexData indexData) {
        log.info("Saving indexID- " + indexData.getId());
        redisTemplate.opsForHash().put(INDEX_DATA, indexData.getId(), gson.toJson(indexData));
    }

    public List<IndexData> fetchIndexData() {
        Map<Object, Object> indexDataDb =
                redisTemplate.opsForHash().entries(INDEX_DATA);

        List<IndexData> indexDataList = new ArrayList<>();
        indexDataDb.values()
                .forEach( entry -> indexDataList.add(gson.fromJson((String) entry, IndexData.class)));

        return indexDataList;
    }

    public List<IndexData> fetchIndexDatav1(String ind_data) {
        Map<Object, Object> indexDataDb =
                redisTemplate.opsForHash().entries(ind_data);

        List<IndexData> indexDataList = new ArrayList<>();
        indexDataDb.values()
                .forEach( entry -> indexDataList.add(gson.fromJson((String) entry, IndexData.class)));

        return indexDataList;
    }
}
