package com.ftkj.x3.client;

import com.ftkj.xxs.client.XxsClientConfig;

import com.typesafe.config.Config;

/**
 * 客户端配置.
 *
 * @author luch
 */
public class ClientConfig extends XxsClientConfig {
    /** 登录时生成 tokey 需要的 key */
    private String loginTokenKey;
    /** 比赛校验时需要的 key */
    private String matchValidateToken;
    /** 服务器是否开启了帐号有效性校验 */
    private boolean authAccount = true;

    public ClientConfig(Config appConfig) {
        super(appConfig);
    }

    @Override
    public void init(Config config) {
        super.init(config);

        loginTokenKey = config.getString("client.base.token-key");
        matchValidateToken = config.getString("client.base.match-validate-token");
        authAccount = config.getBoolean("client.base.auth-account");
    }

    public String getLoginTokenKey() {
        return loginTokenKey;
    }

    public String getMatchValidateToken() {
        return matchValidateToken;
    }

    public boolean isAuthAccount() {
        return authAccount;
    }

    public String to1String() {
        return "{" +
            "\"base\":\"" + super.toString() + "\"" +
            "\n, \"loginTokenKey\":\"" + loginTokenKey + "\"" +
            "\n, \"matchValidateToken\":\"" + matchValidateToken + "\"" +
            "\n, \"authAccount\":\"" + authAccount + "\"" +
            '}';
    }
}
