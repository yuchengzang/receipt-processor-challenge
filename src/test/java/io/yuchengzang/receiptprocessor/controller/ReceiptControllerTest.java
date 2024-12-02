package io.yuchengzang.receiptprocessor.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.hamcrest.Matchers.hasEntry;

import io.yuchengzang.receiptprocessor.repository.ReceiptRepository;
import io.yuchengzang.receiptprocessor.service.ReceiptPointsService;

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

}
