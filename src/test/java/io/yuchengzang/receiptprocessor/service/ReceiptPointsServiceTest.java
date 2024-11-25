package io.yuchengzang.receiptprocessor.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Collections;

import io.yuchengzang.receiptprocessor.model.Receipt;
import io.yuchengzang.receiptprocessor.model.Item;

public class ReceiptPointsServiceTest {

  /**
   * A empty receipt that can be assigned with various different values for testing.
   */
  private Receipt testReceipt;

  /**
   * A ReceiptPointsService object that can be used to test the calculatePoints method.
   */
  private ReceiptPointsService receiptPointsService;

  @BeforeEach
  void setUp() {
    testReceipt = new Receipt();
    receiptPointsService = new ReceiptPointsService();
  }

  /**
   * Testing rule: One point for every alphanumeric character in the retailer name.
   *
   * Test case: Retailer name is "Target1076", which has 10 alphanumeric characters, so the points
   * should be 10.
   */
  @Test
  void testValidCalculatePointsFromRetailerNameOnlyAlphanumeric1() {
    testReceipt.setRetailer("Target1076");
    assertEquals(10, receiptPointsService.calculatePointsFromRetailerName(testReceipt));
  }

  /**
   * Testing rule: One point for every alphanumeric character in the retailer name.
   *
   * Test case: Retailer name is "12Walmart", which has 9 alphanumeric characters, so the points
   * should be 9.
   */
  @Test
  void testValidCalculatePointsFromRetailerNameOnlyAlphanumeric2() {
    testReceipt.setRetailer("12Walmart");
    assertEquals(9, receiptPointsService.calculatePointsFromRetailerName(testReceipt));
  }

  /**
   * Testing rule: One point for every alphanumeric character in the retailer name.
   *
   * Test case: Retailer name is "M&M Corner Market", which has 14 alphanumeric characters (' ' and
   * '&' are not counted), so the points should be 14.
   */
  @Test
  void testValidCalculatePointsFromRetailerNameComplex1() {
    testReceipt.setRetailer("M&M Corner Market");
    assertEquals(14, receiptPointsService.calculatePointsFromRetailerName(testReceipt));
  }

  /**
   * Testing rule: One point for every alphanumeric character in the retailer name.
   *
   * Test case: Retailer name is "Costco Gas #1020", which has 13 alphanumeric characters (' ' and
   * '#' are not counted), so the points should be 13.
   */
  @Test
  void testValidCalculatePointsFromRetailerNameComplex2() {
    testReceipt.setRetailer("Costco Gas #1020");
    assertEquals(13, receiptPointsService.calculatePointsFromRetailerName(testReceipt));
  }

  /**
   * Testing rule: One point for every alphanumeric character in the retailer name.
   *
   * Test case: Retailer name is "!@#$%^&123456789    ?? 123jihoiny|| abc abc abc ", which has 28
   * alphanumeric characters (' ' and special characters are not counted), so the points should be
   * 28.
   */
  @Test
  void testValidCalculatePointsFromRetailerNameSmoke() {
    testReceipt.setRetailer("!@#$%^&123456789    ?? 123jihoiny|| abc abc abc ");
    assertEquals(28, receiptPointsService.calculatePointsFromRetailerName(testReceipt));
  }

  /**
   * Testing rule: One point for every alphanumeric character in the retailer name.
   *
   * Test case: Retailer name is "#!!!???", which has 0 alphanumeric characters, so the points
   * should be 0.
   */
  @Test
  void testValidCalculatePointsFromRetailerNameNoAlphanumeric() {
    testReceipt.setRetailer("#!!!???");
    assertEquals(0, receiptPointsService.calculatePointsFromRetailerName(testReceipt));
  }

  @Test
  void testInvalidCalculatePointsFromRetailerNameNullReceipt() {
    assertThrows(IllegalArgumentException.class, () -> {
      receiptPointsService.calculatePointsFromRetailerName(null);
    });
  }

