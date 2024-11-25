package io.yuchengzang.receiptprocessor.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import io.yuchengzang.receiptprocessor.model.Receipt;

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

}
