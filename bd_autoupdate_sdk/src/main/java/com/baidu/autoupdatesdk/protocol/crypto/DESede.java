//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.baidu.autoupdatesdk.protocol.crypto;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

public class DESede {
    private KeySpec keySpec;
    private String algorithm = "DESede/CBC/PKCS7Padding";
    private SecretKey key;
    private SecretKeyFactory keyFactory;
    private String charset = "utf-8";

    public DESede() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("DESede");
            this.key = keyGen.generateKey();
        } catch (NoSuchAlgorithmException var2) {
//            LogUtils.printE(var2.getMessage());
        }

    }

    public DESede(byte[] keyByte) {
        this.initKey(keyByte);
    }

    public DESede(String keyStr) {
        try {
            byte[] keyByte = keyStr.getBytes(this.charset);
            this.initKey(keyByte);
        } catch (UnsupportedEncodingException var4) {
//            LogUtils.printE(var4.getMessage());
        }

    }

    private void initKey(byte[] keyByte) {
        try {
            this.keyFactory = SecretKeyFactory.getInstance("DESede");
            this.keySpec = new DESedeKeySpec(this.updateKey(keyByte));
            this.key = this.keyFactory.generateSecret(this.keySpec);
        } catch (InvalidKeyException var3) {
//            LogUtils.printE(var3.getMessage());
        } catch (InvalidKeySpecException var4) {
//            LogUtils.printE(var4.getMessage());
        } catch (NoSuchAlgorithmException var5) {
//            LogUtils.printE(var5.getMessage());
        }

    }

    private byte[] updateKey(byte[] keyByte) {
        int lg = keyByte.length;
        if (keyByte.length < 24) {
            byte[] newKey = new byte[24];
            byte[] temp = new byte[24 - lg];

            for(int i = 0; i < 24 - lg; ++i) {
                temp[i] = 0;
            }

            System.arraycopy(keyByte, 0, newKey, 0, lg);
            System.arraycopy(temp, 0, newKey, lg, 24 - lg);
            keyByte = newKey;
        }

        return keyByte;
    }

    public byte[] encrypt(byte[] data) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, Exception {
        IvParameterSpec iVSpec = ivGenerator(this.key.getEncoded());
        Cipher c1 = Cipher.getInstance(this.algorithm);
        c1.init(1, this.key, iVSpec);
        return c1.doFinal(data);
    }

    public byte[] encryptStr(String str) throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, Exception {
        byte[] data = str.getBytes(this.charset);
        return this.encrypt(data);
    }

    private static IvParameterSpec ivGenerator(byte[] b) {
        byte[] defaultIV = new byte[8];
        System.arraycopy(b, 0, defaultIV, 0, 8);
        IvParameterSpec iV = new IvParameterSpec(defaultIV);
        return iV;
    }

    public byte[] decrypt(byte[] data) throws Exception {
        IvParameterSpec iVSpec = ivGenerator(this.key.getEncoded());
        Cipher decryptCipher = Cipher.getInstance(this.algorithm);
        decryptCipher.init(2, this.key, iVSpec);
        return decryptCipher.doFinal(data);
    }

    public String decryptStr(byte[] data) throws Exception {
        byte[] strData = this.decrypt(data);
//        byte c = true;
        return new String(strData, this.charset);
    }

    public byte[] getKey() {
        return this.key.getEncoded();
    }

    public String getCharset() {
        return this.charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }
}
