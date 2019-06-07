package com.example.android.cipherchat.Utils;

import android.content.Context;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Calendar;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.security.auth.x500.X500Principal;

public class RSAEncyptionHelper {

    public static KeyPair generateKeys(Context context, String alias){
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA","AndroidKeyStore");

            if (Build.VERSION.SDK_INT >= 23) { // Device is Marshmallow or greater, initialize with KeyGenParameterSpec

                KeyGenParameterSpec keyGenParameterSpec = new KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_ECB)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                        .build();

                keyPairGenerator.initialize(keyGenParameterSpec);

                return keyPairGenerator.generateKeyPair();
            } else { // Device is below Marshmallow, initialize with KeyPairGeneratorSpec
                Calendar startDate = Calendar.getInstance();
                Calendar endDate = Calendar.getInstance();
                endDate.add(Calendar.YEAR, 20);

                KeyPairGeneratorSpec keyPairGeneratorSpec = new KeyPairGeneratorSpec.Builder(context)
                        .setAlias(alias)
                        .setSerialNumber(BigInteger.ONE)
                        .setSubject(new X500Principal("CN=" + alias))
                        .setStartDate(startDate.getTime())
                        .setEndDate(endDate.getTime())
                        .build();

                keyPairGenerator.initialize(keyPairGeneratorSpec);

                return keyPairGenerator.generateKeyPair();
            }
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String encrypt(String data, Key key) {
        Cipher cipher = createCipher();
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] bytes = cipher.doFinal(data.getBytes());
            return Base64.encodeToString(bytes, Base64.DEFAULT);
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String decrypt(String data, Key key) {
        Cipher cipher = createCipher();
        try {
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] encryptedData = Base64.decode(data, Base64.DEFAULT);
            byte[] decodedData = cipher.doFinal(encryptedData);
            return new String(decodedData);
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Cipher createCipher() {
        String transformation = "RSA/ECB/PKCS1Padding";
        try {
            return Cipher.getInstance(transformation);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }
}