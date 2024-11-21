package io.yuchengzang.receiptprocessor.model;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ItemTest {

  @Test
  void testValidItemConstructor() {
    Item item = new Item("Mountain Dew 12PK", new BigDecimal("5.99"));
    assertEquals("Mountain Dew 12PK", item.getShortDescription());
    assertEquals(new BigDecimal("5.99"), item.getPrice());
  }

  @Test
  void testValidItemConstructorWithZeroPrice() {
    Item item = new Item("Mountain Dew 12PK", new BigDecimal("0.00"));
    assertEquals("Mountain Dew 12PK", item.getShortDescription());
    assertEquals(new BigDecimal("0.00"), item.getPrice());
  }

  @Test
  void testValidItemConstructorWithShortShortDescription() {
    Item item = new Item("A", new BigDecimal("5.99"));
    assertEquals("A", item.getShortDescription());
    assertEquals(new BigDecimal("5.99"), item.getPrice());
  }

  @Test
  void testInvalidItemConstructorWithNullShortDescription() {
    assertThrows(IllegalArgumentException.class, () -> {
      new Item(null, new BigDecimal("5.99"));
    });
  }

  @Test
  void testInvalidItemConstructorWithBlankShortDescription() {
    assertThrows(IllegalArgumentException.class, () -> {
      new Item("    ", new BigDecimal("5.99"));
    });
  }

  @Test
  void testInvalidItemConstructorWithNullPrice() {
    assertThrows(IllegalArgumentException.class, () -> {
      new Item("Mountain Dew 12PK", null);
    });
  }

  @Test
  void testInvalidItemConstructorWithNegativePrice() {
    assertThrows(IllegalArgumentException.class, () -> {
      new Item("Mountain Dew 12PK", new BigDecimal("-5.99"));
    });
  }

  @Test
  void testValidItemSetShortDescription() {
    Item item = new Item("Mountain Dew 12PK", new BigDecimal("5.99"));
    item.setShortDescription("Knorr Creamy Chicken");
    assertEquals("Knorr Creamy Chicken", item.getShortDescription());
  }

  @Test
  void testInvalidItemSetShortDescriptionWithNull() {
    Item item = new Item("Mountain Dew 12PK", new BigDecimal("5.99"));
    assertThrows(IllegalArgumentException.class, () -> {
      item.setShortDescription(null);
    });
  }

  @Test
  void testInvalidItemSetShortDescriptionWithBlank() {
    Item item = new Item("Mountain Dew 12PK", new BigDecimal("5.99"));
    assertThrows(IllegalArgumentException.class, () -> {
      item.setShortDescription("    ");
    });
  }

  @Test
  void testValidItemSetPrice() {
    Item item = new Item("Mountain Dew 12PK", new BigDecimal("5.99"));
    item.setPrice(new BigDecimal("2.49"));
    assertEquals(new BigDecimal("2.49"), item.getPrice());
  }

  @Test
  void testInvalidItemSetPriceWithNull() {
    Item item = new Item("Mountain Dew 12PK", new BigDecimal("5.99"));
    assertThrows(IllegalArgumentException.class, () -> {
      item.setPrice(null);
    });
  }

  @Test
  void testInvalidItemSetPriceWithNegative() {
    Item item = new Item("Mountain Dew 12PK", new BigDecimal("5.99"));
    assertThrows(IllegalArgumentException.class, () -> {
      item.setPrice(new BigDecimal("-5.99"));
    });
  }
}