  /**
   * Testing rule: 50 points if the total is a round dollar amount with no cents.
   *
   * Test case: Total amount is $100.00, a round dollar amount, so the points for this receipt is 50
   */
  @Test
  void testValidCalculatePointsFromTotalAmountRoundDollarRound1() {
    testReceipt.setTotalAmount(new BigDecimal("100.00"));
    assertEquals(50,
        receiptPointsService.calculatePointsFromTotalAmountRoundDollar(testReceipt));
  }

  /**
   * Testing rule: 50 points if the total is a round dollar amount with no cents.
   *
   * Test case: Total amount is $0.00, a round dollar amount, so the points for this receipt is 50
   */
  @Test
  void testValidCalculatePointsFromTotalAmountRoundDollarRound2() {
    testReceipt.setTotalAmount(new BigDecimal("0.00"));
    assertEquals(50,
        receiptPointsService.calculatePointsFromTotalAmountRoundDollar(testReceipt));
  }

  /**
   * Testing rule: 50 points if the total is a round dollar amount with no cents.
   *
   * Test case: Total amount is $12, a round dollar amount but without trailing ".00", so the points
   * for this receipt is 50
   */
  @Test
  void testValidCalculatePointsFromTotalAmountRoundDollarRound3() {
    testReceipt.setTotalAmount(new BigDecimal("12"));
    assertEquals(50,
        receiptPointsService.calculatePointsFromTotalAmountRoundDollar(testReceipt));
  }

  /**
   * Testing rule: 50 points if the total is a round dollar amount with no cents.
   *
   * Test case: Total amount is $0.01, not a round dollar amount, so the points for this receipt is
   * 0
   */
  @Test
  void testValidCalculatePointsFromTotalAmountRoundDollarNonRound1() {
    testReceipt.setTotalAmount(new BigDecimal("0.01"));
    assertEquals(0,
        receiptPointsService.calculatePointsFromTotalAmountRoundDollar(testReceipt));
  }

  /**
   * Testing rule: 50 points if the total is a round dollar amount with no cents.
   *
   * Test case: Total amount is $99.99, not a round dollar amount, so the points for this receipt is
   * 0
   */
  @Test
  void testValidCalculatePointsFromTotalAmountRoundDollarNonRound2() {
    testReceipt.setTotalAmount(new BigDecimal("99.99"));
    assertEquals(0,
        receiptPointsService.calculatePointsFromTotalAmountRoundDollar(testReceipt));
  }

  /**
   * Testing rule: 50 points if the total is a round dollar amount with no cents.
   *
   * Test case: Total amount is $10.50, not a round dollar amount, so the points for this receipt is
   * 0
   */
  @Test
  void testValidCalculatePointsFromTotalAmountRoundDollarNonRound3() {
    testReceipt.setTotalAmount(new BigDecimal("10.50"));
    assertEquals(0,
        receiptPointsService.calculatePointsFromTotalAmountRoundDollar(testReceipt));
  }

  @Test
  void testInvalidCalculatePointsFromTotalAmountRoundDollarNullReceipt() {
    assertThrows(IllegalArgumentException.class, () -> {
      receiptPointsService.calculatePointsFromTotalAmountRoundDollar(null);
    });
  }

  /**
   * Testing rule: 25 points if the total is a multiple of 0.25.
   *
   * Test case: Total amount is $12.25, a multiple of 0.25, so the points for this receipt is 25
   */
  @Test
  void testValidCalculatePointsFromTotalAmountMultipleOfQuarterMultiple1() {
    testReceipt.setTotalAmount(new BigDecimal("12.25"));
    assertEquals(25,
        receiptPointsService.calculatePointsFromTotalAmountMultipleOfQuarter(testReceipt));
  }

  /**
   * Testing rule: 25 points if the total is a multiple of 0.25.
   *
   * Test case: Total amount is $15.50, a multiple of 0.25, so the points for this receipt is 25
   */
  @Test
  void testValidCalculatePointsFromTotalAmountMultipleOfQuarterMultiple2() {
    testReceipt.setTotalAmount(new BigDecimal("15.50"));
    assertEquals(25,
        receiptPointsService.calculatePointsFromTotalAmountMultipleOfQuarter(testReceipt));
  }

