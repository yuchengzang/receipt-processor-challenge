package io.yuchengzang.receiptprocessor.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.math.BigDecimal;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Receipt class represents a shopping receipt.
 */
public class Receipt {
  // Create a logger for the Receipt class
  private static final Logger logger = LoggerFactory.getLogger(Receipt.class);

  /**
   * The unique identifier of the receipt.
   * This value must comply with the UUID format.
   */
   private final String id = UUID.randomUUID().toString();

  /**
   * The retailer where the receipt is from.
   * For example, "Target", "Walmart" or "Costco".
   * This value must not be null or blank.
   */
  private String retailer;

  /**
   * The purchase date of the receipt.
   * For example, "2022-01-01".
   * This value must not be null.
   */
  private LocalDate purchaseDate;

  /**
   * The purchase time of the receipt.
   * For example, "13:01".
   * This value must not be null.
   */
  private LocalTime purchaseTime;

  /**
   * The list of items in the receipt.
   * This value must not be null.
   */
  private List<Item> items;

  /**
   * The total amount of the receipt.
   * For example, 100.00 or 50.00.
   * This value must not be null or negative.
   */
  private BigDecimal totalAmount;

  /**
   * The default constructor of the Receipt class.
   * It is required by the Jackson library to deserialize JSON objects. Do NOT remove it.
   */
  public Receipt() {
  }

  /**
   * The constructor of the Receipt class
   *
   * @param retailer the retailer where the receipt is from
   * @param purchaseDate the purchase date of the receipt
   * @param purchaseTime the purchase time of the receipt
   * @param items the list of items in the receipt
   * @param totalAmount the total amount of the receipt
   */
  public Receipt(
      String retailer,
      LocalDate purchaseDate,
      LocalTime purchaseTime,
      List<Item> items,
      BigDecimal totalAmount
  ) {
    // Validate the inputs before setting the values
    validateRetailer(retailer);
    validatePurchaseDate(purchaseDate);
    validatePurchaseTime(purchaseTime);
    validateItems(items);
    validateTotalAmount(totalAmount);

    this.retailer = retailer;
    this.purchaseDate = purchaseDate;
    this.purchaseTime = purchaseTime;
    this.items = items;
    this.totalAmount = totalAmount;
  }

  /**
   * Get the unique identifier of the receipt.
   *
   * @return the unique identifier of the receipt
   */
  public String getId() {
    return id;

  }
  /**
   * Get the retailer where the receipt is from, such as "Target", "Walmart" or "Costco".
   *
   * @return the retailer where the receipt is from
   */
  public String getRetailer() {
    return retailer;
  }

  /**
   * Set the retailer where the receipt is from, such as "Target", "Walmart" or "Costco".
   *
   * @param retailer the retailer where the receipt is from
   */
  public void setRetailer(String retailer) {
    validateRetailer(retailer);
    this.retailer = retailer;
  }

  /**
   * Get the purchase date of the receipt, such as "2022-01-01".
   *
   * @return the purchase date of the receipt
   */
  public LocalDate getPurchaseDate() {
    return purchaseDate;
  }

  /**
   * Set the purchase date of the receipt, such as "2022-01-01".
   *
   * @param purchaseDate the purchase date of the receipt
   */
  public void setPurchaseDate(LocalDate purchaseDate) {
    validatePurchaseDate(purchaseDate);
    this.purchaseDate = purchaseDate;
  }

  /**
   * Get the purchase time of the receipt, such as "13:01".
   *
   * @return the purchase time of the receipt
   */
  public LocalTime getPurchaseTime() {
    return purchaseTime;
  }

  /**
   * Set the purchase time of the receipt, such as "13:01".
   *
   * @param purchaseTime the purchase time of the receipt
   */
  public void setPurchaseTime(LocalTime purchaseTime) {
    validatePurchaseTime(purchaseTime);
    this.purchaseTime = purchaseTime;
  }

  /**
   * Get the list of items in the receipt.
   *
   * @return the list of items in the receipt
   * @see Item
   */
  public List<Item> getItems() {
    return items;
  }

