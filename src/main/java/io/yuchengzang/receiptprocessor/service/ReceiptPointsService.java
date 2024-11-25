package io.yuchengzang.receiptprocessor.service;

import java.math.BigDecimal;
import java.time.LocalTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.yuchengzang.receiptprocessor.model.Receipt;
import io.yuchengzang.receiptprocessor.model.Item;

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
    totalPoints += calculatePointsFromItemDescriptionLength(receipt);
    totalPoints += calculatePointsFromPurchaseDate(receipt);
    totalPoints += calculatePointsFromPurchaseTime(receipt);

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

  /**
   * Calculate the points based on the length of `shortDescription` of the items in the receipt
   *
   * Rule: If the trimmed length of the item description is a multiple of 3, multiply the price by
   * 0.2 and round up to the nearest integer. The result is the number of points earned.
   *
   * Example:
   * 1) Item: "Mountain Dew 12PK", Price: $5.99, Points: 0
   * 2) Item: "Coca-Cola 12PKG", Price: $5.99, Points: 2
   * 3) Item: "Emils Cheese Pizza", Price: $12.25, Points: 3
   * 4) Item: "   Klarbrunn 12-PK 12 FL OZ  ", Price: $12.00, Points: 3
   *
   * @param receipt the Receipt object to calculate points from. It must not be null.
   * @return the number of points calculated based on the length of the item description
   * @throws IllegalArgumentException if the receipt or its items is null
   */
  protected int calculatePointsFromItemDescriptionLength(Receipt receipt)
      throws IllegalArgumentException {
    if (receipt == null || receipt.getItems() == null) {
      logger.error("Receipt or items cannot be null.");
      throw new IllegalArgumentException("Receipt or items cannot be null.");
    }

    // Compute the points based on the length of the item description
    int points = 0;

    for (Item item : receipt.getItems()) {
      String trimmedShortDescription = item.getShortDescription().trim();
      BigDecimal price = item.getPrice();
      int thisItemPoints = 0;

      if (trimmedShortDescription.length() % 3 == 0) {
        thisItemPoints = (int)Math.ceil(price.multiply(new BigDecimal("0.2")).doubleValue());
      }

      points += thisItemPoints;

      logger.info("Receipt ID: '{}', Rule: Item Description Length, Description: '{}', Price: '{}',"
          + " Trimmed Length: '{}', This Item Points: '{}', Total Running Points: '{}'",
          receipt.getId(), item.getShortDescription(), price, trimmedShortDescription.length(),
          thisItemPoints, points);
    }

    logger.info("Receipt ID: '{}', Rule: Item Description Length, Points: '{}'",
        receipt.getId(), points);

    return points;
  }

  /**
   * Calculate the points based on the purchase date of the receipt
   *
   * Rule: 6 points if the day in the purchase date is odd.
   *
   * Example:
   * 1) Purchase date: 2022-01-01, Points: 6
   * 2) Purchase date: 2022-01-02, Points: 0
   * 3) Purchase date: 2022-01-31, Points: 6
   * 4) Purchase date: 2022-02-28, Points: 0
   * 5) Purchase date: 2020-02-29, Points: 6 (leap year)
   *
   * @param receipt the Receipt object to calculate points from. It must not be null.
   * @return the number of points calculated based on the purchase date
   * @throws IllegalArgumentException if the receipt or its purchase date is null
   */
  protected int calculatePointsFromPurchaseDate(Receipt receipt) throws IllegalArgumentException {
    // Validate the input
    if (receipt == null || receipt.getPurchaseDate() == null) {
      logger.error("Receipt or purchase date cannot be null.");
      throw new IllegalArgumentException("Receipt or purchase date cannot be null.");
    }

    // Calculate the points based on the purchase date
    int day = receipt.getPurchaseDate().getDayOfMonth();
    int points = 0;

    // Check if the day is odd. Odd days have 6 points
    if (day % 2 == 1) {
      points = 6;
    }

    logger.info("Receipt ID: '{}', Rule: Purchase Date, Purchase Date: '{}', Day: '{}',"
            + " Points: '{}'", receipt.getId(), receipt.getPurchaseDate(), day, points);

    return points;
  }

  /**
   * Calculate the points based on the purchase time of the receipt
   *
   * Rule: 10 points if the time of purchase is after 2:00pm and before 4:00pm.
   *
   * Example:
   * 1) Purchase time: 13:00, Points: 0
   * 2) Purchase time: 13:59, Points: 0 (Edge case, last minute before 2:00pm)
   * 3) Purchase time: 14:00, Points: 0 (Edge case, exactly 2:00pm)
   * 4) Purchase time: 14:01, Points: 10 (Edge case, first minute after 2:00pm)
   * 5) Purchase time: 14:23, Points: 10
   * 6) Purchase time: 15:21, Points: 10
   * 7) Purchase time: 15:59, Points: 10 (Edge case, last minute before 4:00pm)
   * 8) Purchase time: 16:00, Points: 0 (Edge case, first minute after 4:00pm)
   * 9) Purchase time: 17:00, Points: 0
   *
   * @param receipt the Receipt object to calculate points from. It must not be null.
   * @return the number of points calculated based on the purchase time
   * @throws IllegalArgumentException if the receipt or its purchase time is null
   */
  protected int calculatePointsFromPurchaseTime(Receipt receipt) throws IllegalArgumentException {
    // Validate the input
    if (receipt == null || receipt.getPurchaseTime() == null) {
      logger.error("Receipt or purchase time cannot be null.");
      throw new IllegalArgumentException("Receipt or purchase time cannot be null.");
    }

    // Calculate the points based on the purchase time
    LocalTime purchaseTime = receipt.getPurchaseTime();
    int points = 0;

    // Check if the purchase time is after 2:00pm and before 4:00pm
    if (purchaseTime.isAfter(LocalTime.of(14, 0)) && purchaseTime.isBefore(LocalTime.of(16, 0))) {
      points = 10;
    }

    logger.info("Receipt ID: '{}', Rule: Purchase Time, Purchase Time: '{}', Points: '{}'",
        receipt.getId(), purchaseTime, points);

    return points;
  }
}
