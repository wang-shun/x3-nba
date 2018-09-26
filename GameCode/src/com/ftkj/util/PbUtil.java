package com.ftkj.util;

import com.google.protobuf.TextFormat;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 *
 */
public class PbUtil {

    public static String shortDebug(com.google.protobuf.Message protobufMsg) {
        return TextFormat.shortDebugString(protobufMsg);
    }

    public static String shortDebug(Collection<? extends com.google.protobuf.Message> protobufMsg) {
        return protobufMsg.stream()
                .map(TextFormat::shortDebugString).collect(Collectors.joining(", "));
    }

}
