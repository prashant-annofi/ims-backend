package com.annofi.ims.message.handler;

import com.annofi.ims.exception.RestMessageException;
import com.annofi.ims.message.RestMessage;
import com.annofi.ims.message.error.FieldValidationErrorMessage;
import com.annofi.ims.message.error.RestErrorMessage;
import com.annofi.ims.message.error.RestFieldValidationMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.awt.TrayIcon.MessageType;
import java.util.*;

/**
 * This class is applied to each controller.
 *
 *
 *
 */
@ControllerAdvice
public class RestValidationHandler {


	private static final String UNEXPECTED_ERROR = "Exception.unexpected";

	private MessageSource messageSource;

	@Autowired
	public RestValidationHandler(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	/*@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<FieldValidationErrorDetails> handleValidationError(
			MethodArgumentNotValidException mNotValidException, HttpServletRequest request) {
		FieldValidationErrorDetails fErrorDetails = new FieldValidationErrorDetails();
		fErrorDetails.setErrorTimeStamp(new Date().getTime());
		fErrorDetails.setErrorStatus(HttpStatus.BAD_REQUEST.value());
		fErrorDetails.setErrorTitle("Field Validation Error");
		fErrorDetails.setErrorDetail("Inut Field Validation Failed");
		fErrorDetails.setErrorDeveloperMessage(mNotValidException.getClass().getName());
		fErrorDetails.setErrorPath(request.getRequestURI());

		BindingResult result = mNotValidException.getBindingResult();
		List<FieldError> fieldErrors = result.getFieldErrors();
		for (FieldError error : fieldErrors) {
			FieldValidationError fError = processFieldError(error);
			List<FieldValidationError> fValidationErrorsList = fErrorDetails.getErrors().get(error.getField());
			if (fValidationErrorsList == null) {
				fValidationErrorsList = new ArrayList<FieldValidationError>();
			}
			fValidationErrorsList.add(fError);
			fErrorDetails.getErrors().put(error.getField(), fValidationErrorsList);
		}
		return new ResponseEntity<FieldValidationErrorDetails>(fErrorDetails, HttpStatus.BAD_REQUEST);
	}
*/


	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<RestMessage> handleValidationError(
			MethodArgumentNotValidException mNotValidException, HttpServletRequest request) {
		RestFieldValidationMessage fErrorDetails = new RestFieldValidationMessage();
		fErrorDetails.setTimeStamp(new Date().getTime());
		fErrorDetails.setHttpStatus(HttpStatus.BAD_REQUEST.value());
		fErrorDetails.setTitle("Field Validation Errors");
		fErrorDetails.setDetail("Input Field Validation Failed");
		fErrorDetails.setDeveloperMessage(mNotValidException.getClass().getName());
		fErrorDetails.setPath(request.getRequestURI());

		BindingResult result = mNotValidException.getBindingResult();
		List<FieldError> fieldErrors = result.getFieldErrors();
		List<FieldError> uniqueFieldErrors = getUniqueFieldErrors(fieldErrors);
		for (FieldError error : uniqueFieldErrors) {
			FieldValidationErrorMessage fError = processFieldError(error);
			//List<FieldValidationErrorMessage> fValidationErrorsList = fErrorDetails.getErrors().get(error.getField());
			//fError.setField(mappingObjectNameToJsonProperty(fError.getField()));
			List<FieldValidationErrorMessage> fValidationErrorsList = fErrorDetails.getErrors();
			if (fValidationErrorsList == null) {
				fValidationErrorsList = new ArrayList<FieldValidationErrorMessage>();
			}
			fValidationErrorsList.add(fError);
			//fErrorDetails.getErrors().put(error.getField(), fValidationErrorsList);
			fErrorDetails.setErrors(fValidationErrorsList);
		}
		ResponseEntity<RestMessage> msg = new ResponseEntity<RestMessage>(fErrorDetails, HttpStatus.BAD_REQUEST);
		return msg;
	}

	private FieldValidationErrorMessage processFieldError(final Object error) {
		FieldValidationErrorMessage fieldValidationError = new FieldValidationErrorMessage();
		String message = "";
		String field = "";
		if(error instanceof FieldError)
		{
			message = ((FieldError) error).getDefaultMessage();
			field = ((FieldError) error).getField();
		}
		else if(error instanceof ConstraintViolation)
		{
			message = ((ConstraintViolation<?>) error).getMessage();
			field = ((ConstraintViolation<?>) error).getPropertyPath().toString();
		}
		if (error != null) {
			Locale currentLocale = LocaleContextHolder.getLocale();
			String msg = messageSource.getMessage(message, null, currentLocale);
			fieldValidationError.setField(mappingObjectNameToJsonProperty(field));
			fieldValidationError.setType(MessageType.ERROR);
			fieldValidationError.setMessage(msg);
		}
		return fieldValidationError;
	}


	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	ResponseEntity<RestMessage> onConstraintValidationException(
			ConstraintViolationException e) {

		RestFieldValidationMessage fErrorDetails = new RestFieldValidationMessage();

		fErrorDetails.setTimeStamp(new Date().getTime());
		fErrorDetails.setHttpStatus(HttpStatus.BAD_REQUEST.value());
		fErrorDetails.setTitle("Field Validation Errors");
		fErrorDetails.setDetail("Input Field Validation Failed");
		fErrorDetails.setDeveloperMessage(e.getClass().getName());
		fErrorDetails.setPath("");

		List<FieldValidationErrorMessage> fValidationErrorsList = new ArrayList<FieldValidationErrorMessage>();

		//fErrorDetails.getErrors().put(error.getField(), fValidationErrorsList);
		for (ConstraintViolation violation : e.getConstraintViolations()) {
			FieldValidationErrorMessage fError = processFieldError(violation);
			if(!fValidationErrorsList.contains(fError)) {
				fValidationErrorsList.add(fError);
			}

		}
		fErrorDetails.setErrors(fValidationErrorsList);
		ResponseEntity<RestMessage> msg = new ResponseEntity<RestMessage>(fErrorDetails, HttpStatus.BAD_REQUEST);
		return msg;
	}


	@ExceptionHandler(RestMessageException.class)
	public ResponseEntity<RestMessage> onRestMessageException(Exception ex, Locale locale) {

		String title = ((RestMessageException)ex).getTitle();
		String exceptionMessage = ex.getMessage();
		if(exceptionMessage.contains(".") && exceptionMessage.split("\\.").length>2)
		{
			exceptionMessage = messageSource.getMessage(exceptionMessage, null, locale);

		}
		RestErrorMessage message = new RestErrorMessage(title,exceptionMessage);
		return new ResponseEntity<>(message, HttpStatus.OK);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<RestMessage> handleExceptions(Exception ex, Locale locale) {
		RestMessage fErrorDetails = new RestMessage();
		fErrorDetails.setTimeStamp(new Date().getTime());
		fErrorDetails.setHttpStatus(HttpStatus.BAD_REQUEST.value());
		fErrorDetails.setTitle("Errors");
		fErrorDetails.setDetail("Internel Errors");
		fErrorDetails.setDeveloperMessage(ex.getMessage());
		fErrorDetails.setPath("");
		String errorMessage = messageSource.getMessage(UNEXPECTED_ERROR, null, locale);
		ex.printStackTrace();
		return new ResponseEntity<>(fErrorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private List<FieldError> getUniqueFieldErrors(List<FieldError> fieldErrors)
	{
		List<FieldError> fieldErrorsUnique = new ArrayList<>();
		List<String> fieldErrorValues = new ArrayList<>();
		fieldErrors.forEach(fieldError -> {
			fieldErrorValues.add(fieldError.getField());
		});
		Set<String> fieldErrorValuesUnique = new HashSet<String>(fieldErrorValues);
		for (String item :  fieldErrorValuesUnique
		) {
			for (FieldError subItem : fieldErrors
			) {
				if(item == subItem.getField())
				{
					fieldErrorsUnique.add(subItem);
					break;
				}
			}
		}
		return fieldErrorsUnique;
	}

	private String mappingObjectNameToJsonProperty(String objectName)
	{
		if("doctor.qualificationId".equals(objectName)){return objectName;}
		else if("doctor.specializationId".equals(objectName)){return objectName;}
		else if("productGroupId".equals(objectName)){return "productGroup_id";}
		else if("productSegmentId".equals(objectName)){return "productSegment_id";}
		else {
			String modelName = "";
			String propertyName = "";
			if(objectName.contains(".")) {
				String[] strArray = objectName.split("\\.");
				if("userProfile".equals(strArray[0])){
					modelName = "user_profile.";
				}
				else {
					modelName = strArray[0] + ".";
				}
				propertyName = strArray[1];
				if("username".equals(propertyName)){
					propertyName = "user_name";
				}
			}
			else {
				propertyName = objectName;
			}
			String returnString = "";
			int length = propertyName.length();
			for (int i = 0; i<length; i++)
			{
				if(!Character.isUpperCase(propertyName.charAt(i))){
					returnString = returnString + propertyName.charAt(i);
				}
				else {
					returnString = returnString + "_" + propertyName.charAt(i);
				}
			}

			//change model name for chemist , doctor , stockist
			System.out.println("the model name:"+modelName);
			if(modelName.contains("chemistJoinInfo"))
			{
				modelName = modelName.replace("chemistJoinInfo","chemistJoinInfos");
			}

			return modelName + returnString.toLowerCase();
		}
	}
}