package com.ftkj.util;

import com.ftkj.server.http.bean.FTXLogin;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class MapsUtils {


	private static final String Key = "4CE4FB09D8DE4E38B16464CEDF779926";

    /**
     * 把值为 Object 的 Map 转成 String，只适合简单类
     * @param param
     * @return
     */
    public static Map<String, String> objectMap2StringMap(Map<String, Object> param) {
        List<String> keys = new ArrayList<String>(param.keySet());
        Map<String, String> stringMap = new HashMap<String, String>();
        for(String key : keys) {
            if(null != param.get(key)) {
                stringMap.put(key, param.get(key).toString());
            }
        }
        return stringMap;
    }

    /**
     * 移除 Map 中值为 null 或空的 key
     * @param param
     */
    public static void removeMapEmptyKey(Map<String, String> param) {
        List<String> keys = new ArrayList<>(param.keySet());
        for(String key : keys) {
            if(Strings.isNullOrEmpty(param.get(key))) {
                param.remove(key);
            }
        }
    }

    //按照key="value"的模式用'&'字符拼接成字符串,sort 是否需要根据key值作升序排列

    /**
     * 按key值排列，以key=value&key=value的形式拼成一个串
     * @param param
     * @param sort 键是否排序
     * @param isWithQuotation 值是否需要加双引号
     * @return
     */
    public static String createLinkString(Map<String, String> param, boolean sort, boolean isWithQuotation) {
        List<String> keys = new ArrayList<String>(param.keySet());
        if (sort){
            Collections.sort(keys);
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = "";

            if(null != param.get(key)){
                value = param.get(key);
            }

            if(isWithQuotation) {
                sb.append(String.format("%s=\"%s\"", key, value));
            } else {
                sb.append(String.format("%s=%s", key, value));
            }
            if (i != keys.size() - 1) {
                sb.append("&");
            }
        }
        return sb.toString();
    }

    public static String getSignKey(FTXLogin login){
    	Map<String, String> requestMap = Maps.newHashMap();
        requestMap.put("appId", ""+login.getAppId());
        requestMap.put("packageId", ""+login.getPackageId());
        requestMap.put("token", login.getToken());
        requestMap.put("userId", login.getUserId());
        requestMap.put("exInfo", login.getExInfo());
        //移除 null 和空的参数
        MapsUtils.removeMapEmptyKey(requestMap);
        String befSign = MapsUtils.createLinkString(requestMap, true, false) + MapsUtils.Key;
    	return MD5Util.encodeMD5(befSign);
    }
    class KV {
    	private String key;
    	private String value;
		public KV(String key, String value) {
			super();
			this.key = key;
			this.value = value;
		}
		public String getKey() {
			return key;
		}
		public String getValue() {
			return value;
		}

    }


    /** 把 src 合并进 target */
    public static <K, V extends Number> void mergeValueInt(Map<K, V> src, Map<K, V> target, BiFunction<V, V, V> op) {
        if (src == null || src.isEmpty()) {
            return;
        }
        for (Map.Entry<K, V> e : src.entrySet()) {
            target.merge(e.getKey(), e.getValue(), op::apply);
        }
    }

    public static <K, V, U> Map<K, U> getByKeys(Map<K, V> src, Collection<K> keys, Function<V, U> valueMapper) {
        if (src == null || src.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<K, U> ret = new HashMap<>();
        for (K k : keys) {
            V v = src.get(k);
            if(v != null) {
                ret.put(k, valueMapper.apply(v));
            }
        }
        return ret;
    }

    public static void main(String [] argv) {
        Map<String, String> requestMap = Maps.newHashMap();
        requestMap.put("appId", "appId");
        requestMap.put("packageId", "packageId");
        requestMap.put("token", "token");
        requestMap.put("userId", "userId");
        requestMap.put("exInfo", "exInfo");
        //移除 null 和空的参数
        MapsUtils.removeMapEmptyKey(requestMap);
        String befSign = MapsUtils.createLinkString(requestMap, true, false) + "秘钥";
//        String sign = MD5Util.getMD5(befSign);
    }
}
