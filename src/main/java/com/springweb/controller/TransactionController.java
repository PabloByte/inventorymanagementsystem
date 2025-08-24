package com.springweb.controller;

import com.springweb.entity.Transaction;
import com.springweb.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "*")
public class TransactionController {

    @Autowired
    private TransactionRepository transactionRepository;

    @GetMapping
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable Long id) {
        Optional<Transaction> transaction = transactionRepository.findById(id);
        return transaction.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Transaction createTransaction(@RequestBody Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Transaction> updateTransaction(@PathVariable Long id, @RequestBody Transaction transactionDetails) {
        Optional<Transaction> optionalTransaction = transactionRepository.findById(id);
        if (optionalTransaction.isPresent()) {
            Transaction transaction = optionalTransaction.get();
            transaction.setTransactionNumber(transactionDetails.getTransactionNumber());
            transaction.setOrder(transactionDetails.getOrder());
            transaction.setAmount(transactionDetails.getAmount());
            transaction.setType(transactionDetails.getType());
            transaction.setPaymentMethod(transactionDetails.getPaymentMethod());
            transaction.setStatus(transactionDetails.getStatus());
            transaction.setDescription(transactionDetails.getDescription());
            return ResponseEntity.ok(transactionRepository.save(transaction));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        if (transactionRepository.existsById(id)) {
            transactionRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/order/{orderId}")
    public List<Transaction> getTransactionsByOrder(@PathVariable Long orderId) {
        return transactionRepository.findByOrderId(orderId);
    }

    @GetMapping("/status/{status}")
    public List<Transaction> getTransactionsByStatus(@PathVariable Transaction.TransactionStatus status) {
        return transactionRepository.findByStatus(status);
    }
}
