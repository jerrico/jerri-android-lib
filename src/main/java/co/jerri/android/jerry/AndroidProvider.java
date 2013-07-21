package co.jerri.android.jerry;

import co.jerri.android.jerry.ProviderInterface;

import java.util.*;
import java.io.*;
import java.net.URLEncoder;
import java.security.MessageDigest;
import android.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


class AndroidProvider implements ProviderInterface{
	private String key;
	private String secret;
	public String end_point = "http://api.jerri.co/api/v1/";

    private static final String CHARSET = "UTF-8";
    private static final String ALGORITHM = "HmacSHA256";

	public AndroidProvider(String key, String secret){
		this.key = key;
		this.secret = secret;
	}

	public void did(String action, int change){

	}
	public void did(String action){
		did(action, 1);
	}

	public JerryUser signin(String user_id, String device_id){
		JerryUser user = new JerryUser(user_id, device_id, this);
		return user;
	}

    public String sign(String method, String url, String query) throws Exception
    {
        
        SecretKeySpec keySpec = new SecretKeySpec(
        		this.secret.getBytes(), ALGORITHM);
        Mac mac = Mac.getInstance(ALGORITHM);
        mac.init(keySpec);

        String resultQuery = "_key=" + URLEncoder.encode(this.key, CHARSET) + "&" + query;
        byte[] result = mac.doFinal((method.toUpperCase() + "&" + url + "&" + resultQuery).getBytes(CHARSET));
        resultQuery += "&_signature=" + URLEncoder.encode(Base64.encodeToString(result, Base64.DEFAULT), CHARSET);

        return resultQuery;
    }
}