  /**
   * Testing rule: 25 points if the total is a multiple of 0.25.
   *
   * Test case: Total amount is $10.75, a multiple of 0.25, so the points for this receipt is 25
   */
  @Test
  void testValidCalculatePointsFromTotalAmountMultipleOfQuarterMultiple3() {
    testReceipt.setTotalAmount(new BigDecimal("10.75"));
    assertEquals(25,
        receiptPointsService.calculatePointsFromTotalAmountMultipleOfQuarter(testReceipt));
  }

  /**
   * Testing rule: 25 points if the total is a multiple of 0.25.
   *
   * Test case: Total amount is $99.00, a multiple of 0.25, so the points for this receipt is 25
   */
  @Test
  void testValidCalculatePointsFromTotalAmountMultipleOfQuarterMultiple4() {
    testReceipt.setTotalAmount(new BigDecimal("99.00"));
    assertEquals(25,
        receiptPointsService.calculatePointsFromTotalAmountMultipleOfQuarter(testReceipt));
  }

  /**
   * Testing rule: 25 points if the total is a multiple of 0.25.
   *
   * Test case: Total amount is 0.01, not a multiple of 0.25, so the points for this receipt is 0
   */
  @Test
  void testValidCalculatePointsFromTotalAmountMultipleOfQuarterNonMultiple1() {
    testReceipt.setTotalAmount(new BigDecimal("0.01"));
    assertEquals(0,
        receiptPointsService.calculatePointsFromTotalAmountMultipleOfQuarter(testReceipt));
  }

  /**
   * Testing rule: 25 points if the total is a multiple of 0.25.
   *
   * Test case: Total amount is $12.26, not a multiple of 0.25, so the points for this receipt is 0
   */
  @Test
  void testValidCalculatePointsFromTotalAmountMultipleOfQuarterNonMultiple2() {
    testReceipt.setTotalAmount(new BigDecimal("12.26"));
    assertEquals(0,
        receiptPointsService.calculatePointsFromTotalAmountMultipleOfQuarter(testReceipt));
  }

  /**
   * Testing rule: 25 points if the total is a multiple of 0.25.
   *
   * Test case: Total amount is $100.49, not a multiple of 0.25, so the points for this receipt is 0
   */
  @Test
  void testValidCalculatePointsFromTotalAmountMultipleOfQuarterNonMultiple3() {
    testReceipt.setTotalAmount(new BigDecimal("100.49"));
    assertEquals(0,
        receiptPointsService.calculatePointsFromTotalAmountMultipleOfQuarter(testReceipt));
  }

  @Test
  void testInvalidCalculatePointsFromTotalAmountMultipleOfQuarterNullReceipt() {
    assertThrows(IllegalArgumentException.class, () -> {
      receiptPointsService.calculatePointsFromTotalAmountMultipleOfQuarter(null);
    });
  }

  /**
   * Testing rule: 5 points for every two items on the receipt
   *
   * Test case: 1 item, so the points for this receipt is 0
   */
  @Test
  void testValidCalculatePointsFromItemCount1Item() {
    Item item1 = new Item("Mountain Dew 12PK", new BigDecimal("5.99"));
    testReceipt.setItems(List.of(item1));

    assertEquals(0, receiptPointsService.calculatePointsFromItemCount(testReceipt));
  }

  /**
   * Testing rule: 5 points for every two items on the receipt
   *
   * Test case: 2 items, so the points for this receipt is 5
   */
  @Test
  void testValidCalculatePointsFromItemCount2Items() {
    Item item1 = new Item("Mountain Dew 12PK", new BigDecimal("5.99"));
    Item item2 = new Item("Coca-Cola 12PK", new BigDecimal("5.99"));
    testReceipt.setItems(List.of(item1, item2));

    assertEquals(5, receiptPointsService.calculatePointsFromItemCount(testReceipt));
  }

