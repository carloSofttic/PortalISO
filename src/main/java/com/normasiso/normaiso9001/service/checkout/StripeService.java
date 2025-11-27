package com.normasiso.normaiso9001.service.checkout;

import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class StripeService {

    public record PlanInfo(String nombre, String moneda, BigDecimal precio, String descripcion) {}

    // Mapa INMUTABLE de planes
    private static final Map<String, PlanInfo> PLANS = Map.of(
            "MONTHLY",    new PlanInfo("Plan Mensual",   "MXN", new BigDecimal("499.00"),  "Acceso mensual"),
            "SEMIANNUAL", new PlanInfo("Plan Semestral", "MXN", new BigDecimal("2499.00"), "Acceso por 6 meses"),
            "ANNUAL",     new PlanInfo("Plan Anual",     "MXN", new BigDecimal("4499.00"), "Acceso anual")
    );

    @Value("${stripe.secret.key}")
    private String secretKey;

    @Value("${stripe.public.key}")
    private String publicKey;

    @PostConstruct
    void init() {
        Stripe.apiKey = secretKey;
    }

    /** Acceso de solo lectura a los planes */
    public Map<String, PlanInfo> plans() {
        return PLANS;
    }

    /** Devuelve la public key para el front */
    public String getPublicKey() {
        return publicKey;
    }

    /** Crea el PaymentIntent y devuelve el clientSecret */
    public String createPaymentIntent(String planKey, String email) throws Exception {
        var plan = PLANS.getOrDefault(planKey, PLANS.get("MONTHLY"));
        long amountCents = plan.precio().movePointRight(2).longValueExact();

        PaymentIntentCreateParams.Builder builder = PaymentIntentCreateParams.builder()
                .setAmount(amountCents)
                .setCurrency(plan.moneda().toLowerCase()) // "mxn"
                .setDescription(plan.descripcion())
                .putMetadata("plan", planKey);

        if (email != null && !email.isBlank()) {
            builder.putMetadata("email", email);
        }

        PaymentIntent intent = PaymentIntent.create(builder.build());
        return intent.getClientSecret();
    }
}
