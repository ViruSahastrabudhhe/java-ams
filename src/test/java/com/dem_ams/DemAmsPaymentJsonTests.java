package com.dem_ams;

import com.dem_ams.domain.dto.PaymentResponse;
import com.dem_ams.domain.model.Payment;
import com.dem_ams.domain.model.UserModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.assertj.core.util.Arrays;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class DemAmsPaymentJsonTests {
    @Autowired
    private JacksonTester<PaymentResponse> jsonPayment;

    @Autowired
    private JacksonTester<List<PaymentResponse>> jsonPaymentsList;

    private List<PaymentResponse> paymentResponsesList;

    @BeforeEach
    void setup() {
        UserModel user = UserModel.builder()
                .id(1L)
                .email("admin@example.com")
                .password("admin")
                .build();

        LocalDateTime defaultDate = LocalDateTime.of(2026, 9, 25, 0, 37, 27, 614000000);

        List<Payment> payments = List.of(
                createPayment(90L, 100.00, user, defaultDate),
                createPayment(91L, 5.00,  user, defaultDate),
                createPayment(92L, 250.00, user, defaultDate),
                createPayment(93L, 110.10, user, defaultDate)
        );

        paymentResponsesList = payments.stream()
                .map(p -> new PaymentResponse(p.getId(), p.getAmountPaid(), p.getUser().getId(), p.getDateCreated(), p.getDateUpdated(), p.getDateDeleted()))
                .toList();
    }

    private static Payment createPayment(Long id, Double amount, UserModel user, LocalDateTime date) {
        Payment payment = new Payment();
        payment.setId(id);
        payment.setAmountPaid(amount);
        payment.setUser(user);
        payment.setDateCreated(date);
        return payment;
    }

    @Test
    void paymentSerializationTest() throws IOException {
        PaymentResponse payment = paymentResponsesList.getFirst();

        assertThat(jsonPayment.write(payment)).isStrictlyEqualToJson("payments_expected.json");
        assertThat(jsonPayment.write(payment)).hasJsonPathNumberValue("@.id");
        assertThat(jsonPayment.write(payment)).extractingJsonPathNumberValue("@.id")
                .isEqualTo(90);
        assertThat(jsonPayment.write(payment)).hasJsonPathNumberValue("@.amountPaid");
        assertThat(jsonPayment.write(payment)).extractingJsonPathNumberValue("@.amountPaid")
                .isEqualTo(100.00);
        assertThat(jsonPayment.write(payment)).hasJsonPathNumberValue("@.user_id");
        assertThat(jsonPayment.write(payment)).extractingJsonPathNumberValue("@.user_id")
                .isEqualTo(1);
    }

    @Test
    void paymentDeserializationTest() throws IOException {
        String expected = """
                {
                    "id": 90,
                    "amountPaid": 100.00,
                    "user_id": 1,
                    "dateCreated": "2026-09-25T00:37:27.614"
                }
                """;

        PaymentResponse payment = paymentResponsesList.getFirst();

        assertThat(jsonPayment.parse(expected)).isEqualTo(payment);
        assertThat(jsonPayment.parseObject(expected).id()).isEqualTo(90L);
        assertThat(jsonPayment.parseObject(expected).amountPaid()).isEqualTo(100.00);
        assertThat(jsonPayment.parseObject(expected).user_id()).isEqualTo(1L);
    }

    @Test
    void paymentListSerializationTest() throws IOException {
        assertThat(jsonPaymentsList.write(paymentResponsesList)).isStrictlyEqualToJson("payments_list.json");
    }

    @Test
    void paymentListDeserializationTest() throws IOException {
        String list = """
                [
                    { "id": 90, "amountPaid": 100.00, "user_id": 1, "dateCreated": "2026-09-25T00:37:27.614" },
                    { "id": 91, "amountPaid": 5.00, "user_id": 1, "dateCreated": "2026-09-25T00:37:27.614"  },
                    { "id": 92, "amountPaid": 250.00, "user_id": 1, "dateCreated": "2026-09-25T00:37:27.614"  },
                    { "id": 93, "amountPaid": 110.10, "user_id": 1, "dateCreated": "2026-09-25T00:37:27.614"  }
                ]
                """;

        assertThat(jsonPaymentsList.parse(list)).isEqualTo(paymentResponsesList);
    }

}
