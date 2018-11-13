package com.nec.baas.adfs_authentication;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.nec.baas.core.NbCallback;
import com.nec.baas.core.NbErrorInfo;
import com.nec.baas.user.NbUser;

import java.net.URISyntaxException;

/**
 * SAML連携ログイン画面Acttivityクラス
 */
public class LoginActivity extends Activity {

    private static final String TAG = "LoginActivity";

    private NbUser.LoginParam mloginParam = null;

    // Layout Object
    private TextView tvOutput = null;

    private StringBuffer outputMessage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        outputMessage = new StringBuffer();

        //メッセージ出力先のTextViewを取得
        tvOutput = (TextView) findViewById(R.id.TvLoginActivityMessage);

        // Urlよりパラメータを取得
        mloginParam = getLoginParam(this.getIntent());

        // ログイン
        loginBaasServer(mloginParam);

        tvOutput.setText(outputMessage);
    }

    /**
     * SAML連携により設定されたパラメータをIntentから取り出すメソッド
     * @param intent
     * @return NbUser.LoginParam
     */
    private NbUser.LoginParam getLoginParam(Intent intent){

        NbUser.LoginParam rtnParam = null;
        // リダイレクト URI を取り出す
        Uri uri = intent.getData();

        outputMessage.append("-Get loginParam from intent ---------" + System.getProperty("line.separator"));
        try{
            // ワンタイムトークンを取り出す
            rtnParam = NbUser.extractOneTimeTokenFromUri(uri.toString());

            // ワンタイム取得結果を表示
            if(rtnParam != null) {

                String formatString = "  %s : %s " + System.getProperty("line.separator");
                outputMessage.append(String.format(formatString, "username", rtnParam.username()));
                outputMessage.append(String.format(formatString, "email"   , rtnParam.email()));
                outputMessage.append(String.format(formatString, "hashCode", rtnParam.hashCode()));
                outputMessage.append(String.format(formatString, "password", rtnParam.password()));
                outputMessage.append(String.format(formatString, "token"   , rtnParam.token()));

                outputMessage.append("-------------" + System.getProperty("line.separator"));

            }else{
                outputMessage.append("Error loginParam = null" + System.getProperty("line.separator"));
            }

        }catch(URISyntaxException e){
            outputMessage.append("Error URISyntax:" + e.toString() + System.getProperty("line.separator"));
            Log.e(TAG,e.toString());
        }catch(Exception e){
            outputMessage.append("Error Other Exception:"  + e.toString() + System.getProperty("line.separator"));
            Log.e(TAG,e.toString());
        }

        return rtnParam;
    }

    /**
     * BaaSサーバーにログインするメソッド
     * @param loginParam
     */
    private void loginBaasServer(NbUser.LoginParam loginParam){

        outputMessage.append("-Start Login from OnetimeToken ---------"
                + System.getProperty("line.separator"));

        if(loginParam == null) {
            outputMessage.append("Error loginParam = null"
                    + System.getProperty("line.separator"));
            return;
        }

        try{
            // ログイン
            NbUser.login(loginParam, new NbCallback<NbUser>() {
                        @Override
                        public void onSuccess(NbUser nbUser) {
                            TextView outputView = (TextView) findViewById(R.id.TvLoginActivityMessage);
                            outputView.append("OnetimeToken Login --- Succeeded" + System.getProperty("line.separator"));
                        }

                        @Override
                        public void onFailure(int i, NbErrorInfo nbErrorInfo) {
                            TextView outputView = (TextView) findViewById(R.id.TvLoginActivityMessage);
                            outputView.append("OnetimeToken Login --- Failured:" + nbErrorInfo.toString());
                            Log.e(TAG,nbErrorInfo.toString());
                        }
                    });

        }catch(Exception e){
            outputMessage.append("Error Other Exception:"  + e.toString() + System.getProperty("line.separator"));
            Log.e(TAG,e.toString());
        }

    }

}
