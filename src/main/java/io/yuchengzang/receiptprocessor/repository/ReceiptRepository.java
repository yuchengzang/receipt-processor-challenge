package io.yuchengzang.receiptprocessor.repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Repository;

import io.yuchengzang.receiptprocessor.model.Receipt;

/**
 * The ReceiptRepository class is a repository for storing receipts.
 */
@Repository
public class ReceiptRepository {

  // Create a logger for the ReceiptRepository class
  private static final Logger logger = LoggerFactory.getLogger(ReceiptRepository.class);

  // Create a map to store the receipts
  private final Map<String, Receipt> receiptStore = new ConcurrentHashMap<>();

  /**
   * Save a receipt to the repository
   *
   * @param receipt the Receipt object to be saved. It must not be null.
   * @throws IllegalArgumentException if the receipt is null
   */
  public void save(Receipt receipt) throws IllegalArgumentException {
    // Validate the input
    if (receipt == null) {
      throw new IllegalArgumentException("Receipt cannot be null.");
    }

    // Save the receipt to the repository
    receiptStore.put(receipt.getId(), receipt);
    logger.info("Receipt with ID '{}' saved successfully.", receipt.getId());
  }

  /**
   * Get a receipt from the repository by ID
   *
   * @param id the ID of the receipt. It must not be null.
   * @return the Receipt object with the given ID, or null if not found
   * @throws IllegalArgumentException if the ID is null
   */
  public Receipt findById(String id) throws IllegalArgumentException {
    // Validate the input
    if (id == null) {
      logger.error("Receipt ID cannot be null.");
      throw new IllegalArgumentException("Receipt ID cannot be null.");
    }

    // Search for the receipt in the repository
    Receipt receipt = receiptStore.get(id);

    // Log the result of the search
    if (receipt == null) {
      logger.error("Receipt with ID '{}' not found.", id);
    } else {
      logger.info("Receipt with ID '{}' found.", id);
    }

    return receipt;
  }

  /**
   * Get all receipts from the repository
   *
   * @return a map of all receipts in the repository
   */
  public Map<String, Receipt> findAll() {
    logger.info("Retrieving all receipts. Total count: {}", receiptStore.size());

    // Return a new map to prevent modification of the original map
    return new ConcurrentHashMap<>(receiptStore);
  }

  /**
   * Delete a receipt from the repository by ID
   *
   * @param id the ID of the receipt to be deleted. It must not be null.
   * @return true if the receipt is deleted successfully, false otherwise
   * @throws IllegalArgumentException if the ID is null
   */
  public boolean deleteById(String id) throws IllegalArgumentException {
    // Validate the input
    if (id == null) {
      logger.error("Receipt ID cannot be null.");
      throw new IllegalArgumentException("Receipt ID cannot be null.");
    }

    // Remove the receipt from the repository
    Receipt receipt = receiptStore.remove(id);

    // Log the result of the deletion
    if (receipt == null) {
      logger.error("Receipt with ID '{}' not found.", id);
      return false;
    } else {
      logger.info("Receipt with ID '{}' deleted successfully.", id);
      return true;
    }
  }

  /**
   * Check if a receipt exists in the repository by ID
   *
   * @param id the ID of the receipt. It must not be null.
   * @return true if the receipt exists, false otherwise
   * @throws IllegalArgumentException if the ID is null
   */
  public boolean existsById(String id) throws IllegalArgumentException {
    if (id == null) {
      logger.error("Receipt ID cannot be null.");
      throw new IllegalArgumentException("Receipt ID cannot be null.");
    }

    // Perform the check
    return receiptStore.containsKey(id);
  }

  /**
   * Get the total number of receipts in the repository
   *
   * @return the number of receipts in the repository
   */
  public int count() {
    return receiptStore.size();
  }
}