  /**
   * Testing rule: 5 points for every two items on the receipt
   *
   * Test case: 3 items, so the points for this receipt is 5
   */
  @Test
  void testValidCalculatePointsFromItemCount3Items() {
    Item item1 = new Item("Mountain Dew 12PK", new BigDecimal("5.99"));
    Item item2 = new Item("Coca-Cola 12PK", new BigDecimal("5.99"));
    Item item3 = new Item("Pepsi 12PK", new BigDecimal("5.99"));
    testReceipt.setItems(List.of(item1, item2, item3));

    assertEquals(5, receiptPointsService.calculatePointsFromItemCount(testReceipt));
  }

  /**
   * Testing rule: 5 points for every two items on the receipt
   *
   * Test case: 4 items, so the points for this receipt is 10
   */
  @Test
  void testValidCalculatePointsFromItemCount4Items() {
    Item item1 = new Item("Mountain Dew 12PK", new BigDecimal("5.99"));
    Item item2 = new Item("Coca-Cola 12PK", new BigDecimal("5.99"));
    Item item3 = new Item("Pepsi 12PK", new BigDecimal("5.99"));
    Item item4 = new Item("Sprite 12PK", new BigDecimal("5.99"));
    testReceipt.setItems(List.of(item1, item2, item3, item4));

    assertEquals(10, receiptPointsService.calculatePointsFromItemCount(testReceipt));
  }

  /**
   * Testing rule: 5 points for every two items on the receipt
   *
   * Test case: 12 items, so the points for this receipt is 30
   */
  @Test
  void testValidCalculatePointsFromItemCount12Items() {
    // Create a list of 12 items
    Item item = new Item("Mountain Dew 12PK", new BigDecimal("5.99"));
    List<Item> items = Collections.nCopies(12, item);
    testReceipt.setItems(items);

    assertEquals(30, receiptPointsService.calculatePointsFromItemCount(testReceipt));
  }

  /**
   * Testing rule: 5 points for every two items on the receipt
   *
   * Test case: 99 items, so the points for this receipt is 245
   */
  @Test
  void testValidCalculatePointsFromItemCount99Items() {
    // Create a list of 99 items
    Item item = new Item("Mountain Dew 12PK", new BigDecimal("5.99"));
    List<Item> items = Collections.nCopies(99, item);
    testReceipt.setItems(items);

    assertEquals(245, receiptPointsService.calculatePointsFromItemCount(testReceipt));
  }

  @Test
  void testInvalidCalculatePointsFromItemCountNullReceipt() {
    assertThrows(IllegalArgumentException.class, () -> {
      receiptPointsService.calculatePointsFromItemCount(null);
    });
  }

  /**
   * Testing rule: If the trimmed length of the item description is a multiple of 3, multiply the
   * price by 0.2 and round up to the nearest integer. The result is the number of points earned.
   *
   * Test case: 1 item.
   * Item: "Mountain Dew 12PK", Price: $5.99, Points: 0
   * (The trimmed length is 17, which is not a multiple of 3)
   */
  @Test
  void testValidCalculatePointsFromItemDescriptionLength1ItemTest1() {
    Item item = new Item("Mountain Dew 12PK", new BigDecimal("5.99"));
    testReceipt.setItems(List.of(item));

    assertEquals(0,
        receiptPointsService.calculatePointsFromItemDescriptionLength(testReceipt));
  }

  /**
   * Testing rule: If the trimmed length of the item description is a multiple of 3, multiply the
   * price by 0.2 and round up to the nearest integer. The result is the number of points earned.
   *
   * Test case: 1 item.
   * Item: "Coca-Cola 12PKG", Price: $5.99, Points: 2
   * (trimmed length is 15, which is a multiple of 3. points = 5.99 * 0.2 = 1.198, rounded up to 2)
   */
  @Test
  void testValidCalculatePointsFromItemDescriptionLength1ItemTest2() {
    Item item = new Item("Coca-Cola 12PKG", new BigDecimal("5.99"));
    testReceipt.setItems(List.of(item));

    assertEquals(2,
        receiptPointsService.calculatePointsFromItemDescriptionLength(testReceipt));
  }

