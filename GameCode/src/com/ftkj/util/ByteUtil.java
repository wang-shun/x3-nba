package com.ftkj.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class ByteUtil {
    private static final Logger log = LoggerFactory.getLogger(ByteUtil.class);

    // 将byte数组bRefArr转为一个整数,字节数组的低位是整型的低字节位
    //	public static int toInt(byte[] res) {
    //	// 一个byte数据左移24位变成0x??000000，再右移8位变成0x00??0000
    //	int targets = (res[0] & 0xff) | ((res[1] << 8) & 0xff00) // | 表示安位或
    //	| ((res[2] << 24) >>> 8) | (res[3] << 24);
    //	return targets;
    //	}
    private static int cachesize = 1024;

    public static byte[] compress(byte[] inputs) {
        Deflater deflater = new Deflater();
        deflater.reset();
        deflater.setInput(inputs);
        deflater.finish();
        byte outputs[] = new byte[0];
        ByteArrayOutputStream stream = new ByteArrayOutputStream(inputs.length);
        byte[] bytes = new byte[cachesize];
        int value;
        while (!deflater.finished()) {
            value = deflater.deflate(bytes);
            stream.write(bytes, 0, value);
        }
        outputs = stream.toByteArray();
        //logger.error("--"+inputs.length+" | "+outputs.length);
        try {
            stream.close();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return outputs;
    }

    public static byte[] decompress(byte[] data) {
        byte[] output = new byte[0];

        Inflater decompresser = new Inflater();
        decompresser.reset();
        decompresser.setInput(data);

        ByteArrayOutputStream o = new ByteArrayOutputStream(data.length);
        try {
            byte[] buf = new byte[1024];
            while (!decompresser.finished()) {
                int i = decompresser.inflate(buf);
                o.write(buf, 0, i);
            }
            output = o.toByteArray();
        } catch (Exception e) {
            output = data;
        } finally {
            try {
                o.close();
            } catch (IOException e) {
            }
        }

        decompresser.end();
        return output;
    }

    public static int toInt(byte[] bRefArr) {
        int iOutcome = 0;
        byte bLoop;
        for (int i = 0; i < 4; i++) {
            bLoop = bRefArr[i];
            iOutcome += (bLoop & 0xFF) << (8 * i);
        }
        return iOutcome;
    }

    public static int byteToInt2(byte[] b) {
        int mask = 0xff;
        int temp = 0;
        int n = 0;
        for (int i = 0; i < 4; i++) {
            n <<= 8;
            temp = b[i] & mask;
            n |= temp;
        }
        return n;
    }

    /**
     * 转换一个无字符的short
     *
     * @param b
     * @return
     */
    public static int byteToUnsignedShort(byte[] b) {
        int mask = 0xff;
        int temp = 0;
        short n = 0;
        for (int i = 0; i < 2; i++) {
            n <<= 8;
            temp = b[i] & mask;
            n |= temp;
        }
        return n & 0x0FFFF;
    }

    @SuppressWarnings("unchecked")
    public static <T> T cloneTo(T src) throws RuntimeException {
        ByteArrayOutputStream memoryBuffer = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        T dist = null;
        try {
            out = new ObjectOutputStream(memoryBuffer);
            out.writeObject(src);
            out.flush();
            in = new ObjectInputStream(new ByteArrayInputStream(memoryBuffer.toByteArray()));
            dist = (T) in.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                    out = null;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (in != null) {
                try {
                    in.close();
                    in = null;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return dist;
    }

    @SuppressWarnings("unchecked")
    public static <T> T toObject(InputStream ins) {
        ObjectInputStream in = null;
        try {
            in = new ObjectInputStream(ins);
            return (T) in.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                    in = null;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static byte[] toByte(Object src) {
        ByteArrayOutputStream memoryBuffer = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(memoryBuffer);
            out.writeObject(src);
            out.flush();
            return memoryBuffer.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                    out = null;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /*读取文件的字节数组*/
    public static byte[] toByteArray(File file) throws IOException {
        File f = file;
        if (!f.exists()) {
            throw new FileNotFoundException(file.getName() + " file not exists");
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream((int) f.length());
        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(f));
            int buf_size = 1024;
            byte[] buffer = new byte[buf_size];
            int len = 0;
            while (-1 != (len = in.read(buffer, 0, buf_size))) {
                bos.write(buffer, 0, len);
            }
            return bos.toByteArray();
        } catch (IOException e) {
            throw e;
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
            bos.close();
        }
    }

}
