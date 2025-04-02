package com.digitalmoneyhouse.transaction_service.controller;

import com.digitalmoneyhouse.transaction_service.dto.ActivityDTO;
import com.digitalmoneyhouse.transaction_service.dto.DepositRequestDTO;
import com.digitalmoneyhouse.transaction_service.dto.TransactionDTO;
import com.digitalmoneyhouse.transaction_service.dto.TransferRequestDTO;
import com.digitalmoneyhouse.transaction_service.entity.Transaction;
import com.digitalmoneyhouse.transaction_service.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

    @Autowired
    private TransactionService transactionService;

    // Obtener las últimas 5 transacciones
    @GetMapping("/account/{accountId}/last")
    public ResponseEntity<List<TransactionDTO>> getLast5Transactions(@PathVariable Long accountId) {
        List<Transaction> transactions = transactionService.getLast5TransactionsByAccountId(accountId);
        List<TransactionDTO> transactionDTOs = transactions.stream()
                .map(tx -> new TransactionDTO(
                        tx.getId(),
                        tx.getAccountId(),
                        tx.getAmount(),
                        tx.getDate(),
                        tx.getType()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(transactionDTOs);
    }

    // Obtener toda la actividad de una cuenta
    @GetMapping("/accounts/{id}/activity")
    public ResponseEntity<?> getAccountActivity(@PathVariable Long id,
                                                @RequestHeader(value = "Authorization", required = false) String token) {

        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Sin permisos");
        }

        if (!transactionService.accountExists(id)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cuenta no encontrada");
        }

        List<ActivityDTO> activities = transactionService.getAccountActivity(id);
        return ResponseEntity.ok(activities);
    }

    // Obtener detalle de una actividad
    @GetMapping("/accounts/{id}/activity/{activityId}")
    public ResponseEntity<?> getActivityDetail(@PathVariable Long id,
                                               @PathVariable Long activityId,
                                               @RequestHeader(value = "Authorization", required = false) String token) {

        try {
            if (id == null || id <= 0 || activityId == null || activityId <= 0) {
                logger.warn("Solicitud con ID inválido: accountId={}, activityId={}", id, activityId);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ID inválido");
            }

            if (token == null || token.isEmpty()) {
                logger.warn("Acceso sin token para accountId={}, activityId={}", id, activityId);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Sin permisos");
            }

            if (!transactionService.accountExists(id)) {
                logger.warn("Cuenta no encontrada: accountId={}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cuenta no encontrada");
            }

            Optional<ActivityDTO> activity = transactionService.getActivityDetail(id, activityId);
            if (activity.isEmpty()) {
                logger.warn("ActivityID inexistente: accountId={}, activityId={}", id, activityId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("ActivityID inexistente");
            }

            return ResponseEntity.ok(activity.get());

        } catch (Exception e) {
            logger.error("Error inesperado en getActivityDetail: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor");
        }
    }

    // Registrar ingreso desde tarjeta
    @PostMapping("/accounts/{id}/transferences")
    public ResponseEntity<?> depositToWallet(@PathVariable Long id,
                                             @RequestBody DepositRequestDTO depositRequest,
                                             @RequestHeader("Authorization") String token) {
        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Sin permisos");
        }

        if (!id.equals(depositRequest.getAccountId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ID de cuenta no coincide");
        }

        Transaction transaction = transactionService.depositToWallet(depositRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
    }

    // Registrar una transferencia entre cuentas
    @PostMapping("/accounts/transfer")
    public ResponseEntity<?> transferBetweenAccounts(@RequestBody TransferRequestDTO request,
                                                     @RequestHeader("Authorization") String token) {
        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Sin permisos");
        }

        try {
            Transaction transaction = transactionService.transferBetweenAccounts(request.getOrigin(), request.getDestination(), request.getAmount());
            return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error en la transferencia.");
        }
    }


}
