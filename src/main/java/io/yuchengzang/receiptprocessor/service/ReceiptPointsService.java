package io.yuchengzang.receiptprocessor.service;

import java.math.BigDecimal;
import java.time.LocalTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.yuchengzang.receiptprocessor.model.Receipt;

/**
 * The ReceiptPointsService class provides services for calculating points from receipts.
 */
public class ReceiptPointsService {

  // Create a logger for the ReceiptPointsService class
  private static final Logger logger = LoggerFactory.getLogger(ReceiptPointsService.class);

  /**
   * Calculate the points from a receipt
   *
   * @param receipt the Receipt object to calculate points from. It must not be null.
   * @return the number of points calculated from the receipt
   * @throws IllegalArgumentException if the receipt is null
   */
  public int calculatePoints(Receipt receipt) throws IllegalArgumentException {
    // Validate the input
    if (receipt == null) {
      logger.error("Receipt cannot be null.");
      throw new IllegalArgumentException("Receipt cannot be null.");
    }

    // Calculate the points based on the total amount of the receipt
    int totalPoints = 0;
    totalPoints += calculatePointsFromRetailerName(receipt);

    logger.info("Total points calculated for receipt ID '{}': '{}'", receipt.getId(), totalPoints);
    return totalPoints;
  }

  /**
   * Calculate the points based on the total amount of the receipt
   *
   * Rule: one point for every alphanumeric character in the retailer name.
   *
   * Example:
   * 1) "Target" has 6 characters. So, the points for a receipt from Target is 6.
   * 2) "M&M Corner Market", it has only 14 alphanumeric characters (' ' and '&' are not counted).
   *
   * @param receipt the Receipt object to calculate points from. It must not be null.
   * @return the number of points calculated based on the total amount
   * @throws IllegalArgumentException if the receipt is null
   */
  protected int calculatePointsFromRetailerName(Receipt receipt) throws IllegalArgumentException {
    // Validate the input
    if (receipt == null || receipt.getRetailer() == null) {
      logger.error("Receipt cannot be null.");
      throw new IllegalArgumentException("Receipt or retailer cannot be null.");
    }

    // Calculate the points based on the retailer name
    String retailer = receipt.getRetailer();
    int points = retailer.replaceAll("[^a-zA-Z0-9]", "").length();

    logger.info("Receipt ID: '{}',Rule: Retailer Name, Retailer: '{}', Points: '{}'",
        receipt.getId(), retailer, points);

    return points;
  }
}
