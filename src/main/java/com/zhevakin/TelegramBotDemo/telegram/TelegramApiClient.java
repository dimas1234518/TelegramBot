package com.zhevakin.TelegramBotDemo.telegram;

import com.zhevakin.TelegramBotDemo.exceptions.TelegramFileNotFoundException;
import com.zhevakin.TelegramBotDemo.exceptions.TelegramFileUploadException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.telegram.telegrambots.meta.api.objects.ApiResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.text.MessageFormat;
import java.util.Objects;

// Отправка файлов/приемка файлов
@Service
public class TelegramApiClient {

    private final String URL;
    private final String botToken;

    private final RestTemplate restTemplate;

    public TelegramApiClient(@Value("${telegram.api-url}") String URL,
                             @Value("${telegram.bot-token}") String botToken) {
        this.URL = URL;
        this.botToken = botToken;
        this.restTemplate = new RestTemplate();
    }

    public void uploadFile(String chatId, ByteArrayResource value) {
        LinkedMultiValueMap<String,Object> map = new LinkedMultiValueMap<>();
        map.add("document", value);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<LinkedMultiValueMap<String,Object>> requestEntity = new HttpEntity<>(map, httpHeaders);

        try {
            restTemplate.exchange(
                    MessageFormat.format("{0}bot{1}/sendDocument?chat_id={2}", URL,botToken,chatId),
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );
        } catch (Exception e) {
            throw new TelegramFileUploadException();
        }
    }

    public File getDocumentFile (String fileId) {
        try {
            return restTemplate.execute(
                    Objects.requireNonNull(getDocumentTelegramFileUrl(fileId)),
                    HttpMethod.GET,
                    null,
                    clientHttpResponse -> {
                        File ret = File.createTempFile("download", "tmp");
                        StreamUtils.copy(clientHttpResponse.getBody(), new FileOutputStream(ret));
                        return ret;
                    }
            );
        } catch (Exception e) {
            throw new TelegramFileNotFoundException();
        }
    }

    private String getDocumentTelegramFileUrl(String fileId) {
        try {
            ResponseEntity<ApiResponse<org.telegram.telegrambots.meta.api.objects.File>> response = restTemplate.exchange(
                    MessageFormat.format("{0}bot{1}/getFile?file_id={2}", URL, botToken, fileId),
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<ApiResponse<org.telegram.telegrambots.meta.api.objects.File>>() {
                    }
            );
            return Objects.requireNonNull(response.getBody()).getResult().getFileUrl(botToken);
        } catch (Exception e) {
            throw  new TelegramFileNotFoundException();
        }
    }
}
