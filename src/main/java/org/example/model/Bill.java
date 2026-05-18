package org.example.model;

import java.time.LocalDate;

/**
 * Factura imutabila generata pentru fiecare operatie pe o comanda, stocata in tabela log.
 *
 * @author Bianca Herghelegiu
 */
    public record Bill(
            int orderId,
            int clientId,
            int productId,
            String clientName,
            String productName,
            double price,
            double totalPrice,
            int quantity,
            int bill_id,
            LocalDate time

    ) {


    }

