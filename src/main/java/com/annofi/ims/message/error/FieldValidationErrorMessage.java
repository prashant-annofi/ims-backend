package com.annofi.ims.message.error;

import java.awt.TrayIcon.MessageType;


public class FieldValidationErrorMessage {

	private String field;
	private String message;
	private MessageType type;

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public MessageType getType() {
		return type;
	}

	public void setType(MessageType type) {
		this.type = type;
	}




	@Override
	public boolean equals(Object msg) {
		if(this.field.equals(((FieldValidationErrorMessage)msg).getField()))
		{
			return true;
		}
		return false;
	}
}

/*
 * public enum MessageType { SUCCESS, INFO, WARNING, ERROR }
 */
