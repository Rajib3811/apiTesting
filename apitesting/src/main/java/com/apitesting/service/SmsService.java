package com.apitesting.service;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import okhttp3.OkHttpClient;
import reactor.core.publisher.Mono;

@Service
public class SmsService {

	private  WebClient webClient;

//	public SmsService() {
//	    this.webClient = WebClient.builder()
//	            .baseUrl("https://www.smsgatewayhub.com/api/mt/")
//	            .defaultHeader("Content-Type", "application/json")
//	            .build();
//
//	    // Disable SSL verification for local API call
//	    disableSslVerification();
//	}
//	
	
	public SmsService() {
	    OkHttpClient client = new OkHttpClient.Builder()
	            .build();

	    SslContext sslContext;
		try {
			sslContext = SslContextBuilder.forClient()
			        .trustManager(InsecureTrustManagerFactory.INSTANCE)
			        .build();
		

	    reactor.netty.http.client.HttpClient httpClient = reactor.netty.http.client.HttpClient.create()
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
	


	public Mono<String> sendSms(String apiKey, String senderId, String channel, String route, String dcs,
	        String flashsms, String number, String text) {

		 String url = "SendSms?APIKey=" + apiKey +
		            "&SenderId=" + senderId +
		            "&Channel=" + channel +
		            "&DCS=" + dcs +
		            "&FlashSms=" + flashsms +
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