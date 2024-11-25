package io.yuchengzang.receiptprocessor.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

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
}
