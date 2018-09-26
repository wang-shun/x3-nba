package com.ftkj.server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 统计 redis key 的长度
 */
public class RedisKeyLength {
      /*
     * bash

#!/usr/bin/env bash
search_keys="$1"

redis_cmd='./redis-cli -a pwd'

# get keys and sizes
for k in `$redis_cmd keys "$search_keys"`; do key_size_bytes=`$redis_cmd debug object $k | perl -wpe 's/^.+serializedlength:([\d]+).+$/$1/g'`; echo "$key_size_bytes $k\n"; done

     */

    /**
     * arg[0] file path
     * arg[1] key spilter
     *
     * @param args
     */
    public static void main(String[] args) throws IOException {
        new RedisKeyLength().readAndParse(args[0], args.length < 2 ? "_" : args[1]);
    }

    private void readAndParse(String filePath, String keySpilter) throws IOException {
        Map<String, Integer> keyAndLen = new HashMap<>();
        AtomicInteger lineSize = new AtomicInteger();

        Files.lines(Paths.get(filePath)).forEach(line -> {
            if (line.startsWith("ERR no such key")) {
                return;
            }
            lineSize.incrementAndGet();

            String[] lenAndKey = line.split("\\s+");
            String key = lenAndKey[1];
            int len = Integer.parseInt(lenAndKey[0]);
            //            System.out.printf("key %s len %s ", key, len);
            String[] splitedKey = key.split(keySpilter);

            int numIdx = -1;
            for (int i = 0; i < splitedKey.length; i++) {
                String keypar = splitedKey[i];
                if (keypar.length() > 0) {
                    char first = keypar.charAt(0);
                    if (first >= '0' & first <= '9') {//球队id或日期或类型
                        numIdx = i;
                        break;
                    }
                }
            }

            String keyPre = numIdx >= 0 ? Arrays.stream(splitedKey)
                .limit(numIdx)
                .collect(Collectors.joining(keySpilter)) + "*" :
                key;
            keyAndLen.merge(keyPre, len, (oldv, v) -> oldv + v);
        });

        StringBuilder sb = new StringBuilder();
        AtomicInteger totalLen = new AtomicInteger();
        keyAndLen.forEach((k, v) -> {
            sb.append(k).append("\t").append(v).append("\n");
            totalLen.addAndGet(v);
        });
        sb.append("total").append("\t").append(totalLen.get()).append("\n");

        System.out.println("lines_size " + lineSize.get());
        System.out.println(sb);
    }
}