  /**
   * Testing rule: If the trimmed length of the item description is a multiple of 3, multiply the
   * price by 0.2 and round up to the nearest integer. The result is the number of points earned.
   *
   * Test case: 1 item.
   * Item: "Emils Cheese Pizza", Price: $12.25, Points: 3
   * (trimmed length is 18, which is a multiple of 3. points = 12.25 * 0.2 = 2.45, rounded up to 3)
   */
  @Test
  void testValidCalculatePointsFromItemDescriptionLength1ItemTest3() {
    Item item = new Item("Emils Cheese Pizza", new BigDecimal("12.25"));
    testReceipt.setItems(List.of(item));

    assertEquals(3,
        receiptPointsService.calculatePointsFromItemDescriptionLength(testReceipt));
  }

  /**
   * Testing rule: If the trimmed length of the item description is a multiple of 3, multiply the
   * price by 0.2 and round up to the nearest integer. The result is the number of points earned.
   *
   * Test case: 1 item.
   * Item: "   Klarbrunn 12-PK 12 FL OZ  ", Price: $12.00, Points: 3
   * (trimmed length is 24, which is a multiple of 3. points = 12.00 * 0.2 = 2.4, rounded up to 3)
   */
  @Test
  void testValidCalculatePointsFromItemDescriptionLength1ItemTest4() {
    Item item = new Item("   Klarbrunn 12-PK 12 FL OZ  ", new BigDecimal("12.00"));
    testReceipt.setItems(List.of(item));

    assertEquals(3,
        receiptPointsService.calculatePointsFromItemDescriptionLength(testReceipt));
  }

  /**
   * Testing rule: If the trimmed length of the item description is a multiple of 3, multiply the
   * price by 0.2 and round up to the nearest integer. The result is the number of points earned.
   *
   * Test case: 2 items.
   * Item1: "Mountain Dew 12PK", Price: $5.99
   * (The trimmed length is 17, which is not a multiple of 3)
   * Item2: "Coca-Cola 12PKG", Price: $5.99
   * (trimmed length is 15, which is a multiple of 3. points = 5.99 * 0.2 = 1.198, rounded up to 2)
   * Points: 2
   */
  @Test
  void testValidCalculatePointsFromItemDescriptionLength2ItemsTest1() {
    Item item1 = new Item("Mountain Dew 12PK", new BigDecimal("5.99"));
    Item item2 = new Item("Coca-Cola 12PKG", new BigDecimal("5.99"));
    testReceipt.setItems(List.of(item1, item2));

    assertEquals(2,
        receiptPointsService.calculatePointsFromItemDescriptionLength(testReceipt));
  }

  /**
   * Testing rule: If the trimmed length of the item description is a multiple of 3, multiply the
   * price by 0.2 and round up to the nearest integer. The result is the number of points earned.
   *
   * Test case: 2 items.
   * Item1: "Coca-Cola 12PKG", Price: $5.99
   * (trimmed length is 15, which is a multiple of 3. points = 5.99 * 0.2 = 1.198, rounded up to 2)
   * Item2: "Emils Cheese Pizza", Price: $12.25
   * (trimmed length is 18, which is a multiple of 3. points = 12.25 * 0.2 = 2.45, rounded up to 3)
   * Points: 5
   */
  @Test
  void testValidCalculatePointsFromItemDescriptionLength2ItemsTest2() {
    Item item1 = new Item("Coca-Cola 12PKG", new BigDecimal("5.99"));
    Item item2 = new Item("Emils Cheese Pizza", new BigDecimal("12.25"));
    testReceipt.setItems(List.of(item1, item2));

    assertEquals(5,
        receiptPointsService.calculatePointsFromItemDescriptionLength(testReceipt));
  }

  /**
   * Testing rule: If the trimmed length of the item description is a multiple of 3, multiply the
   * price by 0.2 and round up to the nearest integer. The result is the number of points earned.
   *
   * Test case: 500 items.
   * Item: "Mountain Dew 12PK", Price: $5.99
   *
   * It adds 500 items with the same description and price. The trimmed length of the description
   * is 17, which is not a multiple of 3, so the points for this receipt is 0.
   */
  @Test
  void testValidCalculatePointsFromItemDescriptionLengthSmoke1() {
    Item item = new Item("Mountain Dew 12PK", new BigDecimal("5.99"));
    List<Item> items = Collections.nCopies(500, item);
    testReceipt.setItems(items);

    assertEquals(0,
        receiptPointsService.calculatePointsFromItemDescriptionLength(testReceipt));
  }

