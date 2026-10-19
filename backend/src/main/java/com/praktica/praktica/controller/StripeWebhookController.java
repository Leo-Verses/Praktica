package com.praktica.praktica.controller;

import com.praktica.praktica.service.PagoService;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stripe")
public class StripeWebhookController {

    @Value("${stripe.webhookSecret}")
    private String webhookSecret;

    private final PagoService pagoService;

    public StripeWebhookController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody String payload,
                                                @RequestHeader("Stripe-Signature") String sigHeader) {
        try {
            Event event = Webhook.constructEvent(payload, sigHeader, webhookSecret);

            if ("checkout.session.completed".equals(event.getType())) {
                event.getDataObjectDeserializer().getObject().ifPresent(obj -> {
                    Session session = (Session) obj;
                    pagoService.registrarPago(session);
                });
            }

            return ResponseEntity.ok("Evento recibido");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error webhook: " + e.getMessage());
        }
    }
}
