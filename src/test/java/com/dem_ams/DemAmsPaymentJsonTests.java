package com.dem_ams;

import com.dem_ams.domain.model.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.assertj.core.util.Arrays;

import java.io.IOException;
import java.sql.Timestamp;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class DemAmsPaymentJsonTests {
    @Autowired
    private JacksonTester<Payment> jsonPayment;

    @Autowired
    private JacksonTester<Payment[]> jsonPaymentsList;

    private Payment[] payments;

    @BeforeEach
    void setup() {
        Payment payment1 = new Payment();
        payment1.setId(90L);
        payment1.setAmountPaid(100.00);
        payment1.setUser_id(1L);
        payment1.setDateCreated(Timestamp.valueOf("2026-09-25 00:37:27.614"));

        Payment payment2 = new Payment();
        payment2.setId(91L);
        payment2.setAmountPaid(5.00);
        payment2.setUser_id(1L);
        payment2.setDateCreated(Timestamp.valueOf("2026-09-25 00:37:27.614"));

        Payment payment3 = new Payment();
        payment3.setId(92L);
        payment3.setAmountPaid(250.00);
        payment3.setUser_id(1L);
        payment3.setDateCreated(Timestamp.valueOf("2026-09-25 00:37:27.614"));

        Payment payment4 = new Payment();
        payment4.setId(93L);
        payment4.setAmountPaid(110.10);
        payment4.setUser_id(1L);
        payment4.setDateCreated(Timestamp.valueOf("2026-09-25 00:37:27.614"));

        payments = Arrays.array(
                payment1, payment2, payment3, payment4);
    }

    @Test
    void paymentSerializationTest() throws IOException {
        Payment payment = payments[0];

        assertThat(jsonPayment.write(payment)).isStrictlyEqualToJson("expected.json");
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
                    "dateCreated": "2026-09-24T16:37:27.614Z"
                }
                """;

        Payment payment = payments[0];

        assertThat(jsonPayment.parse(expected)).isEqualTo(payment);
        assertThat(jsonPayment.parseObject(expected).getId()).isEqualTo(90L);
        assertThat(jsonPayment.parseObject(expected).getAmountPaid()).isEqualTo(100.00);
        assertThat(jsonPayment.parseObject(expected).getUser_id()).isEqualTo(1L);
    }

    @Test
    void paymentListSerializationTest() throws IOException {
        assertThat(jsonPaymentsList.write(payments)).isStrictlyEqualToJson("list.json");
    }

    @Test
    void paymentListDeserializationTest() throws IOException {
        String list = """
                [
                    { "id": 90, "amountPaid": 100.00, "user_id": 1, "dateCreated": "2026-09-24T16:37:27.614Z" },
                    { "id": 91, "amountPaid": 5.00, "user_id": 1, "dateCreated": "2026-09-24T16:37:27.614Z"  },
                    { "id": 92, "amountPaid": 250.00, "user_id": 1, "dateCreated": "2026-09-24T16:37:27.614Z"  },
                    { "id": 93, "amountPaid": 110.10, "user_id": 1, "dateCreated": "2026-09-24T16:37:27.614Z"  }
                ]
                """;

        assertThat(jsonPaymentsList.parse(list)).isEqualTo(payments);
    }

}
