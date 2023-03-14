package com.nfs.nfshackathon;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.HistoricalData;
import com.zerodhatech.models.Profile;
import com.zerodhatech.models.Tick;
import com.zerodhatech.models.User;
import com.zerodhatech.ticker.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@Slf4j
@RequiredArgsConstructor
public class LiveDataController {
  KiteConnect kiteSdk;
  private final IndexStoreRedisImpl indexStoreRedis;
  private final ObjectMapper objectMapper;
  private final Gson gson = new Gson();
  private final IndexDataDao indexDataDao;
  Integer z_id = 1;

  public void getOHLC(KiteConnect kiteConnect) throws KiteException, IOException {
    String[] instruments = {"256265", "BSE:INFY", "NSE:INFY", "NSE:NIFTY 50"};
    System.out.println(kiteConnect.getOHLC(instruments).get("256265").lastPrice);
    System.out.println(kiteConnect.getOHLC(instruments).get("NSE:NIFTY 50").ohlc.open);
  }

  public void getLTP(KiteConnect kiteConnect) throws KiteException, IOException {
    String[] instruments = {"256265", "BSE:INFY", "NSE:INFY", "NSE:NIFTY 50"};
    System.out.println(kiteConnect.getLTP(instruments).get("256265").lastPrice);
  }

  public void getHistoricalData(KiteConnect kiteConnect) throws KiteException, IOException {
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Date from = new Date();
    Date to = new Date();
    try {
      from = formatter.parse("2019-09-20 09:15:00");
      to = formatter.parse("2019-09-20 15:30:00");
    } catch (ParseException e) {
      e.printStackTrace();
    }
    HistoricalData historicalData =
        kiteConnect.getHistoricalData(from, to, "54872327", "15minute", false, true);
    System.out.println(historicalData.dataArrayList.size());
    System.out.println(historicalData.dataArrayList.get(0).volume);
    System.out.println(
        historicalData.dataArrayList.get(historicalData.dataArrayList.size() - 1).volume);
    System.out.println(historicalData.dataArrayList.get(0).oi);
  }

  @RequestMapping("/connect")
  public void connectZerodha() {
    String req_token = "req_token";
    String api_key = "api_key";
    String sec_key = "sec_key";
    kiteSdk = new KiteConnect(api_key);
    kiteSdk.setUserId("user_id");
    User users = null;
    try {
      users = kiteSdk.generateSession(req_token, sec_key);
    } catch (Exception e) {
      // log.error("Response: {}", webClient);
      log.error("Process Stopped");
    } catch (KiteException ex) {
      log.error("Process Stopped");
    }
    kiteSdk.setAccessToken(users.accessToken);
    kiteSdk.setPublicToken(users.publicToken);
    Profile profile;
    try {
      profile = kiteSdk.getProfile();
      System.out.println(profile.userName);
    } catch (IOException | KiteException | JSONException e) {
      Logger.getLogger(NfsHackathonApplication.class.getName()).log(Level.SEVERE, null, e);
    }

    KiteTicker tickerProvider = new KiteTicker(kiteSdk.getAccessToken(), kiteSdk.getApiKey());
    ArrayList<Long> tokens = new ArrayList<>();
    tokens.add(256265L);
    log.info("Tokens: {}", tokens);

    tickerProvider.connect();

    tickerProvider.subscribe(tokens);
    tickerProvider.setMode(tokens, KiteTicker.modeFull);

    tickerProvider.setOnConnectedListener(
        new OnConnect() {
          @Override
          public void onConnected() {
            tickerProvider.subscribe(tokens);
            tickerProvider.setMode(tokens, KiteTicker.modeFull);
          }
        });

    tickerProvider.setOnDisconnectedListener(
        new OnDisconnect() {
          @Override
          public void onDisconnected() {
            // tickerProvider.unsubscribe(tokens);

            // After using com.zerodhatech.com.zerodhatech.ticker, close websocket connection.
            // tickerProvider.disconnect();
          }
        });

    tickerProvider.setOnErrorListener(
        new OnError() {
          @Override
          public void onError(Exception exception) {
            System.out.println(exception);
          }

          @Override
          public void onError(KiteException kiteException) {
            System.out.println(kiteException);
          }

          @Override
          public void onError(String error) {
            System.out.println(error);
          }
        });

    tickerProvider.setOnTickerArrivalListener(
        new OnTicks() {
          @Override
          public void onTicks(ArrayList<Tick> ticks) {
            NumberFormat formatter = new DecimalFormat();
            System.out.println("Ticks size: " + ticks.size());
            if (ticks.size() > 0) {
              System.out.println("Last Price: " + ticks.get(0).getLastTradedPrice());
              System.out.println("Change: " + formatter.format(ticks.get(0).getChange()));
              System.out.println("Tick Timestamp " + ticks.get(0).getTickTimestamp());
              System.out.println("Zerodha Timestamp " + LocalDateTime.now());
              IndexData indexData = new IndexData();
              indexData.setId(z_id);
              indexData.setExchangeZtimestamp(ticks.get(0).getTickTimestamp().toString());
              indexData.setValueZ(ticks.get(0).getLastTradedPrice());
              indexData.setZTimestamp(LocalDateTime.now().toString());
              // indexStoreRedis.saveIndexData(indexData);
              indexDataDao.save(indexData);
              z_id += 1;
            }
          }
        });

    tickerProvider.setTryReconnection(true);

    try {
      tickerProvider.setMaximumRetries(10);
    } catch (KiteException e) {
      log.error("{}", e);
    }

    try {
      tickerProvider.setMaximumRetryInterval(30);
    } catch (KiteException e) {
      log.error("{}", e);
    }

    boolean isConnected = tickerProvider.isConnectionOpen();
    System.out.println(isConnected);
  }

