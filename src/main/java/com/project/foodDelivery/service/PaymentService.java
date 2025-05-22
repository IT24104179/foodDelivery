package com.project.foodDelivery.service;

import com.project.foodDelivery.model.Payment;
import com.project.foodDelivery.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;

    public void createPayment(Payment payment) {
        payment.setPaymentId(UUID.randomUUID().toString().substring(0,8));
        payment.setPaymentDate(java.time.LocalDate.now().toString());
        payment.setPaymentStatus("COMPLETED");

        // Basic card validation
        validateCard(payment);

        paymentRepository.save(payment);
    }

    private void validateCard(Payment payment) {
        // Remove all spaces from card number before validation
        String rawCardNumber = payment.getCardNumber() != null
                ? payment.getCardNumber().replaceAll("\\s+", "")
                : null;

        if (rawCardNumber == null || !rawCardNumber.matches("\\d{16}")) {
            throw new IllegalArgumentException("Please enter exactly 16-digit card number");
        }

        if (payment.getCardCvc() == null || !payment.getCardCvc().matches("\\d{3}")) {
            throw new IllegalArgumentException("Please enter exactly 3-digit CVC");
        }

        if (payment.getCardExpiry() == null || !payment.getCardExpiry().matches("(0[1-9]|1[0-2])/\\d{2}")) {
            throw new IllegalArgumentException("Please enter valid expiry date (MM/YY)");
        }

        // Update the payment object with the cleaned card number
        payment.setCardNumber(rawCardNumber);
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Optional<Payment> getPaymentById(String paymentId) {
        return paymentRepository.findById(paymentId);
    }
    
    public List<Payment> getPaymentsByOrderId(String orderId) {
        return paymentRepository.findByOrderId(orderId);
    }
    
    public boolean hasPayment(String orderId) {
        List<Payment> payments = getPaymentsByOrderId(orderId);
        return !payments.isEmpty();
    }
}
