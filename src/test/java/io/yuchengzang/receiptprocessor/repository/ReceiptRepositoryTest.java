package io.yuchengzang.receiptprocessor.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.math.BigDecimal;

import io.yuchengzang.receiptprocessor.model.Receipt;
import io.yuchengzang.receiptprocessor.model.Item;

public class ReceiptRepositoryTest {

  private ReceiptRepository receiptRepository;

  @BeforeEach
  void setUp() {
    receiptRepository = new ReceiptRepository();
  }

  @Test
  void testValidReceiptRepositorySave() {
    Item item = new Item("Mountain Dew 12PK", new BigDecimal("5.99"));

    Receipt receipt = new Receipt(
        "Target",
        LocalDate.of(2022, 1, 1),
        LocalTime.of(13, 1),
        List.of(item),
        new BigDecimal("5.99")
    );

    receiptRepository.save(receipt);
    assertEquals(receipt, receiptRepository.findById(receipt.getId()));
  }

  /**
   * Test saving a receipt with a duplicate ID. This is a valid scenario, and it might happen when
   * the receipt is updated. The repository should overwrite the existing receipt.
   */
  @Test
  void testValidReceiptRepositorySaveDuplicate() {
    // Add the receipt as normal
    Item item = new Item("Mountain Dew 12PK", new BigDecimal("5.99"));
    Receipt receipt = new Receipt(
        "Target",
        LocalDate.of(2022, 1, 1),
        LocalTime.of(13, 1),
        List.of(item),
        new BigDecimal("5.99")
    );
    receiptRepository.save(receipt);

    // Update the receipt, simulating a scenario where the receipt is updated
    receipt.setItems(List.of(new Item("Coca-Cola 12PK", new BigDecimal("5.99"))));
    receiptRepository.save(receipt);

    // Check if the ID is still the same
    assertEquals(receipt, receiptRepository.findById(receipt.getId()));

    // Check if the item is updated
    assertEquals(
        new Item("Coca-Cola 12PK", new BigDecimal("5.99")),
        receiptRepository.findById(receipt.getId()).getItems().get(0)
    );
  }

  @Test
  void testInvalidReceiptRepositorySaveWithNullReceipt() {
    assertThrows(IllegalArgumentException.class, () -> {
      receiptRepository.save(null);
    });
  }

  @Test
  void testValidReceiptRepositoryFindById() {
    Item item = new Item("Mountain Dew 12PK", new BigDecimal("5.99"));

    Receipt receipt = new Receipt(
        "Target",
        LocalDate.of(2022, 1, 1),
        LocalTime.of(13, 1),
        List.of(item),
        new BigDecimal("5.99")
    );

    receiptRepository.save(receipt);
    assertEquals(receipt, receiptRepository.findById(receipt.getId()));
  }

  @Test
  void testInvalidReceiptRepositoryFindByIdWithNullId() {
    assertThrows(IllegalArgumentException.class, () -> {
      receiptRepository.findById(null);
    });
  }

  @Test
  void testInvalidReceiptRepositoryFindByIdWithNonexistentId() {
    assertNull(receiptRepository.findById("nonexistent-id"));
  }

  @Test
  void testValidReceiptRepositoryFindAll() {
    Item item1 = new Item("Mountain Dew 12PK", new BigDecimal("5.99"));
    Item item2 = new Item("Coca-Cola 12PK", new BigDecimal("5.99"));

    Receipt receipt1 = new Receipt(
        "Target",
        LocalDate.of(2022, 1, 1),
        LocalTime.of(13, 1),
        List.of(item1),
        new BigDecimal("5.99")
    );

    Receipt receipt2 = new Receipt(
        "Walmart",
        LocalDate.of(2022, 1, 2),
        LocalTime.of(14, 2),
        List.of(item2),
        new BigDecimal("5.99")
    );

    receiptRepository.save(receipt1);
    receiptRepository.save(receipt2);

    // Convert the list to a set because the order of the elements in the list does not matter
    assertEquals(
        new HashSet<>(List.of(receipt1, receipt2)),
        new HashSet<>(receiptRepository.findAll().values())
    );
  }

  @Test
  void testValidReceiptRepositoryFindAllEmpty() {
    assertTrue(receiptRepository.findAll().isEmpty());
  }

