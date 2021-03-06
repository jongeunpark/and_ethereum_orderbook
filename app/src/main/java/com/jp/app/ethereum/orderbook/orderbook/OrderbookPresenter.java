package com.jp.app.ethereum.orderbook.orderbook;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.jp.app.ethereum.orderbook.data.OrderItem;

import jp.com.lib.orderbook.network.datas.LastPrice;
import jp.com.lib.orderbook.network.datas.Orderbook;
import jp.com.lib.orderbook.network.datas.Orderbooks;

/**
 * Created by jp on 16. 10. 21..
 */
public class OrderbookPresenter implements OrderbookContract.Presenter {
    private static final int MAX = 7;
    private List<OrderItem> orderItemList;
    private List<LastPrice> mPriceItemList;
    private int mErrorCode = -1;
    private OrderbookContract.View mOrderbookView;

    public OrderbookPresenter(@NonNull OrderbookContract.View orderbookView) {

        this.mOrderbookView = orderbookView;
        mOrderbookView.setPresenter(this);
    }


    @Override
    public void start() {

    }

    @Override
    public void setOrderbook(Orderbooks orderbooks) {
        if (orderbooks != null && orderbooks.getAskArray().size() >= MAX && orderbooks.getBidArray().size() >= MAX) {
            Collections.sort(orderbooks.getAskArray(), new Comparator<Orderbook>() {
                @Override
                public int compare(Orderbook t1, Orderbook t2) {

                    return t2.getPrice().compareTo(t1.getPrice());
                }
            });

            Collections.sort(orderbooks.getBidArray(), new Comparator<Orderbook>() {
                @Override
                public int compare(Orderbook t1, Orderbook t2) {
                    return t2.getPrice().compareTo(t1.getPrice());
                }
            });

            orderItemList = new ArrayList<OrderItem>();
            int end = orderbooks.getAskArray().size();
            int start = end - MAX;

            for (Orderbook orderbook : orderbooks.getAskArray().subList(start, end)) {


                orderItemList.add(new OrderItem(orderbook.getPrice(), orderbook.getQty(), OrderItem.ORDER_TYPE.ASK));
            }
            start = 0;
            end = MAX;
            for (Orderbook orderbook : orderbooks.getBidArray().subList(start, end)) {

                orderItemList.add(new OrderItem(orderbook.getPrice(), orderbook.getQty(), OrderItem.ORDER_TYPE.BID));
            }


            if (mOrderbookView.isActive()) {
                mOrderbookView.drawList(orderItemList);

            }
        }
    }

    @Override
    public void setPrice(List<LastPrice> priceItemList) {
        mPriceItemList = priceItemList;
        if (mOrderbookView.isActive()) {
            mOrderbookView.drawDataPrice(priceItemList);

        }
    }

    @Override
    public void setError(int erroCode) {
        mErrorCode = erroCode;
        if (mOrderbookView.isActive()) {
            mOrderbookView.drawError(erroCode);
        }
    }

    @Override
    public void clearData() {
        orderItemList = null;
    }

    @Override
    public void clearPriceData() {
        mPriceItemList = null;
    }

    @Override
    public void drawListAvailable() {
        if(orderItemList != null && orderItemList.size() > 0){
            mOrderbookView.drawList(orderItemList);
        }else{
            if(mErrorCode != -1) {
                mOrderbookView.drawError(mErrorCode);
            }
        }

    }

    @Override
    public void drawPriceAvailable() {
        if(mPriceItemList != null && mPriceItemList.size() > 0){
            mOrderbookView.drawDataPrice(mPriceItemList);
        }else{
            if(mErrorCode != -1) {
                mOrderbookView.drawError(mErrorCode);
            }
        }
    }
}
