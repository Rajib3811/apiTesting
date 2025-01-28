package com.apitesting.service;


import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Service
public class SmsService2 {

	
	@Value("${sms.gateway.api.key}")
	private String apiKey;

	@Value("${sms.gateway.senderid}")
	private String senderId;

	@Value("${sms.gateway.channel}")
	private String channel;

	@Value("${sms.gateway.dcs}")
	private String dcs;

	@Value("${sms.gateway.flashsms}")
	private String flashSms;

	@Value("${sms.gateway.route}")
	private String route;
	
	
	private  WebClient webClient;
	
	
	public SmsService2() {
	   
	    SslContext sslContext;
		try {
			sslContext = SslContextBuilder.forClient()
			        .trustManager(InsecureTrustManagerFactory.INSTANCE)
			        .build();
		

	    HttpClient httpClient = HttpClient.create()
	            .secure(sslContextSpec -> sslContextSpec.sslContext(sslContext));

	    ReactorClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);

	    this.webClient = WebClient.builder()
	            .clientConnector(connector)
	            .baseUrl("https://www.smsgatewayhub.com/api/mt/")
	            .defaultHeader("Content-Type", "application/json")
	            .build();
	    
		} catch (SSLException e) {
			e.printStackTrace();
		}
	    // Disable SSL verification for local API call
	    disableSslVerification();
	  
	}
	
	
	
	


	public Mono<String> sendSms(String number, String text) {

		 String url = "SendSms?APIKey=" + apiKey +
		            "&SenderId=" + senderId +
		            "&Channel=" + channel +
		            "&DCS=" + dcs +
		            "&FlashSms=" + flashSms +
		            "&Route=" + route +
		            "&Number=" + number +
		            "&Text=" + text;

		    return webClient.get()
		            .uri(url)
		            .retrieve()
		            .bodyToMono(String.class);
	}

	
	

	private void disableSslVerification() {
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			@Override
			public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			}

			@Override
			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			}

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return new X509Certificate[] {};
			}
		} };

		try {
			SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
			SSLContext.setDefault(sslContext);
		} catch (NoSuchAlgorithmException | KeyManagementException e) {
			throw new RuntimeException(e);
		}
	}
}