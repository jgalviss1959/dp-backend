package com.digitalmoneyhouse.wallet_service.controller;

import com.digitalmoneyhouse.transaction_service.dto.TransactionDTO;
import com.digitalmoneyhouse.wallet_service.client.TransactionClient;
import com.digitalmoneyhouse.wallet_service.dto.AccountDTO;
import com.digitalmoneyhouse.wallet_service.dto.DashboardDTO;
import com.digitalmoneyhouse.wallet_service.entity.Account;
import com.digitalmoneyhouse.wallet_service.exception.AccountNotFoundException;
import com.digitalmoneyhouse.wallet_service.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@Tag(name = "Cuentas", description = "Operaciones relacionadas con cuentas de usuario")
public class AccountController {

    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    @Autowired
    private AccountService accountService;

    @Autowired(required = false)
    private TransactionClient transactionClient;

    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener cuenta por ID",
            description = "Devuelve los datos de una cuenta específica por su ID."
    )
    public ResponseEntity<AccountDTO> getAccount(@PathVariable Long id) {
        logger.info("Buscando cuenta con ID: {}", id);

        Account account = accountService.getAccountById(id);

        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setId(account.getId());
        accountDTO.setBalance(account.getBalance());
        accountDTO.setAlias(account.getAlias());
        accountDTO.setCvu(account.getCvu());

        return ResponseEntity.ok(accountDTO);
    }

    @GetMapping("/cvu/{cvu}")
    @Operation(
            summary = "Obtener cuenta por CVU",
            description = "Devuelve los datos de una cuenta específica usando su CVU."
    )
    public ResponseEntity<AccountDTO> getAccountByCvu(@PathVariable String cvu) {
        try {
            logger.info("Buscando cuenta con CVU: {}", cvu);
            AccountDTO accountDTO = accountService.findByCvu(cvu);
            return ResponseEntity.ok(accountDTO);
        } catch (AccountNotFoundException e) {
            logger.warn("Cuenta no encontrada con CVU: {}", cvu);
            return ResponseEntity.status(404).body(null);
        }
    }

    @GetMapping("/dashboard/{accountId}")
    @Operation(
            summary = "Obtener información del dashboard de una cuenta",
            description = "Retorna el balance, alias, CVU y las últimas 5 transacciones de una cuenta."
    )
    public ResponseEntity<DashboardDTO> getDashboard(@PathVariable Long accountId) {
        logger.info("Generando dashboard para cuenta con ID: {}", accountId);
        Account account = accountService.getAccountById(accountId);
        Double balance = account.getBalance().doubleValue();
        List<TransactionDTO> last5 = transactionClient.getLast5Transactions(accountId);

        DashboardDTO dashboard = new DashboardDTO();
        dashboard.setId(account.getId());
        dashboard.setAlias(account.getAlias());
        dashboard.setCvu(account.getCvu());
        dashboard.setBalance(balance);
        dashboard.setLastTransactions(last5);

        return ResponseEntity.ok(dashboard);
    }

    @PutMapping("/{accountId}/deposit")
    @Operation(
            summary = "Depositar dinero en la cuenta",
            description = "Aumenta el saldo de la cuenta especificada en el monto indicado."
    )
    public ResponseEntity<Void> increaseAccountBalance(@PathVariable Long accountId, @RequestBody BigDecimal amount) {
        logger.info("Intentando depositar {} en cuenta con ID: {}", amount, accountId);
        try {
            accountService.increaseBalance(accountId, amount);
            return ResponseEntity.noContent().build();
        } catch (com.digitalmoneyhouse.wallet_service.exception.AccountNotFoundException e) {
            logger.warn("Cuenta no encontrada para depósito. ID: {}", accountId);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error inesperado al depositar en cuenta ID {}: {}", accountId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/balance/{accountId}")
    @Operation(
            summary = "Actualizar saldo de cuenta",
            description = "Establece un nuevo saldo para la cuenta especificada."
    )
    public ResponseEntity<Void> updateAccountBalance(@PathVariable Long accountId, @RequestBody BigDecimal newBalance) {
        logger.info("Actualizando balance de cuenta ID: {} a nuevo valor: {}", accountId, newBalance);
        try {
            accountService.updateBalance(accountId, newBalance);
            return ResponseEntity.noContent().build();
        } catch (AccountNotFoundException e) {
            logger.warn("Cuenta no encontrada al intentar actualizar balance. ID: {}", accountId);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error inesperado al actualizar balance de cuenta ID {}: {}", accountId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
