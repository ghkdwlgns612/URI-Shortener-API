package com.example.demo.service;

import com.example.demo.dto.UrlResponseDto;
import org.dom4j.rule.Mode;
import org.springframework.ui.Model;

import javax.xml.bind.ValidationException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public interface UrlService {
     UrlResponseDto findByHashValue(String encodedValue, Model model) throws Exception;
     UrlResponseDto createUrl(String originUrl) throws NoSuchAlgorithmException, ValidationException;
     UrlResponseDto createUrlWithLogin(String originUrl, String name) throws NoSuchAlgorithmException, ValidationException, UnsupportedEncodingException;
}
