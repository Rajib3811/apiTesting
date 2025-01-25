package com.apitesting.sms.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.CloseableThreadContext.Instance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.apitesting.service.SmsService;
import com.apitesting.service.SmsService2;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;




class MessageHistory {
	
	
    private String mobileNumber;
    private String status;
    
    

    public MessageHistory(String mobileNumber, String status) {
		super();
		this.mobileNumber = mobileNumber;
		this.status = status;
	}

	// Getters and setters
    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}


@RestController
public class SMSController {
	
	@Autowired
	private SmsService smsService;
	
	@Autowired
	SmsService2 service2;
	
	 final String apiKey = "OyiQzajx1kycMM9LxnQF6Q";
     final String senderId = "PSOFTT";
     final String channel = "2";
     final String dcs = "0";
     final String flashsms = "0";
     final String route = "1";
     final String number = "7074424277";
     final String text = "Dear Candidate, Thanks for registration. Your Id is 101. Please mention Id for further "
     						+ "communication. - Pisoft Informatics Pvt Ltd, Mohali";
     
     
     
	
	
	@PostMapping("/SMS/senda")
	public Mono<List<MessageHistory>> sendSMS(){
		
		
		String[] numbs = {
				"7074424277","8219895853"
		};
		
		
		List<Mono<MessageHistory>> statusMonos = new ArrayList<>();
		
		
		 // Loop over each number and create a Mono<MessageHistory> for each
        for (String number : numbs) {
            Mono<MessageHistory> statusMono = smsService.sendSms(apiKey, senderId, channel, route, dcs, flashsms, number, text)
                .map(response -> {
                    String status = "Unsuccess";  // Default to "Unsuccess"
                    // Extract the status from the response
                    System.out.println(response);

                    if (response.contains("\"ErrorCode\":\"000\"") && response.contains("\"ErrorMessage\":\"Success\"")) {
                        status = "Success";  // Update status to "Success" if the response is successful
                    }
                    return new MessageHistory(number, status);
                })
                .onErrorReturn(new MessageHistory(number, "Error"));  // Handle errors gracefully

            statusMonos.add(statusMono);  // Add each Mono<MessageHistory> to the list
        }

        // Return a single Mono that will emit a list of MessageHistory objects
        return Mono.zip(statusMonos, results -> {
            List<MessageHistory> historyList = new ArrayList<>();
            for (Object result : results) {
                historyList.add((MessageHistory) result);
            }
            return historyList; 
        });
        
        
        
        
		
        
        
	}
	
	
	
	@PostMapping("/SMS/send")
	public void sendSMSInBulk() {

		String[] numbs = {
				"8219895853",
				"7074424277"
		};
		
		  String textTemplate = "Dear Candidate, Thanks for registration. Your Id is 101. Please mention Id for further "
					+ "communication. - Pisoft Informatics Pvt Ltd, Mohali";

  for (String number : numbs) {

	// Send SMS and handle the response asynchronously
    Mono<String> smsResponse =service2.sendSms(number, textTemplate)
            .map(response -> {
                if (response.contains("\"ErrorCode\":\"000\"") && response.contains("\"ErrorMessage\":\"Success\"")) {
                	
                } else {
                }
                return "success:"+number;
            })
            .doOnError(error -> {
                System.out.println("SMS send attempt for in error :  " + number);

            })
            .doOnTerminate(() -> {
                System.out.println("SMS send attempt completed for " + number);
            });
                System.out.println("smsResponse "+smsResponse);

            }
	}
	
	
	
	
	@GetMapping("/SMS/send")
	public Mono<List<String>> sendSMSs(){
		
		//List<Mono<String>> res = new ArrayList<Mono<String>>();
		
		String[] numbs = {
				"8219895853",
//				"7074424277",
//				"7876761256",
				"9625223001"
		};
		
		
		return Flux.fromArray(numbs)
				.flatMap(num -> smsService.sendSms(apiKey, senderId, channel, route, dcs, flashsms, num, text))
				.collectList();
		
		
	}
	
	
	
	
	
	
	
	
	@GetMapping("/hello-world")
	public String helloWorld() {
		return "Hello World!";
	}

	
	 public static void main(String[] args) {
	        // The response string
	        String response = "{\"ErrorCode\":\"000\",\"ErrorMessage\":\"Success\",\"JobId\":\"146080689\",\"MessageData\":[{\"Number\":\"917074424277\",\"MessageId\":\"u56GVBSnV0u5TLLTurM4kw\",\"Message\":\"Dear Candidate, Thanks for registration. Your Id is 101. Please mention Id for further communication. - Pisoft Informatics Pvt Ltd, Mohali\"}]}";
	        
	        
	        System.out.println("response: "+ response);
	        // Check for "ErrorCode":"000" and "ErrorMessage":"Success"
	        boolean hasErrorCode = response.contains("\"ErrorCode\":\"000\"");
	        boolean hasErrorMessage = response.contains("\"ErrorMessage\":\"Success\"");
	        
	        // Output the results
	        System.out.println("Contains \"ErrorCode\":\"000\": " + hasErrorCode);
	        System.out.println("Contains \"ErrorMessage\":\"Success\": " + hasErrorMessage);
	    }
}
