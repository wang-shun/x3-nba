package com.ftkj.x3.client.proto;

import com.ftkj.enums.ErrorCode;
import com.ftkj.xxs.core.util.StringUtil;
import com.ftkj.xxs.proto.SimpleRet;

import java.util.HashMap;
import java.util.Map;

/**
 * @author luch
 */
public class Ret extends SimpleRet {

    Ret(int ret, String msg) {
        super(ret, msg);
    }

    public static Ret convert(int errCode) {
        return Holder.convert(errCode);
    }

    public static Ret success() {
        return Holder.SUCCESS;
    }

    public static Ret nulll() {
        return Holder.NULL;
    }

    public static Ret clientError(String msg, Object... args) {
        return new Ret(ErrorCode.Error.getCode(), String.format("客户端错误:" + msg, args));
    }

    private static final class Holder {
        private static Map<Integer, Ret> retCache = new HashMap<>();

        static {
            for (ErrorCode err : ErrorCode.values()) {
                retCache.put(err.getCode(), new Ret(err.getCode(), StringUtil.isEmpty(err.getTip()) ? err.name() : err.getTip()));
            }
        }

        static Ret convert(int errCode) {
            return retCache.get(errCode);
        }

        private static Ret SUCCESS = convert(ErrorCode.Success.getCode());
        private static Ret NULL = convert(ErrorCode.NULL.getCode());

    }

    public static void main(String[] args) {
        System.out.println(Ret.success());
    }
}
