package io.yuchengzang.receiptprocessor.model;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Item class represents an item in a receipt.
 */
public class Item {
  // Create a logger for the Item class
  private static final Logger logger = LoggerFactory.getLogger(Item.class);

  /**
   * The short description of the item.
   * For example, "Mountain Dew 12PK" or "Knorr Creamy Chicken".
   * This value must not be null or blank.
   */
  private String shortDescription;

  /**
   * The price of the item.
   * For example, 5.99 or 2.49. This value must not be negative.
   */
  private BigDecimal price;

  /**
   * The default constructor of the Item class.
   * It is required by the Jackson library to deserialize JSON objects. Do NOT remove it.
   */
  public Item() {
  }

  /**
   * The constructor of the Item class
   *
   * @param shortDescription the short description of the item
   * @param price the price of the item
   */
  public Item(String shortDescription, BigDecimal price) {
    // Validate the inputs before setting the values
    validateShortDescription(shortDescription);
    validatePrice(price);

    this.shortDescription = shortDescription;
    this.price = price;
  }

  /**
   * Get the short description of the item
   *
   * @return the short description of the item
   */
  public String getShortDescription() {
    return shortDescription;
  }

  /**
   * Set the short description of the item
   *
   * @param shortDescription the short description of the item
   */
  public void setShortDescription(String shortDescription) {
    // Validate the input before setting the value
    validateShortDescription(shortDescription);
    this.shortDescription = shortDescription;
  }

  /**
   * Get the price of the item
   *
   * @return the price of the item
   */
  public BigDecimal getPrice() {
    return price;
  }

  /**
   * Set the price of the item
   *
   * @param price the price of the item
   */
  public void setPrice(BigDecimal price) {
    // Validate the input before setting the value
    validatePrice(price);
    this.price = price;
  }

  /**
   * Return a string representation of the item
   *
   * @return a string representation of the item
   */
  @Override
  public String toString() {
    return "Item{" +
        "shortDescription='" + shortDescription + '\'' +
        ", price=" + price +
        '}';
  }

  /**
   * Compare the item with another object to determine if they are equal
   *
   * @param obj the object to compare with
   * @return true if the item is equal to the object, false otherwise
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    Item item = (Item) obj;
    return shortDescription.equals(item.shortDescription) && price.equals(item.price);
  }

  /**
   * Validate the input for the short description before setting it via the setter method of the
   * constructor. If the input is invalid, an IllegalArgumentException will be thrown.
   *
   * @param shortDescription the input short description of the item.
   *                         This value must not be null or blank.
   * @throws IllegalArgumentException if the short description is null or blank
   */
  private void validateShortDescription(String shortDescription) throws IllegalArgumentException {
    if (shortDescription == null || shortDescription.isBlank()) {
      // Log the error message and throw an IllegalArgumentException
      logger.error("Invalid short description: '{}'", shortDescription);
      throw new IllegalArgumentException("The short description must not be null or blank.");
    }
  }

  /**
   * Validate the input for the price before setting it via the setter method of the constructor.
   * If the input is invalid, an IllegalArgumentException will be thrown.
   *
   * @param price the input price of the item. This value must not be null or negative.
   * @throws IllegalArgumentException if the price is null or negative
   */
  private void validatePrice(BigDecimal price) throws IllegalArgumentException {
    if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
      // Log the error message and throw an IllegalArgumentException
      logger.error("Invalid price: '{}'", price);
      throw new IllegalArgumentException("The price must not be null or negative.");
    }
  }
}
