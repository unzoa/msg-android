package io.crim.android.demo.repository;

;

import io.crim.android.ouicore.net.RXRetrofit.Exception.RXRetrofitException;
import io.crim.android.ouicore.net.bage.Base;
import io.crim.android.ouicore.net.bage.GsonHel;
import io.reactivex.Observable;
import io.reactivex.functions.Function;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

import retrofit2.http.Body;

import retrofit2.http.POST;

public interface CRIMService {
    @POST("account/login")
    Observable<ResponseBody> login(@Body RequestBody requestBody);

    @POST("account/register")
    Observable<ResponseBody> register(@Body RequestBody requestBody);

    @POST("user/find/full")
    Observable<ResponseBody> getUsersFullInfo(@Body RequestBody requestBody);

    @POST("user/update")
    Observable<ResponseBody> updateUserInfo(@Body RequestBody requestBody);

    @POST("account/code/send")
    Observable<ResponseBody> getVerificationCode(@Body RequestBody requestBody);

    @POST("account/code/verify")
    Observable<ResponseBody> checkVerificationCode(@Body RequestBody requestBody);

    @POST("account/reset_password")
    Observable<ResponseBody> resetPassword(@Body RequestBody requestBody);


    static <T> Function<ResponseBody, T> turn(Class<T> tClass) {
        return responseBody -> {
            String body = responseBody.string();
            Base<T> base = GsonHel.dataObject(body, tClass);
            if (base.errCode == 0)
                return null == base.data ? tClass.newInstance() : base.data;
            throw new RXRetrofitException(base.errCode, base.errMsg);
        };
    }
}
