package com.example.demo.service;

import com.example.demo.domain.Url;
import com.example.demo.dto.UrlResponseDto;
import com.example.demo.repository.UrlRepository;
import com.example.demo.utils.Base62Converter;
import com.example.demo.utils.MakeDto;
import com.example.demo.utils.Sha512Converter;
import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import javax.persistence.EntityExistsException;
import javax.xml.bind.ValidationException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UrlServiceImpl implements UrlService{
    private UrlRepository urlRepository;
    private UrlCheckService urlCheckService;
    private MakeDto makeDto;
    private Base62Converter base62Converter;
    private Sha512Converter sha512Converter = new Sha512Converter();

    @Autowired
    public UrlServiceImpl(UrlRepository urlRepository, UrlCheckService urlCheckService, MakeDto makeDto, Base62Converter base62Converter) {
        this.urlRepository = urlRepository;
        this.urlCheckService = urlCheckService;
        this.makeDto = makeDto;
        this.base62Converter = base62Converter;
    }

    public UrlResponseDto findByHashValue(String encodedValue, Model model) throws Exception { //인코딩 된 값을 디코딩하여 DB에서 찾아 바로 리다이렉트
        String decodedValue = base62Converter.decoding(encodedValue);
        Optional<Url> result = urlRepository.findByhashvalue(decodedValue);
        result.ifPresent(o -> {
            o.setCount(o.getCount() + 1);
            urlRepository.save(o);
        });
        return urlCheckService.isEmpty(encodedValue, result);
    }

    public UrlResponseDto createUrl(String originUrl) throws NoSuchAlgorithmException, ValidationException {
        originUrl = urlCheckService.checkUrl(originUrl);
        BigInteger id = makeRandomValue(); //약 3~4%
        String extract10Char = encryption(id.toString());
        Url url = new Url(extract10Char, originUrl, null);
        duplicate10CharCheck(extract10Char);
        urlRepository.save(url); // 약 25%
        String encodedStr = base62Converter.encoding(extract10Char);
        return makeDto.makeUrlResponseDto(originUrl, extract10Char, encodedStr);
    }

    public UrlResponseDto createUrlWithLogin(String originUrl, String name) throws NoSuchAlgorithmException, ValidationException, UnsupportedEncodingException {
        originUrl = urlCheckService.checkUrl(originUrl);
        String extract10Char = makeExtract10Char(originUrl); //문제 : 사용자1과 사용자2가 똑같은 값을가지면? 그럼 안된다. 어떻게 해야할까?
        Url url = new Url(extract10Char,originUrl,name);
        duplicate10CharCheck(extract10Char);
        duplicateNameAndOriginUrl(originUrl, name);
        urlRepository.save(url); //약 25%
        String encodedStr = base62Converter.encoding(extract10Char);
        return makeDto.makeUrlResponseDto(originUrl, extract10Char, encodedStr,name);
    }

    public void duplicateNameAndOriginUrl(String originUrl, String name) {
        if (urlRepository.existsByname(name)) {
            if (urlRepository.existsByoriginurl(originUrl)) {
                throw new EntityExistsException(originUrl);
            }
        }
    }

    public void duplicate10CharCheck(String extract10Char) {
        if (urlRepository.existsByhashvalue(extract10Char)) // 약 70%
            throw new DuplicateKeyException(extract10Char);
    }

    /**
     * UTF-8인코딩 -> 512SHA -> 0 ~ 10자르기를 통해 10개의 16진수문자를 추출함.
     * **/
    public String makeExtract10Char(String originUrl) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        String encodedUtf8 = URLEncoder.encode(originUrl, "UTF-8");
        String extract10Char = encryption(encodedUtf8);
        return extract10Char;
    }

    public String encryption(String encodedUtf8) throws NoSuchAlgorithmException {
        String convertedBySha512 = sha512Converter.convert512(encodedUtf8);
        return randomPick10(convertedBySha512);
    }

    /**
     * 64글자 중 랜덤으로 10개를 뽑아 반환
     * **/
    public String randomPick10(String sha512) {
        String res = "";
        for(int i=0;i < 10;i++) {
            int tmp = (int)(Math.random() * 60);
            res += sha512.charAt(tmp);
        }
        return res;
    }

    /**
     *
     * 난수발생시키기
     */
    public BigInteger makeRandomValue() {
        return new BigInteger(String.valueOf((int) (Math.random() * 10000000000.0)));
    }
}
