package com.praktica.praktica.service;

import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeService {

        @Value("${stripe.apiKey}")
        private String apiKey;

        public Session crearLinkPago(String descripcion, Double monto, String moneda, Long idOrden) throws Exception {
                Stripe.apiKey = apiKey;
                Long montoEnCentavos = Math.round(monto * 100);

                SessionCreateParams params = SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setSuccessUrl("http://praktica.s3-website.us-east-2.amazonaws.com:4200/chat")
                        .setCancelUrl("http://praktica.s3-website.us-east-2.amazonaws.com/chat")
                        .setClientReferenceId(String.valueOf(idOrden))
                        .addLineItem(
                                SessionCreateParams.LineItem.builder()
                                        .setQuantity(1L)
                                        .setPriceData(
                                                SessionCreateParams.LineItem.PriceData.builder()
                                                        .setCurrency(moneda.toLowerCase())
                                                        .setUnitAmount(montoEnCentavos)
                                                        .setProductData(
                                                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                        .setName(descripcion)
                                                                        .build()
                                                        )
                                                        .build()
                                        )
                                        .build()
                        )
                        .build();

                return Session.create(params);
        }
}
