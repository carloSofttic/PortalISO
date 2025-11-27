package com.normasiso.normaiso9001.controller.checkout;

import com.normasiso.normaiso9001.service.checkout.StripeService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
public class PaymentController {

    private final StripeService stripeService;

    public PaymentController(StripeService stripeService) {
        this.stripeService = stripeService;
    }

    // GET /checkout?plan=...
    @GetMapping("/checkout")
    public String showCheckout(@RequestParam String plan, Model model) {
        var p = stripeService.plans().getOrDefault(plan, stripeService.plans().get("MONTHLY"));
        model.addAttribute("planKey", plan);
        model.addAttribute("plan", p);
        model.addAttribute("stripePublicKey", stripeService.getPublicKey());
        return "checkout";
    }

    // POST /checkout/intent -> JSON { clientSecret: "..." }
    @PostMapping(
            value = "/checkout/intent",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createIntent(@RequestParam String plan,
                                                            @RequestParam(required = false) String email) {
        try {
            String clientSecret = stripeService.createPaymentIntent(plan, email);
            return ResponseEntity.ok(Map.of("clientSecret", clientSecret));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/checkout/success")
public String paymentSuccess() {
    return "checkout/success";
}
}