  /**
   * Testing rule: If the trimmed length of the item description is a multiple of 3, multiply the
   * price by 0.2 and round up to the nearest integer. The result is the number of points earned.
   *
   * Test case: 500 items.
   * Item: "Emils Cheese Pizza", Price: $12.25
   *
   * It adds 500 items with the same description and price. The trimmed length of the description
   * is 18, which is a multiple of 3, so the points for this receipt is 3 * 500 = 1500.
   */
  @Test
  void testValidCalculatePointsFromItemDescriptionLengthSmoke2() {
    Item item = new Item("Emils Cheese Pizza", new BigDecimal("12.25"));
    List<Item> items = Collections.nCopies(500, item);
    testReceipt.setItems(items);

    assertEquals(1500,
        receiptPointsService.calculatePointsFromItemDescriptionLength(testReceipt));
  }

  @Test
  void testInvalidCalculatePointsFromItemDescriptionLengthNullReceipt() {
    assertThrows(IllegalArgumentException.class, () -> {
      receiptPointsService.calculatePointsFromItemDescriptionLength(null);
    });
  }

  /**
   * Testing rule: 6 points if the day in the purchase date is odd.
   *
   * Test case: Purchase date is 2022-01-01, which is an odd day, so the points for this receipt
   * is 6
   */
  @Test
  void testValidCalculatePointsFromPurchaseDateJan01() {
    testReceipt.setPurchaseDate(LocalDate.parse("2022-01-01"));
    assertEquals(6, receiptPointsService.calculatePointsFromPurchaseDate(testReceipt));
  }

  /**
   * Testing rule: 6 points if the day in the purchase date is odd.
   *
   * Test case: Purchase date is 2022-01-02, which is not an odd day, so the points for this receipt
   * is 0
   */
  @Test
  void testValidCalculatePointsFromPurchaseDateJan02() {
    testReceipt.setPurchaseDate(LocalDate.parse("2022-01-02"));
    assertEquals(0, receiptPointsService.calculatePointsFromPurchaseDate(testReceipt));
  }

  /**
   * Testing rule: 6 points if the day in the purchase date is odd.
   *
   * Test case: Purchase date is 2022-01-31 (edge case, the last day of of a month), which is an odd
   * day, so the points for this receipt is 6
   */
  @Test
  void testValidCalculatePointsFromPurchaseDateJan31() {
    testReceipt.setPurchaseDate(LocalDate.parse("2022-01-31"));
    assertEquals(6, receiptPointsService.calculatePointsFromPurchaseDate(testReceipt));
  }

  /**
   * Testing rule: 6 points if the day in the purchase date is odd.
   *
   * Test case: Purchase date is 2022-02-28 (edge case, the last day of a month), which is not an
   * odd day, so the points for this receipt is 0
   */
  @Test
  void testValidCalculatePointsFromPurchaseDateFeb28() {
    testReceipt.setPurchaseDate(LocalDate.parse("2022-02-28"));
    assertEquals(0, receiptPointsService.calculatePointsFromPurchaseDate(testReceipt));
  }

  /**
   * Testing rule: 6 points if the day in the purchase date is odd.
   *
   * Test case: Purchase date is 2020-02-29 (edge case, a leap year), which is an odd day, so the
   * points for this receipt is 6
   */
  @Test
  void testValidCalculatePointsFromPurchaseDateFeb29() {
    testReceipt.setPurchaseDate(LocalDate.parse("2020-02-29"));
    assertEquals(6, receiptPointsService.calculatePointsFromPurchaseDate(testReceipt));
  }

  @Test
  void testInvalidCalculatePointsFromPurchaseDateNullReceipt() {
    assertThrows(IllegalArgumentException.class, () -> {
      receiptPointsService.calculatePointsFromPurchaseDate(null);
    });
  }


}
