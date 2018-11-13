package com.nec.baas.adfs_authentication;

import android.app.Application;

import com.nec.baas.core.NbAndroidServiceBuilder;
import com.nec.baas.core.NbOperationMode;
import com.nec.baas.core.NbService;
import com.nec.baas.core.NbSetting;
import com.nec.baas.http.NbHttpClient;

/**
 * BaaSサーバーへアクセスするサービスを登録するアプリケーションクラス
 */
public class MainApplication extends Application {

    private static final String TAG="MainApplication";

    private NbService mService = null;

    @Override
    public void onCreate(){
        super.onCreate();

        // サーバ自己署名証明書を許可する。
        // 呼び出しはNebulaServiceの生成前に行うこと。
        //
        // デバッグ用なので、本番時には使用しないこと!!!
        // (本番時は正規のサーバ証明書を使用すること)
        if(getString(R.string.SSL_Self_Signed).compareTo("true") == 0) {
            NbHttpClient.getInstance().setAllowSelfSignedCertificate(true);
        }

        // デバッグモードの設定
        if(getString(R.string.DebugMode).compareTo("true") == 0) {
            NbSetting.setOperationMode(NbOperationMode.DEBUG);
        }

        // BaaS 初期化処理。NebulaService を生成する。
        mService = new NbAndroidServiceBuilder(this)
                .tenantId(getString(R.string.TenantId))
                .appId(getString(R.string.AppId))
                .appKey(getString(R.string.AppKey))
                .endPointUri(getString(R.string.EndpointUri))
                .build();

    }
}
