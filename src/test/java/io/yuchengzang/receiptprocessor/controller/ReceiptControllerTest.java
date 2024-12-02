package io.yuchengzang.receiptprocessor.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.hamcrest.Matchers.hasEntry;

import static org.mockito.Mockito.*;

import io.yuchengzang.receiptprocessor.repository.ReceiptRepository;
import io.yuchengzang.receiptprocessor.service.ReceiptPointsService;
import io.yuchengzang.receiptprocessor.model.Receipt;

@WebMvcTest(ReceiptController.class)
public class ReceiptControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private ReceiptRepository receiptRepository;

  @MockBean
  private ReceiptPointsService receiptPointsService;

  /**
   * Test to add a valid receipt to the repository.
   *
   * @throws Exception Being thrown by the mockMvc.perform() method, and will automatically be
   *                   caught by JUnit and reported as a test failure.
   */
  @Test
  void testAddReceiptValid_ShouldReturnCreatedStatusAndReceiptId() throws Exception {
    String receiptJson = """
            {
                "retailer": "Target",
                "purchaseDate": "2022-01-01",
                "purchaseTime": "13:01",
                "items": [
                    {"shortDescription": "Mountain Dew 12PK", "price": "6.49"}
                ],
                "total": "6.49"
            }
            """;

    mockMvc.perform(post("/receipts/process")
            .contentType(MediaType.APPLICATION_JSON)
            .content(receiptJson))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").isNotEmpty());
  }

  /**
   * Test to add an invalid receipt to the repository with 4 missing fields: retailer, purchaseDate,
   * purchaseTime, and total.
   *
   * @throws Exception Being thrown by the mockMvc.perform() method, and will automatically be
   *                   caught by JUnit and reported as a test failure.
   */
  @Test
  void testAddReceiptInvalid_ShouldReturnBadRequestForMissingReceiptFields() throws Exception {
    String invalidReceiptJson = """
            {
                "items": [
                    {"shortDescription": "Mountain Dew 12PK", "price": "6.49"}
                ]
            }
            """;

    mockMvc.perform(post("/receipts/process")
            .contentType(MediaType.APPLICATION_JSON)
            .content(invalidReceiptJson))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.retailer").value("Retailer name is required and must not be blank."))
        .andExpect(jsonPath("$.purchaseDate").value("Purchase date is required."))
        .andExpect(jsonPath("$.purchaseTime").value("Purchase time is required."))
        .andExpect(jsonPath("$.total").value("Total amount is required."));
  }

  /**
   * Test to add an invalid receipt to the repository with bad item format. The first item is
   * missing both shortDescription and price, the second item is missing shortDescription, and the
   * third item is missing price.
   *
   * @throws Exception Being thrown by the mockMvc.perform() method, and will automatically be
   *                   caught by JUnit and reported as a test failure.
   */
  @Test
  void testAddReceiptInvalid_ShouldReturnBadRequestForMissingReceiptItemFields() throws Exception {
    String invalidReceiptJson = """
            {
                "retailer": "Target",
                "purchaseDate": "2022-01-01",
                "purchaseTime": "13:01",
                "items": [
                    {},
                    {"price": "6.49"},
                    {"shortDescription": "Mountain Dew 12PK"}
                ],
                "total": "6.49"
            }
            """;

    mockMvc.perform(post("/receipts/process")
            .contentType(MediaType.APPLICATION_JSON)
            .content(invalidReceiptJson))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$", hasEntry("items[0].shortDescription", "Short description is required and must not be blank.")))
        .andExpect(jsonPath("$", hasEntry("items[0].price", "Price is required.")))
        .andExpect(jsonPath("$", hasEntry("items[1].shortDescription", "Short description is required and must not be blank.")))
        .andExpect(jsonPath("$", hasEntry("items[2].price", "Price is required.")));
  }

  @Test
  void testAddReceiptInvalid_ShouldReturnBadRequestForEmptyItemsList() throws Exception {
    String invalidReceiptJson = """
            {
                "retailer": "Target",
                "purchaseDate": "2022-01-01",
                "purchaseTime": "13:01",
                "items": [],
                "total": "6.49"
            }
            """;

    mockMvc.perform(post("/receipts/process")
            .contentType(MediaType.APPLICATION_JSON)
            .content(invalidReceiptJson))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.items").value("Items list must not be empty."));
  }

  @Test
  void testAddReceiptInvalid_ShouldReturnBadRequestForMalformedJson() throws Exception {
    String invalidJson = "{ invalidJson ";

    mockMvc.perform(post("/receipts/process")
            .contentType(MediaType.APPLICATION_JSON)
            .content(invalidJson))
        .andExpect(status().isBadRequest());
  }

  /**
   * Test to retrieve points for a valid receipt ID.
   *
   * @throws Exception Being thrown by the mockMvc.perform() method, and will automatically be
   *                   caught by JUnit and reported as a test failure.
   */
  @Test
  void testGetReceiptPointsValid_ShouldReturnPoints() throws Exception {
    String receiptId = "123e4567-e89b-12d3-a456-426614174000";
    Receipt mockReceipt = mock(Receipt.class);

    when(receiptRepository.findById(receiptId)).thenReturn(mockReceipt);
    when(receiptPointsService.calculatePoints(mockReceipt)).thenReturn(50);

    mockMvc.perform(get("/receipts/{id}/points", receiptId)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.points").value(50));

    verify(receiptRepository, times(1)).findById(receiptId);
    verify(receiptPointsService, times(1)).calculatePoints(mockReceipt);
  }

  /**
   * Test to retrieve points for an invalid UUID.
   *
   * @throws Exception Being thrown by the mockMvc.perform() method, and will automatically be
   *                   caught by JUnit and reported as a test failure.
   */
  @Test
  void testGetReceiptPointsInvalidUUID_ShouldReturnBadRequest() throws Exception {
    String invalidUUID = "invalid-uuid";

    mockMvc.perform(get("/receipts/{id}/points", invalidUUID)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.id").value("Invalid UUID"));

    verify(receiptRepository, times(0)).findById(any());
    verify(receiptPointsService, times(0)).calculatePoints(any());
  }

  /**
   * Test to retrieve points for a non-existent receipt ID.
   *
   * @throws Exception Being thrown by the mockMvc.perform() method, and will automatically be
   *                   caught by JUnit and reported as a test failure.
   */
  @Test
  void testGetReceiptPointsNonExistent_ShouldReturnNotFound() throws Exception {
    String receiptId = "123e4567-e89b-12d3-a456-426614174000";

    when(receiptRepository.findById(receiptId)).thenReturn(null);

    mockMvc.perform(get("/receipts/{id}/points", receiptId)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").value("Receipt not found"));

    verify(receiptRepository, times(1)).findById(receiptId);
    verify(receiptPointsService, times(0)).calculatePoints(any());
  }
}