  @Test
  void testValidReceiptRepositoryDeleteByIdOneItem() {
    Item item = new Item("Mountain Dew 12PK", new BigDecimal("5.99"));

    Receipt receipt = new Receipt(
        "Target",
        LocalDate.of(2022, 1, 1),
        LocalTime.of(13, 1),
        List.of(item),
        new BigDecimal("5.99")
    );

    receiptRepository.save(receipt);
    receiptRepository.deleteById(receipt.getId());
    assertNull(receiptRepository.findById(receipt.getId()));
  }

  @Test
  void testValidReceiptRepositoryDeleteByIdMultipleItems() {
    Item item1 = new Item("Mountain Dew 12PK", new BigDecimal("5.99"));
    Item item2 = new Item("Coca-Cola 12PK", new BigDecimal("5.99"));

    Receipt receipt1 = new Receipt(
        "Target",
        LocalDate.of(2022, 1, 1),
        LocalTime.of(13, 1),
        List.of(item1),
        new BigDecimal("5.99")
    );

    Receipt receipt2 = new Receipt(
        "Walmart",
        LocalDate.of(2022, 1, 2),
        LocalTime.of(14, 2),
        List.of(item2),
        new BigDecimal("5.99")
    );

    receiptRepository.save(receipt1);
    receiptRepository.save(receipt2);
    receiptRepository.deleteById(receipt1.getId());
    assertNull(receiptRepository.findById(receipt1.getId()));
    assertEquals(receipt2, receiptRepository.findById(receipt2.getId()));
  }

  @Test
  void testInvalidReceiptRepositoryDeleteByIdWithNonexistentId() {
    assertFalse(receiptRepository.deleteById("nonexistent-id"));
  }

  @Test
  void testInvalidReceiptRepositoryDeleteByIdWithNullId() {
    assertThrows(IllegalArgumentException.class, () -> {
      receiptRepository.deleteById(null);
    });
  }

  @Test
  void testValidReceiptRepositoryExistsByIdExisting() {
    Item item = new Item("Mountain Dew 12PK", new BigDecimal("5.99"));

    Receipt receipt = new Receipt(
        "Target",
        LocalDate.of(2022, 1, 1),
        LocalTime.of(13, 1),
        List.of(item),
        new BigDecimal("5.99")
    );

    receiptRepository.save(receipt);
    assertTrue(receiptRepository.existsById(receipt.getId()));
  }

  @Test
  void testValidReceiptRepositoryExistsByIdNonExisting() {
    assertFalse(receiptRepository.existsById("nonexistent-id"));
  }

  @Test
  void testInvalidReceiptRepositoryExistsByIdWithNullId() {
    assertThrows(IllegalArgumentException.class, () -> {
      receiptRepository.existsById(null);
    });
  }

  @Test
  void testValidReceiptRepositoryCountEmpty() {
    assertEquals(0, receiptRepository.count());
  }

  @Test
  void testValidReceiptRepositoryCountWithOneReceipt() {
    Item item = new Item("Mountain Dew 12PK", new BigDecimal("5.99"));

    Receipt receipt = new Receipt(
        "Target",
        LocalDate.of(2022, 1, 1),
        LocalTime.of(13, 1),
        List.of(item),
        new BigDecimal("5.99")
    );

    receiptRepository.save(receipt);
    assertEquals(1, receiptRepository.count());
  }

  @Test
  void testValidReceiptRepositoryCountWithMultipleReceipts() {
    Item item1 = new Item("Mountain Dew 12PK", new BigDecimal("5.99"));
    Item item2 = new Item("Coca-Cola 12PK", new BigDecimal("5.99"));

    Receipt receipt1 = new Receipt(
        "Target",
        LocalDate.of(2022, 1, 1),
        LocalTime.of(13, 1),
        List.of(item1),
        new BigDecimal("5.99")
    );

    Receipt receipt2 = new Receipt(
        "Walmart",
        LocalDate.of(2022, 1, 2),
        LocalTime.of(14, 2),
        List.of(item2),
        new BigDecimal("5.99")
    );

    receiptRepository.save(receipt1);
    receiptRepository.save(receipt2);
    assertEquals(2, receiptRepository.count());
  }
}
