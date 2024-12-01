package io.yuchengzang.receiptprocessor.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public class ReceiptTest {

  @Test
  void testValidReceiptConstructor() {
    List<Item> items = List.of(
      new Item("Mountain Dew 12PK", new BigDecimal("5.99")),
      new Item("Knorr Creamy Chicken", new BigDecimal("2.49"))
    );

    Receipt receipt = new Receipt(
      "Walmart",
      LocalDate.of(2022, 1, 1),
      LocalTime.of(13, 1),
      items,
      new BigDecimal("8.48")
    );

    assertNotNull(receipt.getId());
    assertEquals("Walmart", receipt.getRetailer());
    assertEquals(LocalDate.of(2022, 1, 1), receipt.getPurchaseDate());
    assertEquals(LocalTime.of(13, 1), receipt.getPurchaseTime());
    assertEquals(items, receipt.getItems());
    assertEquals(new BigDecimal("8.48"), receipt.getTotal());
  }

  @Test
  void testValidReceiptConstructorWithShortRetailer() {
    List<Item> items = List.of(
      new Item("Mountain Dew 12PK", new BigDecimal("5.99")),
      new Item("Knorr Creamy Chicken", new BigDecimal("2.49"))
    );

    Receipt receipt = new Receipt(
      "A",
      LocalDate.of(2022, 1, 1),
      LocalTime.of(13, 1),
      items,
      new BigDecimal("8.48")
    );

    assertNotNull(receipt.getId());
    assertEquals("A", receipt.getRetailer());
    assertEquals(LocalDate.of(2022, 1, 1), receipt.getPurchaseDate());
    assertEquals(LocalTime.of(13, 1), receipt.getPurchaseTime());
    assertEquals(items, receipt.getItems());
    assertEquals(new BigDecimal("8.48"), receipt.getTotal());
  }

  @Test
  void testValidReceiptConstructorWithOneItem() {
    List<Item> items = List.of(
      new Item("Mountain Dew 12PK", new BigDecimal("5.99"))
    );

    Receipt receipt = new Receipt(
      "Walmart",
      LocalDate.of(2022, 1, 1),
      LocalTime.of(13, 1),
      items,
      new BigDecimal("5.99")
    );

    assertNotNull(receipt.getId());
    assertEquals("Walmart", receipt.getRetailer());
    assertEquals(LocalDate.of(2022, 1, 1), receipt.getPurchaseDate());
    assertEquals(LocalTime.of(13, 1), receipt.getPurchaseTime());
    assertEquals(items, receipt.getItems());
    assertEquals(new BigDecimal("5.99"), receipt.getTotal());
  }

  @Test
  void testValidReceiptConstructorWithZeroTotalAmount() {
    List<Item> items = List.of(
      new Item("Mountain Dew 12PK", new BigDecimal("0.00")),
      new Item("Knorr Creamy Chicken", new BigDecimal("0.00"))
    );

    Receipt receipt = new Receipt(
      "Walmart",
      LocalDate.of(2022, 1, 1),
      LocalTime.of(13, 1),
      items,
      new BigDecimal("0.00")
    );

    assertNotNull(receipt.getId());
    assertEquals("Walmart", receipt.getRetailer());
    assertEquals(LocalDate.of(2022, 1, 1), receipt.getPurchaseDate());
    assertEquals(LocalTime.of(13, 1), receipt.getPurchaseTime());
    assertEquals(items, receipt.getItems());
    assertEquals(new BigDecimal("0.00"), receipt.getTotal());
  }

  @Test
  void testInvalidReceiptConstructorWithNullRetailer() {
    List<Item> items = List.of(
      new Item("Mountain Dew 12PK", new BigDecimal("5.99")),
      new Item("Knorr Creamy Chicken", new BigDecimal("2.49"))
    );

    assertThrows(IllegalArgumentException.class, () -> {
      new Receipt(
        null,
        LocalDate.of(2022, 1, 1),
        LocalTime.of(13, 1),
        items,
        new BigDecimal("8.48")
      );
    });
  }

  @Test
  void testInvalidReceiptConstructorWithBlankRetailer() {
    List<Item> items = List.of(
      new Item("Mountain Dew 12PK", new BigDecimal("5.99")),
      new Item("Knorr Creamy Chicken", new BigDecimal("2.49"))
    );

    assertThrows(IllegalArgumentException.class, () -> {
      new Receipt(
        "    ",
        LocalDate.of(2022, 1, 1),
        LocalTime.of(13, 1),
        items,
        new BigDecimal("8.48")
      );
    });
  }

  @Test
  void testInvalidReceiptConstructorWithNullPurchaseDate() {
    List<Item> items = List.of(
      new Item("Mountain Dew 12PK", new BigDecimal("5.99")),
      new Item("Knorr Creamy Chicken", new BigDecimal("2.49"))
    );

    assertThrows(IllegalArgumentException.class, () -> {
      new Receipt(
        "Walmart",
        null,
        LocalTime.of(13, 1),
        items,
        new BigDecimal("8.48")
      );
    });
  }

  @Test
  void testInvalidReceiptConstructorWithNullPurchaseTime() {
    List<Item> items = List.of(
      new Item("Mountain Dew 12PK", new BigDecimal("5.99")),
      new Item("Knorr Creamy Chicken", new BigDecimal("2.49"))
    );

    assertThrows(IllegalArgumentException.class, () -> {
      new Receipt(
        "Walmart",
        LocalDate.of(2022, 1, 1),
        null,
        items,
        new BigDecimal("8.48")
      );
    });
  }

  @Test
  void testInvalidReceiptConstructorWithNullItems() {
    assertThrows(IllegalArgumentException.class, () -> {
      new Receipt(
        "Walmart",
        LocalDate.of(2022, 1, 1),
        LocalTime.of(13, 1),
        null,
        new BigDecimal("8.48")
      );
    });
  }

  @Test
  void testInvalidReceiptConstructorWithEmptyItems() {
    assertThrows(IllegalArgumentException.class, () -> {
      new Receipt(
        "Walmart",
        LocalDate.of(2022, 1, 1),
        LocalTime.of(13, 1),
        List.of(),
        new BigDecimal("8.48")
      );
    });
  }

  @Test
  void testInvalidReceiptConstructorWithNullTotalAmount() {
    List<Item> items = List.of(
      new Item("Mountain Dew 12PK", new BigDecimal("5.99")),
      new Item("Knorr Creamy Chicken", new BigDecimal("2.49"))
    );

    assertThrows(IllegalArgumentException.class, () -> {
      new Receipt(
        "Walmart",
        LocalDate.of(2022, 1, 1),
        LocalTime.of(13, 1),
        items,
        null
      );
    });
  }

  @Test
  void testInvalidReceiptConstructorWithNegativeTotalAmount() {
    List<Item> items = List.of(
      new Item("Mountain Dew 12PK", new BigDecimal("5.99")),
      new Item("Knorr Creamy Chicken", new BigDecimal("2.49"))
    );

    assertThrows(IllegalArgumentException.class, () -> {
      new Receipt(
        "Walmart",
        LocalDate.of(2022, 1, 1),
        LocalTime.of(13, 1),
        items,
        new BigDecimal("-8.48")
      );
    });
  }

  @Test
  void testValidReceiptSetRetailer() {
    Receipt receipt = new Receipt(
      "Walmart",
      LocalDate.of(2022, 1, 1),
      LocalTime.of(13, 1),
      List.of(
        new Item("Mountain Dew 12PK", new BigDecimal("5.99")),
        new Item("Knorr Creamy Chicken", new BigDecimal("2.49"))
      ),
      new BigDecimal("8.48")
    );

    receipt.setRetailer("Target");
    assertEquals("Target", receipt.getRetailer());
  }

  @Test
  void testInvalidReceiptSetRetailerWithNull() {
    Receipt receipt = new Receipt(
      "Walmart",
      LocalDate.of(2022, 1, 1),
      LocalTime.of(13, 1),
      List.of(
        new Item("Mountain Dew 12PK", new BigDecimal("5.99")),
        new Item("Knorr Creamy Chicken", new BigDecimal("2.49"))
      ),
      new BigDecimal("8.48")
    );

    assertThrows(IllegalArgumentException.class, () -> {
      receipt.setRetailer(null);
    });
  }

  @Test
  void testInvalidReceiptSetRetailerWithBlank() {
    Receipt receipt = new Receipt(
      "Walmart",
      LocalDate.of(2022, 1, 1),
      LocalTime.of(13, 1),
      List.of(
        new Item("Mountain Dew 12PK", new BigDecimal("5.99")),
        new Item("Knorr Creamy Chicken", new BigDecimal("2.49"))
      ),
      new BigDecimal("8.48")
    );

    assertThrows(IllegalArgumentException.class, () -> {
      receipt.setRetailer("    ");
    });
  }

  @Test
  void testValidReceiptSetPurchaseDate() {
    Receipt receipt = new Receipt(
      "Walmart",
      LocalDate.of(2022, 1, 1),
      LocalTime.of(13, 1),
      List.of(
        new Item("Mountain Dew 12PK", new BigDecimal("5.99")),
        new Item("Knorr Creamy Chicken", new BigDecimal("2.49"))
      ),
      new BigDecimal("8.48")
    );

    receipt.setPurchaseDate(LocalDate.of(2022, 2, 2));
    assertEquals(LocalDate.of(2022, 2, 2), receipt.getPurchaseDate());
  }

  @Test
  void testValidReceiptSetPurchaseDateWithParse() {
    Receipt receipt = new Receipt(
      "Walmart",
      LocalDate.parse("2022-01-01"),
      LocalTime.of(13, 1),
      List.of(
        new Item("Mountain Dew 12PK", new BigDecimal("5.99")),
        new Item("Knorr Creamy Chicken", new BigDecimal("2.49"))
      ),
      new BigDecimal("8.48")
    );

    receipt.setPurchaseDate(LocalDate.parse("2022-02-02"));
    assertEquals(LocalDate.of(2022, 2, 2), receipt.getPurchaseDate());
  }

  @Test
  void testInvalidReceiptSetPurchaseDateWithNull() {
    Receipt receipt = new Receipt(
      "Walmart",
      LocalDate.of(2022, 1, 1),
      LocalTime.of(13, 1),
      List.of(
        new Item("Mountain Dew 12PK", new BigDecimal("5.99")),
        new Item("Knorr Creamy Chicken", new BigDecimal("2.49"))
      ),
      new BigDecimal("8.48")
    );

    assertThrows(IllegalArgumentException.class, () -> {
      receipt.setPurchaseDate(null);
    });
  }

  @Test
  void testValidReceiptSetPurchaseTime() {
    Receipt receipt = new Receipt(
      "Walmart",
      LocalDate.of(2022, 1, 1),
      LocalTime.of(13, 1),
      List.of(
        new Item("Mountain Dew 12PK", new BigDecimal("5.99")),
        new Item("Knorr Creamy Chicken", new BigDecimal("2.49"))
      ),
      new BigDecimal("8.48")
    );

    receipt.setPurchaseTime(LocalTime.of(14, 2));
    assertEquals(LocalTime.of(14, 2), receipt.getPurchaseTime());
  }

  @Test
  void testValidReceiptSetPurchaseTimeWithParse() {
    Receipt receipt = new Receipt(
      "Walmart",
      LocalDate.of(2022, 1, 1),
      LocalTime.parse("13:01"),
      List.of(
        new Item("Mountain Dew 12PK", new BigDecimal("5.99")),
        new Item("Knorr Creamy Chicken", new BigDecimal("2.49"))
      ),
      new BigDecimal("8.48")
    );

    receipt.setPurchaseTime(LocalTime.parse("14:02"));
    assertEquals(LocalTime.of(14, 2), receipt.getPurchaseTime());
  }

  @Test
  void testInvalidReceiptSetPurchaseTimeWithNull() {
    Receipt receipt = new Receipt(
      "Walmart",
      LocalDate.of(2022, 1, 1),
      LocalTime.of(13, 1),
      List.of(
        new Item("Mountain Dew 12PK", new BigDecimal("5.99")),
        new Item("Knorr Creamy Chicken", new BigDecimal("2.49"))
      ),
      new BigDecimal("8.48")
    );

    assertThrows(IllegalArgumentException.class, () -> {
      receipt.setPurchaseTime(null);
    });
  }

  @Test
  void testValidReceiptSetItems() {
    List<Item> items = List.of(
      new Item("Mountain Dew 12PK", new BigDecimal("5.99")),
      new Item("Knorr Creamy Chicken", new BigDecimal("2.49"))
    );

    Receipt receipt = new Receipt(
      "Walmart",
      LocalDate.of(2022, 1, 1),
      LocalTime.of(13, 1),
      items,
      new BigDecimal("8.48")
    );

    List<Item> newItems = List.of(
      new Item("Mountain Dew 12PK", new BigDecimal("5.99")),
      new Item("Knorr Creamy Chicken", new BigDecimal("2.49")),
      new Item("Coca Cola 12PK", new BigDecimal("5.99"))
    );

    receipt.setItems(newItems);
    assertEquals(newItems, receipt.getItems());
  }

  @Test
  void testInvalidReceiptSetItemsWithNull() {
    List<Item> items = List.of(
      new Item("Mountain Dew 12PK", new BigDecimal("5.99")),
      new Item("Knorr Creamy Chicken", new BigDecimal("2.49"))
    );

    Receipt receipt = new Receipt(
      "Walmart",
      LocalDate.of(2022, 1, 1),
      LocalTime.of(13, 1),
      items,
      new BigDecimal("8.48")
    );

    assertThrows(IllegalArgumentException.class, () -> {
      receipt.setItems(null);
    });
  }

  @Test
  void testInvalidReceiptSetItemsWithEmpty() {
    List<Item> items = List.of(
      new Item("Mountain Dew 12PK", new BigDecimal("5.99")),
      new Item("Knorr Creamy Chicken", new BigDecimal("2.49"))
    );

    Receipt receipt = new Receipt(
      "Walmart",
      LocalDate.of(2022, 1, 1),
      LocalTime.of(13, 1),
      items,
      new BigDecimal("8.48")
    );

    assertThrows(IllegalArgumentException.class, () -> {
      receipt.setItems(List.of());
    });
  }

  @Test
  void testValidReceiptSetTotalAmount() {
    Receipt receipt = new Receipt(
      "Walmart",
      LocalDate.of(2022, 1, 1),
      LocalTime.of(13, 1),
      List.of(
        new Item("Mountain Dew 12PK", new BigDecimal("5.99")),
        new Item("Knorr Creamy Chicken", new BigDecimal("2.49"))
      ),
      new BigDecimal("8.48")
    );

    receipt.setTotal(new BigDecimal("10.00"));
    assertEquals(new BigDecimal("10.00"), receipt.getTotal());
  }

  @Test
  void testValidReceiptSetTotalAmountWithZero() {
    Receipt receipt = new Receipt(
        "Walmart",
        LocalDate.of(2022, 1, 1),
        LocalTime.of(13, 1),
        List.of(
            new Item("Mountain Dew 12PK", new BigDecimal("0.00")),
            new Item("Knorr Creamy Chicken", new BigDecimal("0.00"))
        ),
        new BigDecimal("0.00")
    );

    receipt.setTotal(new BigDecimal("0.00"));
    assertEquals(new BigDecimal("0.00"), receipt.getTotal());
  }

  @Test
  void testInvalidReceiptSetTotalAmountWithNull() {
    Receipt receipt = new Receipt(
      "Walmart",
      LocalDate.of(2022, 1, 1),
      LocalTime.of(13, 1),
      List.of(
        new Item("Mountain Dew 12PK", new BigDecimal("5.99")),
        new Item("Knorr Creamy Chicken", new BigDecimal("2.49"))
      ),
      new BigDecimal("8.48")
    );

    assertThrows(IllegalArgumentException.class, () -> {
      receipt.setTotal(null);
    });
  }

  @Test
  void testInvalidReceiptSetTotalAmountWithNegative() {
    Receipt receipt = new Receipt(
      "Walmart",
      LocalDate.of(2022, 1, 1),
      LocalTime.of(13, 1),
      List.of(
        new Item("Mountain Dew 12PK", new BigDecimal("5.99")),
        new Item("Knorr Creamy Chicken", new BigDecimal("2.49"))
      ),
      new BigDecimal("8.48")
    );

    assertThrows(IllegalArgumentException.class, () -> {
      receipt.setTotal(new BigDecimal("-8.48"));
    });
  }
}