  @RequestMapping("/transfer_data")
  public void transferData() {
    List<IndexData> indexDataList = indexStoreRedis.fetchIndexData();
    indexDataList.forEach(
        element -> {
          indexDataDao.save(element);
        });
  }

  @RequestMapping("/fetch_data")
  public void fetchData() {
    Set<String> zerodhaData = indexDataDao.fetchData("INDEX_DATA_ZERODHA", 0, 10000);
    List<IndexData> finalData = new ArrayList<>();
    zerodhaData.forEach(
        element -> {
          try {
            IndexData indexDto = objectMapper.readValue(element, IndexData.class);
            System.out.println(indexDto);
            IndexData indexData = new IndexData();
            System.out.println(indexDto.getExchangeZtimestamp());
            System.out.println(indexDto.getZTimestamp());
            indexData.setZTimestamp(indexDto.getZTimestamp());
            indexData.setExchangeZtimestamp(indexDto.getExchangeZtimestamp());
            finalData.add(indexData);
          } catch (Exception E) {
            log.error("Error processing Data");
          }
        });

    Set<String> gData = indexDataDao.fetchData("INDEX_DATA_G", 0, 10000);
    gData.forEach(
        element -> {
          try {
            IndexData indexDto = objectMapper.readValue(element, IndexData.class);
            System.out.println(indexDto.getGTimestamp());
            for (IndexData element1 : finalData) {
              if (element1.getExchangeZtimestamp() == indexDto.getExchangeZtimestamp()) {
                element1.setGTimestamp(indexDto.getGTimestamp());
                finalData.add(element1);
              }
            }
          } catch (Exception E) {
            log.error("Error processing Data");
          }
        });

    FileOutputStream fos = null;
    try {
      fos = new FileOutputStream("allDetails.txt", true);
    } catch (FileNotFoundException e) {
      log.error("File cannot be made");
    }
    List<JsonObject> jsonObject = new ArrayList<>();
    for (IndexData element : finalData) {
      JsonObject jsonObject1 = new JsonObject();
      System.out.println(element.getGTimestamp());
      // jsonObject1.add("exchangeTimestamp", gson.toJson(element.getGTimestamp())));
      jsonObject.add(jsonObject1);
    }
  }
}
