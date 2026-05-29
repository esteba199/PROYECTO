package com.agenda.interactiva.controller;

import com.agenda.interactiva.dto.TransactionDTO;
import com.agenda.interactiva.model.Transaction;
import com.agenda.interactiva.model.User;
import com.agenda.interactiva.service.TransactionService;
import com.agenda.interactiva.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestionar Transacciones Financieras.
 */
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Tag(name = "Finanzas", description = "Endpoints para la gestión de ingresos y gastos.")
public class TransactionController {

    private final TransactionService transactionService;
    private final UserService userService;

    @GetMapping
    @Operation(summary = "Obtener todas las transacciones", description = "Devuelve el historial de ingresos y gastos del usuario.")
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        User user = userService.getAuthenticatedUser();
        List<Transaction> transactions = transactionService.getAllTransactions(user);
        return ResponseEntity.ok(transactions);
    }

    @PostMapping
    @Operation(summary = "Crear nueva transacción", description = "Registra un ingreso o gasto.")
    public ResponseEntity<Transaction> createTransaction(@Valid @RequestBody TransactionDTO dto) {
        User user = userService.getAuthenticatedUser();
        Transaction createdTx = transactionService.createTransaction(dto, user);
        return new ResponseEntity<>(createdTx, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar transacción", description = "Edita datos de un ingreso o gasto existente.")
    public ResponseEntity<Transaction> updateTransaction(@PathVariable Long id, @Valid @RequestBody TransactionDTO dto) {
        User user = userService.getAuthenticatedUser();
        Transaction updatedTx = transactionService.updateTransaction(id, dto, user);
        return ResponseEntity.ok(updatedTx);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar transacción", description = "Realiza el borrado lógico de un movimiento.")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        User user = userService.getAuthenticatedUser();
        transactionService.deleteTransaction(id, user);
        return ResponseEntity.noContent().build();
    }
}
