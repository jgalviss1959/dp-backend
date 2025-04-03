package com.digitalmoneyhouse.transaction_service.controller;

import com.digitalmoneyhouse.transaction_service.dto.ActivityDTO;
import com.digitalmoneyhouse.transaction_service.dto.DepositRequestDTO;
import com.digitalmoneyhouse.transaction_service.dto.TransactionDTO;
import com.digitalmoneyhouse.transaction_service.dto.TransferRequestDTO;
import com.digitalmoneyhouse.transaction_service.entity.Transaction;
import com.digitalmoneyhouse.transaction_service.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transactions")
@Tag(name = "Transacciones", description = "Gestión de transacciones y actividades de cuenta")
public class TransactionController {

    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

    @Autowired
    private TransactionService transactionService;

    @GetMapping("/account/{accountId}/last")
    @Operation(
            summary = "Obtener últimas 5 transacciones",
            description = "Devuelve las últimas cinco transacciones realizadas por una cuenta."
    )
    public ResponseEntity<List<TransactionDTO>> getLast5Transactions(@PathVariable Long accountId) {
        logger.info("Obteniendo últimas 5 transacciones para la cuenta ID: {}", accountId);
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

    @GetMapping("/accounts/{id}/activity")
    @Operation(
            summary = "Obtener actividad de la cuenta",
            description = "Devuelve todas las actividades (movimientos) de una cuenta específica."
    )
    public ResponseEntity<?> getAccountActivity(@PathVariable Long id,
                                                @RequestHeader(value = "Authorization", required = false) String token) {

        logger.info("Solicitando actividad de cuenta ID: {}", id);

        if (token == null || token.isEmpty()) {
            logger.warn("Solicitud sin token de autorización");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Sin permisos");
        }

        if (!transactionService.accountExists(id)) {
            logger.warn("Cuenta no encontrada: {}", id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cuenta no encontrada");
        }

        List<ActivityDTO> activities = transactionService.getAccountActivity(id);
        return ResponseEntity.ok(activities);
    }

    @GetMapping("/accounts/{id}/activity/{activityId}")
    @Operation(
            summary = "Detalle de una actividad",
            description = "Obtiene los detalles de una actividad específica dentro de una cuenta."
    )
    public ResponseEntity<?> getActivityDetail(@PathVariable Long id,
                                               @PathVariable Long activityId,
                                               @RequestHeader(value = "Authorization", required = false) String token) {

        try {
            logger.info("Consultando detalle de actividad. Cuenta ID: {}, Actividad ID: {}", id, activityId);

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

    @PostMapping("/accounts/{id}/transferences")
    @Operation(
            summary = "Registrar ingreso desde tarjeta",
            description = "Permite registrar un depósito desde tarjeta a la cuenta indicada."
    )
    public ResponseEntity<?> depositToWallet(@PathVariable Long id,
                                             @RequestBody DepositRequestDTO depositRequest,
                                             @RequestHeader("Authorization") String token) {

        logger.info("Registrando ingreso desde tarjeta en cuenta ID: {}", id);

        if (token == null || token.isEmpty()) {
            logger.warn("Solicitud sin token al realizar depósito");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Sin permisos");
        }

        if (!id.equals(depositRequest.getAccountId())) {
            logger.warn("El ID de cuenta no coincide con la tarjeta");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ID de cuenta no coincide");
        }

        Transaction transaction = transactionService.depositToWallet(depositRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
    }

    @PostMapping("/accounts/transfer")
    @Operation(
            summary = "Transferencia entre cuentas",
            description = "Realiza una transferencia entre dos cuentas mediante CVU."
    )
    public ResponseEntity<?> transferBetweenAccounts(@RequestBody TransferRequestDTO request,
                                                     @RequestHeader("Authorization") String token) {

        logger.info("Iniciando transferencia entre cuentas. Origen: {}, Destino: {}", request.getOrigin(), request.getDestination());

        if (token == null || token.isEmpty()) {
            logger.warn("Transferencia sin token de autorización");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Sin permisos");
        }

        try {
            Transaction transaction = transactionService.transferBetweenAccounts(request.getOrigin(), request.getDestination(), request.getAmount());
            return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
        } catch (IllegalArgumentException e) {
            logger.warn("Transferencia inválida: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error en la transferencia: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error en la transferencia.");
        }
    }

}
