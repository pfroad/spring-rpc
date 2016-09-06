package com.s515.rpc.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by ryan on 12/25/15.
 */
public class HashUtils {
    static char[] hexChars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    static String DEFAULT_CHARSET = "UTF-8";
    static String DEFAULT_ALG = "MD5";

    public static byte[] md5(byte[] bytes) throws NoSuchAlgorithmException {
        return hashBytes(bytes, DEFAULT_ALG);
    }

    public static byte[] hashBytes(byte[] bytes, String algorithm) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(algorithm);
        digest.update(bytes);
        return digest.digest();
    }

    public static String md5(String source, String charset) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        byte[] data = source.getBytes(charset);
        byte[] bytes = hashBytes(data, DEFAULT_ALG);

        return byteToHex(bytes);
    }

    public static String md5(String data) {
        try {
            return byteToHex(hashBytes(data.getBytes(DEFAULT_CHARSET), DEFAULT_ALG));
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            return null;
        }
    }

    public static String byteToHex(byte[] bytes) {
        StringBuilder str = new StringBuilder();

        for (int i = 0; i < bytes.length; i++) {
            str.append(hexChars[bytes[i] >>> 4 & 0xf]);
            str.append(hexChars[bytes[i] & 0xf]);
        }
        return str.toString();
    }

    /**
     * 将待加密数据data，通过密钥key，使用hmac-md5算法进行加密，然后返回加密结果。 参照rfc2104 HMAC算法介绍实现。
     *
     * @author 尹星
     * @param key
     *            密钥
     * @param data
     *            待加密数据
     * @return 加密结果
     * @throws NoSuchAlgorithmException
     */
    public static byte[] hmacMd5Bytes(byte[] key, byte[] data)
            throws NoSuchAlgorithmException {
		/*
		 * HmacMd5 calculation formula: H(K XOR opad, H(K XOR ipad, text))
		 * HmacMd5 计算公式：H(K XOR opad, H(K XOR ipad, text))
		 * H代表hash算法，本类中使用MD5算法，K代表密钥，text代表要加密的数据 ipad为0x36，opad为0x5C。
		 */
        int length = 64;
        byte[] ipad = new byte[length];
        byte[] opad = new byte[length];
        for (int i = 0; i < 64; i++) {
            ipad[i] = 0x36;
            opad[i] = 0x5C;
        }
        byte[] actualKey = key; // Actual key.
        byte[] keyArr = new byte[length]; // Key bytes of 64 bytes length
		/*
		 * If key's length is longer than 64,then use hash to digest it and use
		 * the result as actual key. 如果密钥长度，大于64字节，就使用哈希算法，计算其摘要，作为真正的密钥。
		 */
        if (key.length > length) {
            actualKey = md5(key);
        }
        for (int i = 0; i < actualKey.length; i++) {
            keyArr[i] = actualKey[i];
        }

		/*
		 * append zeros to K 如果密钥长度不足64字节，就使用0x00补齐到64字节。
		 */
        if (actualKey.length < length) {
            for (int i = actualKey.length; i < keyArr.length; i++)
                keyArr[i] = 0x00;
        }

		/*
		 * calc K XOR ipad 使用密钥和ipad进行异或运算。
		 */
        byte[] kIpadXorResult = new byte[length];
        for (int i = 0; i < length; i++) {
            kIpadXorResult[i] = (byte) (keyArr[i] ^ ipad[i]);
        }

		/*
		 * append "text" to the end of "K XOR ipad" 将待加密数据追加到K XOR ipad计算结果后面。
		 */
        byte[] firstAppendResult = new byte[kIpadXorResult.length + data.length];
        for (int i = 0; i < kIpadXorResult.length; i++) {
            firstAppendResult[i] = kIpadXorResult[i];
        }
        for (int i = 0; i < data.length; i++) {
            firstAppendResult[i + keyArr.length] = data[i];
        }

		/*
		 * calc H(K XOR ipad, text) 使用哈希算法计算上面结果的摘要。
		 */
        byte[] firstHashResult = md5(firstAppendResult);

		/*
		 * calc K XOR opad 使用密钥和opad进行异或运算。
		 */
        byte[] kOpadXorResult = new byte[length];
        for (int i = 0; i < length; i++) {
            kOpadXorResult[i] = (byte) (keyArr[i] ^ opad[i]);
        }

		/*
		 * append "H(K XOR ipad, text)" to the end of "K XOR opad" 将H(K XOR
		 * ipad, text)结果追加到K XOR opad结果后面
		 */
        byte[] secondAppendResult = new byte[kOpadXorResult.length
                + firstHashResult.length];
        for (int i = 0; i < kOpadXorResult.length; i++) {
            secondAppendResult[i] = kOpadXorResult[i];
        }
        for (int i = 0; i < firstHashResult.length; i++) {
            secondAppendResult[i + keyArr.length] = firstHashResult[i];
        }

		/*
		 * H(K XOR opad, H(K XOR ipad, text)) 对上面的数据进行哈希运算。
		 */
        byte[] hmacMd5Bytes = md5(secondAppendResult);

        return hmacMd5Bytes;
    }

    public static String hmacMd5(byte[] key, byte[] data) throws NoSuchAlgorithmException {
        return byteToHex(hmacMd5Bytes(key, data));
    }

    public static String hmacMd5(String secretKey, String data, String charset) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        if (charset == null || charset == "") {
            charset = DEFAULT_CHARSET;
        }

        return byteToHex(hmacMd5Bytes(secretKey.getBytes(charset), data.getBytes(charset)));
    }

    public static void main(String[] args) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        System.out.println(md5("888888", "UTF-8"));
    }
}
