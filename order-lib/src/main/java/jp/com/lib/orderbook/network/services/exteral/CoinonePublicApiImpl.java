package jp.com.lib.orderbook.network.services.exteral;

import android.content.Context;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

import cz.msebera.android.httpclient.Header;
import jp.com.lib.orderbook.network.HttpSender;
import jp.com.lib.orderbook.network.datas.Orderbook;
import jp.com.lib.orderbook.network.datas.Orderbooks;
import jp.com.lib.orderbook.network.listeners.OrderBooksListener;
import jp.com.lib.orderbook.network.services.Constants;

/**
 * Created by jp on 16. 10. 12..
 */
public class CoinonePublicApiImpl implements PublickApi {
    private static final String BASE_URL = "https://api.coinone.co.kr/";
    private static final String ORDERBOOK_URL = "orderbook?currency=eth";

    private static final String INTERAL_ORDERBOOK_URL = "markets/coinone";
    private static Context mContext;
    private static CoinonePublicApiImpl mPublicAPI;

    private CoinonePublicApiImpl() {

    }

    public static CoinonePublicApiImpl getInstance(Context context) {
        mContext = context.getApplicationContext();
        if (mPublicAPI == null) {
            mPublicAPI = new CoinonePublicApiImpl();
        }
        return mPublicAPI;
    }

    @Override
    public void getInteralOrderbook(final OrderBooksListener listener) throws Exception {
        JsonHttpResponseHandler jsonHttpResponseHandler = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  JSONObject response) {
                try {


                    JSONArray datas = response.getJSONArray("data");
                    Orderbooks orderbooks = new Orderbooks();
                    for (int i = 0; i < datas.length(); i++) {
                        JSONObject data = datas.getJSONObject(i);
                        BigDecimal qty = new BigDecimal(data.getString("qty"));
                        BigDecimal price = new BigDecimal(data.getString("price"));
                        String type = data.getString("type");
                        Orderbook orderbook = new Orderbook();
                        orderbook.setPrice(price);
                        orderbook.setQty(qty);
                        if(type.equals("ask")){
                            orderbooks.addAsk(orderbook);
                        }else if(type.equals("bid")){
                            orderbooks.addBid(orderbook);
                        }
                    }



                    listener.onSuccess(orderbooks);

                } catch (JSONException e) {
                    listener.onFailure(statusCode, e);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  Throwable throwable, JSONObject jsonObject) {
                listener.onFailure(statusCode, throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String error, Throwable throwable) {
                listener.onFailure(statusCode, throwable);
            }
        };
        HttpSender.sendGetRequest(Constants.INTERAL_BASE_URL + "" + INTERAL_ORDERBOOK_URL, jsonHttpResponseHandler);
    }

    @Override
    public void getOrderbook(final OrderBooksListener listener) throws Exception {
        JsonHttpResponseHandler jsonHttpResponseHandler = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  JSONObject response) {
                try {

                    String success = response.getString("errorCode");
                    if (success.equals("0")) {


                        JSONArray bids = response.getJSONArray("bid");
                        JSONArray asks = response.getJSONArray("ask");
                        Orderbooks orderbooks = new Orderbooks();
                        for (int i = 0; i < bids.length(); i++) {
                            JSONObject bid = bids.getJSONObject(i);
                            BigDecimal qty = new BigDecimal(bid.getString("qty"));
                            BigDecimal price = new BigDecimal(bid.getString("price"));
                            Orderbook orderbook = new Orderbook();
                            orderbook.setPrice(price);
                            orderbook.setQty(qty);
                            orderbooks.addBid(orderbook);

                        }
                        for (int i = 0; i < asks.length(); i++) {
                            JSONObject ask = asks.getJSONObject(i);

                            BigDecimal qty = new BigDecimal(ask.getString("qty"));
                            BigDecimal price = new BigDecimal(ask.getString("price"));
                            Orderbook orderbook = new Orderbook();
                            orderbook.setPrice(price);
                            orderbook.setQty(qty);
                            orderbooks.addAsk(orderbook);
                        }

                        listener.onSuccess(orderbooks);
                    } else {
                        listener.onFailure(statusCode, new Exception("FAIL"));
                    }
                } catch (JSONException e) {
                    listener.onFailure(statusCode, e);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  Throwable throwable, JSONObject jsonObject) {
                listener.onFailure(statusCode, throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String error, Throwable throwable) {
                listener.onFailure(statusCode, throwable);
            }
        };
        HttpSender.sendGetRequest(BASE_URL + "" + ORDERBOOK_URL, jsonHttpResponseHandler);

    }
}