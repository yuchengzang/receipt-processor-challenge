package io.yuchengzang.receiptprocessor.exception;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * The GlobalExceptionHandler class is a global exception handler for the application.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  // Logger for logging exception details
  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  /**
   * Handles exceptions for requests to non-existing resources.
   *
   * This method catches exceptions related to accessing non-existing API endpoints
   * or static resources and responds with a NOT_FOUND (404) status.
   *
   * @param ex the NoResourceFoundException to handle
   * @return a ResponseEntity containing the error message with a NOT_FOUND (404) status
   */
  @ExceptionHandler(org.springframework.web.servlet.resource.NoResourceFoundException.class)
  public ResponseEntity<Map<String, String>> handleNoResourceFoundException(
      org.springframework.web.servlet.resource.NoResourceFoundException ex) {

    // Log the error
    logger.warn("Requested resource not found: '{}'", ex.getMessage());

    // Create an error response with a meaningful message
    Map<String, String> errorResponse = new HashMap<>();
    errorResponse.put("error", "The requested resource could not be found.");

    // Return a NOT_FOUND response
    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
  }


  /**
   * Handles validation exceptions for @Valid @RequestBody objects.
   *
   * When the application receives a request with invalid data (e.g., missing fields), this method
   * will handle the validation errors, log them, and return a concise error message.
   *
   * @param ex the MethodArgumentNotValidException to handle
   * @return a ResponseEntity containing the validation errors with a BAD_REQUEST (400) status
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, String>> handleValidationExceptions(
      MethodArgumentNotValidException ex) {

    // Create a map to store field names and their corresponding error messages
    Map<String, String> errors = new HashMap<>();

    // Iterate over the validation errors and populate the errors map
    ex.getBindingResult().getAllErrors().forEach((error) -> {
      String fieldName;

      // Check if the error is associated with a specific field
      if (error instanceof FieldError) {
        FieldError fieldError = (FieldError) error;
        fieldName = fieldError.getField();

        // Handle nested fields (e.g., items[0].price)
        if (fieldError.getObjectName() != null && fieldError.getObjectName().matches(".*\\[\\d+\\].*")) {
          fieldName = fieldError.getObjectName() + "." + fieldName;
        }
      } else {
        // For global errors not associated with a field
        fieldName = error.getObjectName();
      }

      // Get the default error message
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });

    // Log the validation errors
    logger.error("Validation failed, Exception: '{}'", ex.getMessage());

    // Return the error response with BAD_REQUEST status
    return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
  }

  /**
   * Handles validation exceptions for method parameters (e.g., invalid UUID in path variable).
   *
   * @param ex the ConstraintViolationException to handle
   * @return a ResponseEntity containing the error messages with a BAD_REQUEST (400) status
   */
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<Map<String, String>> handleConstraintViolationException(
      ConstraintViolationException ex) {

    // Create a map to store parameter names and their corresponding error messages
    Map<String, String> errors = new HashMap<>();

    // Iterate over the constraint violations and populate the errors map
    ex.getConstraintViolations().forEach(violation -> {
      String fieldName = violation.getPropertyPath().toString();

      // Optionally simplify the field name if needed
      if (fieldName.contains(".")) {
        fieldName = fieldName.substring(fieldName.lastIndexOf('.') + 1);
      }

      String errorMessage = violation.getMessage();
      errors.put(fieldName, errorMessage);
    });

    // Log the constraint violations
    logger.error("Constraint violation, Exception: '{}'", ex.getConstraintViolations());

    // Return the error response with BAD_REQUEST status
    return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
  }

  /**
   * Handles exceptions when the request body cannot be read due to malformed JSON.
   *
   * @param ex the HttpMessageNotReadableException to handle
   * @return a ResponseEntity containing a descriptive error message with a BAD_REQUEST (400) status
   */
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<Map<String, String>> handleHttpMessageNotReadableException(
      HttpMessageNotReadableException ex) {
    // Log the error
    logger.error("Malformed JSON request, Exception: '{}'", ex.getMessage());

    // Return a BAD_REQUEST response with a descriptive error message
    Map<String, String> errorResponse = new HashMap<>();
    errorResponse.put("error", "Malformed JSON request.");
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  /**
   * Handles any other exceptions not specifically handled by other exception handlers.
   *
   * @param ex the Exception to handle
   * @return a ResponseEntity containing a generic error message with an INTERNAL_SERVER_ERROR (500) status
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, String>> handleAllExceptions(Exception ex) {

    // Log the exception with stack trace
    logger.error("An unexpected error occurred: ", ex);

    // Create a map to store the generic error message
    Map<String, String> errors = new HashMap<>();
    errors.put("error", "An unexpected error occurred");

    // Return the error response with INTERNAL_SERVER_ERROR status
    return new ResponseEntity<>(errors, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
