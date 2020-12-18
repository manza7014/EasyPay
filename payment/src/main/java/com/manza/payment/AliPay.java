package com.manza.payment;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.alipay.sdk.app.PayTask;
import com.manza.payment.bean.AliPayResult;

import java.lang.ref.WeakReference;
import java.util.Map;

/**
 * author : yongfeng.li
 * date : 2020/12/16 13:32
 * description :支付宝支付
 */
public class AliPay implements IPay {
    private static final String TAG = "AliPay";
    private static final int SDK_PAY_FLAG = 1;
    private static AliPay mInstance;
    private final WeakReference<Activity> activityWeakReference;
    private static PayHelper payHelper;

    private AliPay(WeakReference<Activity> weakReference) {
        this.activityWeakReference = weakReference;
    }

    public static AliPay getInstance(WeakReference<Activity> activityWeakReference) {
        if (mInstance == null) {
            synchronized (AliPay.class) {
                if (mInstance == null) {
                    mInstance = new AliPay(activityWeakReference);
                    payHelper = PayHelper.getInstance(activityWeakReference);
                }
            }
        }
        return mInstance;
    }

    public interface AlipayCallBack {
        void onAlipayResult();
    }

    public static class PayHandler extends Handler {
        public PayHandler(Looper mainLooper) {
            super(mainLooper);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == SDK_PAY_FLAG) {
                @SuppressWarnings("unchecked")
                AliPayResult payResult = new AliPayResult((Map<String, String>) msg.obj);
                /*
                  对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                 */
                String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                String resultStatus = payResult.getResultStatus();
                if (TextUtils.equals(resultStatus, "9000")) {
                    payHelper.invokePaySuccess();
                } else if (TextUtils.equals(resultStatus, "6001")) {
                    payHelper.invokePayCancel();
                } else {
                    payHelper.invokePayError();
                }
            }
        }
    }

    @Override
    public void pay(String orderInfo) {
        Runnable runnable = () -> {
            PayTask alipay = new PayTask(activityWeakReference.get());
            Map<String, String> result = alipay.payV2(orderInfo, true);
            Message msg = new Message();
            msg.what = SDK_PAY_FLAG;
            msg.obj = result;
            PayHandler mHandler = new PayHandler(Looper.getMainLooper());
            mHandler.sendMessage(msg);
        };
        new Thread(runnable).start();
    }
}
