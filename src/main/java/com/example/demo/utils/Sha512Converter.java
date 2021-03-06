package com.example.demo.utils;


import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Sha512Converter {
    public String convert512(String url) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.update(url.getBytes());
        String hex = String.format("%064x", new BigInteger(1, md.digest()));
        return hex;
    }
}
