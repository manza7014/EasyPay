package com.manza.payment;

/**
 * author : yongfeng.li
 * date : 2020/12/16 17:49
 * description :
 */
public interface PayListener {
    void onSuccess();

    void onError();

    void onCancel();
}
