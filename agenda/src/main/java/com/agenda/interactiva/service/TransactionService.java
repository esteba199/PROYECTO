package com.agenda.interactiva.service;

import com.agenda.interactiva.dto.TransactionDTO;
import com.agenda.interactiva.exception.ResourceNotFoundException;
import com.agenda.interactiva.model.Transaction;
import com.agenda.interactiva.model.User;
import com.agenda.interactiva.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public List<Transaction> getAllTransactions(User user) {
        return transactionRepository.findByUserOrderByDateDesc(user);
    }

    public Transaction getTransactionById(Long id, User user) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transacción no encontrada con ID: " + id));
        
        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("No tienes permisos para ver esta transacción.");
        }
        return transaction;
    }

    @Transactional
    public Transaction createTransaction(TransactionDTO dto, User user) {
        Transaction transaction = Transaction.builder()
                .user(user)
                .type(dto.getType())
                .amount(dto.getAmount())
                .category(dto.getCategory())
                .description(dto.getDescription())
                .date(dto.getDate())
                .build();
        return transactionRepository.save(transaction);
    }

    @Transactional
    public Transaction updateTransaction(Long id, TransactionDTO dto, User user) {
        Transaction transaction = getTransactionById(id, user);
        transaction.setType(dto.getType());
        transaction.setAmount(dto.getAmount());
        transaction.setCategory(dto.getCategory());
        transaction.setDescription(dto.getDescription());
        transaction.setDate(dto.getDate());
        return transactionRepository.save(transaction);
    }

    @Transactional
    public void deleteTransaction(Long id, User user) {
        Transaction transaction = getTransactionById(id, user);
        transactionRepository.delete(transaction); // Realiza un Soft Delete gracias a @SQLDelete
    }
}