  /**
   * Set the list of items in the receipt.
   *
   * @param items the list of items in the receipt
   * @see Item
   */
  public void setItems(List<Item> items) {
    validateItems(items);
    this.items = items;
  }

  /**
   * Get the total amount of the receipt, such as 100.00 or 3.35.
   *
   * @return the total amount of the receipt
   */
  public BigDecimal getTotalAmount() {
    return totalAmount;
  }

  /**
   * Set the total amount of the receipt, such as 100.00 or 3.35.
   *
   * @param totalAmount the total amount of the receipt
   */
  public void setTotalAmount(BigDecimal totalAmount) {
    validateTotalAmount(totalAmount);
    this.totalAmount = totalAmount;
  }

  /**
   * Get the string representation of the receipt.
   *
   * @return the string representation of the receipt
   */
  @Override
  public String toString() {
    return "Receipt{" +
        "id='" + id + '\'' +
        ", retailer='" + retailer + '\'' +
        ", purchaseDate=" + purchaseDate +
        ", purchaseTime=" + purchaseTime +
        ", items=" + items +
        ", totalAmount=" + totalAmount +
        '}';
  }

  /**
   * Compare the receipt with another object to see if they are equal.
   *
   * @param obj the object to compare with
   * @return true if the receipt is equal to the object, false otherwise
   */
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    Receipt receipt = (Receipt) obj;
    return id.equals(receipt.id) &&
        retailer.equals(receipt.retailer) &&
        purchaseDate.equals(receipt.purchaseDate) &&
        purchaseTime.equals(receipt.purchaseTime) &&
        items.equals(receipt.items) &&
        totalAmount.equals(receipt.totalAmount);
  }

  /**
   * Validate the input for the retailer before setting it via the setter method or the constructor
   *
   * @param retailer the retailer input. It must not be null or blank.
   * @throws IllegalArgumentException if the retailer is null or blank
   */
  private void validateRetailer(String retailer) throws IllegalArgumentException {
    if (retailer == null || retailer.isBlank()) {
      logger.error("Invalid retailer: '{}'", retailer);
      throw new IllegalArgumentException("Retailer must not be null or blank");
    }
  }

  /**
   * Validate the input for the purchase date before setting it via the setter method or the
   * constructor
   *
   * @param purchaseDate the purchase date input. It must not be null.
   * @throws IllegalArgumentException if the purchase date is null
   */
  private void validatePurchaseDate(LocalDate purchaseDate) throws IllegalArgumentException {
    if (purchaseDate == null) {
      logger.error("Invalid purchase date: 'null'");
      throw new IllegalArgumentException("Purchase date must not be null");
    }
  }

  /**
   * Validate the input for the purchase time before setting it via the setter method or the
   * constructor
   *
   * @param purchaseTime the purchase time input. It must not be null.
   * @throws IllegalArgumentException if the purchase time is null
   */
  private void validatePurchaseTime(LocalTime purchaseTime) throws IllegalArgumentException {
    if (purchaseTime == null) {
      logger.error("Invalid purchase time: 'null'");
      throw new IllegalArgumentException("Purchase time must not be null");
    }
  }

  /**
   * Validate the input for the items before setting it via the setter method or the constructor
   *
   * @param items the items input. It must not be null or empty.
   * @throws IllegalArgumentException if the items is null or empty
   */
  private void validateItems(List<Item> items) throws IllegalArgumentException {
    if (items == null) {
      logger.error("Invalid items: 'null'");
      throw new IllegalArgumentException("Items must not be null");
    }

    if (items.isEmpty()) {
      logger.error("Invalid items: '{}' is empty", items);
      throw new IllegalArgumentException("Items must not be empty");
    }
  }

  /**
   * Validate the input for the total amount before setting it via the setter method or the
   * constructor
   *
   * @param totalAmount the total amount input. It must not be null or negative.
   * @throws IllegalArgumentException if the total amount is null or negative
   */
  private void validateTotalAmount(BigDecimal totalAmount) throws IllegalArgumentException {
    if (totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) < 0) {
      logger.error("Invalid total amount: '{} is null or negative'", totalAmount);
      throw new IllegalArgumentException("Total amount must not be null or negative");
    }
  }
}
