package com.apitesting.service;


public class SmsResponse {
	
    private String ErrorCode;
    private String ErrorMessage;
    private String JobId;
    private String MessageData;
	public String getErrorCode() {
		return ErrorCode;
	}
	public void setErrorCode(String errorCode) {
		ErrorCode = errorCode;
	}
	public String getErrorMessage() {
		return ErrorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		ErrorMessage = errorMessage;
	}
	public String getJobId() {
		return JobId;
	}
	public void setJobId(String jobId) {
		JobId = jobId;
	}
	public String getMessageData() {
		return MessageData;
	}
	public void setMessageData(String messageData) {
		MessageData = messageData;
	}
}
