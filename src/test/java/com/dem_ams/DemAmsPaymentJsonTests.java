package com.dem_ams;

import com.dem_ams.domain.dto.PaymentFormDTO;
import com.dem_ams.domain.model.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.assertj.core.util.Arrays;

import java.io.IOException;

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
        payments = Arrays.array(
                new Payment(90L, 100.00, 1L),
                new Payment(91L, 5.00, 1L),
                new Payment(92L, 250.00, 1L),
                new Payment(93L, 110.10, 1L));
    }

    @Test
    void paymentSerializationTest() throws IOException {
        Payment payment = payments[0];

        assertThat(jsonPayment.write(payment)).isStrictlyEqualToJson("expected.json");
        assertThat(jsonPayment.write(payment)).hasJsonPathNumberValue("@.id");
        assertThat(jsonPayment.write(payment)).extractingJsonPathNumberValue("@.id")
                .isEqualTo(90);
        assertThat(jsonPayment.write(payment)).hasJsonPathNumberValue("@.amount");
        assertThat(jsonPayment.write(payment)).extractingJsonPathNumberValue("@.amount")
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
                    "amount": 100.00,
                    "user_id": 1
                }
                """;

        Payment payment = payments[0];

        assertThat(jsonPayment.parse(expected)).isEqualTo(payment);
        assertThat(jsonPayment.parseObject(expected).getId()).isEqualTo(90L);
        assertThat(jsonPayment.parseObject(expected).getAmount()).isEqualTo(100.00);
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
                    { "id": 90, "amount": 100.00, "user_id": 1 },
                    { "id": 91, "amount": 5.00, "user_id": 1  },
                    { "id": 92, "amount": 250.00, "user_id": 1  },
                    { "id": 93, "amount": 110.10, "user_id": 1  }
                ]
                """;

        assertThat(jsonPaymentsList.parse(list)).isEqualTo(payments);
    }

}
