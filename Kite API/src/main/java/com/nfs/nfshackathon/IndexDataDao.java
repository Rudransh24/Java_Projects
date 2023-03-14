package com.nfs.nfshackathon;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
@Slf4j
public class IndexDataDao extends BaseZsetDao<String> {

  private static final String INDEX_DATA_ZSET = "INDEX_DATA_ZERODHA_V2";
  private final Gson gson = new Gson();

  public void save(IndexData indexData) {
    log.info("Saving indexID- " + indexData.getId());
    save(INDEX_DATA_ZSET, gson.toJson(indexData), indexData.getId());
  }

  public Long delete(String key, String value) {
    return deleteValueZset(key, value);
  }

  public Set<String> fetchData(String indexData, int from, int to) {
    return fetchRangeDesc(indexData, from, to);
  }
}
