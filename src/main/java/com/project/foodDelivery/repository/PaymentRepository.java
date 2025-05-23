package com.project.foodDelivery.repository;

import com.project.foodDelivery.model.Payment;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.*;

@Repository
public class PaymentRepository {
    private final String filePath = "data/payments.txt";

    public PaymentRepository() {
        ensureFileExists();
    }

    private void ensureFileExists() {
        try {
            File file = new File(filePath);
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save(Payment payment) {
        ensureFileExists();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(format(payment));
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Payment> findAll() {
        ensureFileExists();
        List<Payment> payments = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                payments.add(parse(line));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return payments;
    }

    public Optional<Payment> findById(String paymentId) {
        ensureFileExists();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Payment payment = parse(line);
                if (payment.getPaymentId().equals(paymentId)) {
                    return Optional.of(payment);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
    
    public List<Payment> findByOrderId(String orderId) {
        ensureFileExists();
        List<Payment> payments = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Payment payment = parse(line);
                if (payment.getOrderId().equals(orderId)) {
                    payments.add(payment);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return payments;
    }

    private String format(Payment p) {
        return String.join("::",
                p.getPaymentId(), p.getOrderId(), p.getCustomerEmail(),
                String.valueOf(p.getAmount()), p.getPaymentMethod(), p.getPaymentDate(),
                p.getCardNumber(), p.getCardCvc(), p.getCardType(),
                p.getCardExpiry(), p.getCardHolderName(), p.getPaymentStatus());
    }

    private Payment parse(String line) {
        String[] parts = line.split("::");
        Payment p = new Payment();
        p.setPaymentId(parts[0]);
        p.setOrderId(parts[1]);
        p.setCustomerEmail(parts[2]);
        p.setAmount(Double.parseDouble(parts[3]));
        p.setPaymentMethod(parts[4]);
        p.setPaymentDate(parts[5]);
        p.setCardNumber(parts[6]);
        p.setCardCvc(parts[7]);
        p.setCardType(parts[8]);
        p.setCardExpiry(parts[9]);
        p.setCardHolderName(parts[10]);
        p.setPaymentStatus(parts.length > 11 ? parts[11] : "COMPLETED");
        return p;
    }
}
