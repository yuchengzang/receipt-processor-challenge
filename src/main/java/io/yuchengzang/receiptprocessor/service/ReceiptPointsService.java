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
    totalPoints += calculatePointsFromTotalAmountRoundDollar(receipt);
    totalPoints += calculatePointsFromTotalAmountMultipleOfQuarter(receipt);
    totalPoints += calculatePointsFromItemCount(receipt);

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
   * @throws IllegalArgumentException if the receipt or its retailer is null
   */
  protected int calculatePointsFromRetailerName(Receipt receipt) throws IllegalArgumentException {
    // Validate the input
    if (receipt == null || receipt.getRetailer() == null) {
      logger.error("Receipt or retailer cannot be null.");
      throw new IllegalArgumentException("Receipt or retailer cannot be null.");
    }

    // Calculate the points based on the retailer name
    String retailer = receipt.getRetailer();
    int points = retailer.replaceAll("[^a-zA-Z0-9]", "").length();

    logger.info("Receipt ID: '{}',Rule: Retailer Name, Retailer: '{}', Points: '{}'",
        receipt.getId(), retailer, points);

    return points;
  }

  /**
   * Calculate the points based on the total amount of the receipt, where the total amount is a
   * round dollar amount with no cents.
   *
   * Rule: 50 points if the total is a round dollar amount with no cents.
   *
   * Example:
   * 1) Total amount is $100.00, so the points for this receipt is 50.
   * 2) Total amount is $99.99, so the points for this receipt is 0.
   *
   * @param receipt the Receipt object to calculate points from. It must not be null.
   * @return the number of points calculated based on the total amount. It will be 50 if the total
   *         amount is a round dollar amount, 0 otherwise.
   * @throws IllegalArgumentException if the receipt or its total amount is null
   */
  protected int calculatePointsFromTotalAmountRoundDollar(Receipt receipt)
      throws IllegalArgumentException {
    if (receipt == null || receipt.getTotalAmount() == null) {
      logger.error("Receipt or total amount cannot be null.");
      throw new IllegalArgumentException("Receipt or total amount cannot be null.");
    }

    // Compute the points based on the total amount
    BigDecimal totalAmount = receipt.getTotalAmount();

    // Check if the total amount is a round dollar amount.
    // remainder(BigDecimal.ONE): Computes the fractional part of the number.
    // If the remainder is zero, it means the number has no fractional part and is a round number
    boolean isRoundDollar = totalAmount.remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) == 0;

    int points = isRoundDollar ? 50 : 0;
    logger.info("Receipt ID: '{}', Rule: Total Amount Round Dollar, Total Amount: '{}', "
        + "Points: '{}'", receipt.getId(), totalAmount, points);

    return points;
  }

  /**
   * Calculate the points based on the total amount of the receipt, where the total amount is a
   * multiple of 0.25.
   *
   * Rule: 25 points if the total is a multiple of 0.25
   *
   * Example:
   * 1) Total amount is $12.25, so the points for this receipt is 25.
   * 2) Total amount if $15.50, so the points for this receipt is 25.
   * 3) Total amount is $10.75, so the points for this receipt is 25.
   * 4) Total amount is $99.00, so the points for this receipt is 25.
   * 5) Total amount is $99.99, so the points for this receipt is 0.
   *
   * @param receipt the Receipt object to calculate points from. It must not be null.
   * @return the number of points calculated based on the total amount. It will be 25 if the total
   *         amount is a multiple of 0.25, 0 otherwise.
   * @throws IllegalArgumentException if the receipt or its total amount is null
   */
  protected int calculatePointsFromTotalAmountMultipleOfQuarter(Receipt receipt)
      throws IllegalArgumentException {
    if (receipt == null || receipt.getTotalAmount() == null) {
      logger.error("Receipt or total amount cannot be null.");
      throw new IllegalArgumentException("Receipt or total amount cannot be null.");
    }

    // Compute the points based on the total amount
    BigDecimal totalAmount = receipt.getTotalAmount();

    // Check if the total amount is a multiple of 0.25
    boolean isMultipleOfQuarter = totalAmount.remainder(new BigDecimal("0.25"))
        .compareTo(BigDecimal.ZERO) == 0;

    int points = isMultipleOfQuarter ? 25 : 0;
    logger.info("Receipt ID: '{}', Rule: Total Amount Multiple of Quarter, Total Amount: '{}', "
        + "Points: '{}'", receipt.getId(), totalAmount, points);

    return points;
  }

  /**
   * Calculate the points based on the number of items on the receipt
   *
   * Rule: 5 points for every two items on the receipt
   *
   * Example:
   *  1) 1 item: 0 points
   *  2) 2 items: 5 points
   *  3) 3 items: 5 points
   *  4) 4 items: 10 points
   *  5) 12 items: 30 points
   *  6) 99 items: 245 points
   *
   * @param receipt the Receipt object to calculate points from. It must not be null.
   * @return the number of points calculated based on number of items on the receipt. It will be 5
   *         points for every two items on the receipt.
   * @throws IllegalArgumentException if the receipt or its items is null
   */
  protected int calculatePointsFromItemCount(Receipt receipt)
      throws IllegalArgumentException {
    if (receipt == null || receipt.getItems() == null) {
      logger.error("Receipt or items cannot be null.");
      throw new IllegalArgumentException("Receipt or items cannot be null.");
    }

    // Compute the points based on the number of items. 5 points for every two items
    int itemsCount = receipt.getItems().size();
    int points = itemsCount / 2 * 5;

    logger.info("Receipt ID: '{}', Rule: Item Count, Items Count: '{}', Points: '{}'",
        receipt.getId(), itemsCount, points);

    return points;
  }
}
