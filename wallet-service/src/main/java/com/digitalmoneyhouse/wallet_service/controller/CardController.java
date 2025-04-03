package com.digitalmoneyhouse.wallet_service.controller;

import com.digitalmoneyhouse.wallet_service.dto.CardDTO;
import com.digitalmoneyhouse.wallet_service.exception.CardAlreadyExistsException;
import com.digitalmoneyhouse.wallet_service.exception.CardNotFoundException;
import com.digitalmoneyhouse.wallet_service.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts/{accountId}/cards")
@Tag(name = "Tarjetas", description = "Gestión de tarjetas asociadas a una cuenta")
public class CardController {

    private static final Logger logger = LoggerFactory.getLogger(CardController.class);

    @Autowired
    private CardService cardService;

    @GetMapping
    @Operation(
            summary = "Listar tarjetas de una cuenta",
            description = "Obtiene todas las tarjetas asociadas a una cuenta específica."
    )
    public ResponseEntity<List<CardDTO>> getCards(@PathVariable Long accountId) {
        logger.info("Listando tarjetas para cuenta ID: {}", accountId);
        List<CardDTO> cards = cardService.getCardsByAccountId(accountId);
        return ResponseEntity.ok(cards);
    }

    @GetMapping("/{cardId}")
    @Operation(
            summary = "Obtener tarjeta específica",
            description = "Devuelve la información de una tarjeta específica asociada a una cuenta."
    )
    public ResponseEntity<CardDTO> getCard(@PathVariable Long accountId, @PathVariable Long cardId) {
        logger.info("Obteniendo tarjeta ID: {} para cuenta ID: {}", cardId, accountId);
        CardDTO card = cardService.getCardById(accountId, cardId);
        return ResponseEntity.ok(card);
    }

    @PostMapping
    @Operation(
            summary = "Agregar nueva tarjeta",
            description = "Agrega una tarjeta a la cuenta especificada."
    )
    public ResponseEntity<CardDTO> addCard(@PathVariable Long accountId, @Valid @RequestBody CardDTO cardDTO) {
        logger.info("Agregando tarjeta a la cuenta ID: {}", accountId);
        CardDTO newCard = cardService.addCardToAccount(accountId, cardDTO);
        return ResponseEntity.status(201).body(newCard);
    }

    @DeleteMapping("/{cardId}")
    @Operation(
            summary = "Eliminar tarjeta",
            description = "Elimina una tarjeta específica asociada a una cuenta."
    )
    public ResponseEntity<String> deleteCard(@PathVariable Long accountId, @PathVariable Long cardId) {
        logger.info("Eliminando tarjeta ID: {} de cuenta ID: {}", cardId, accountId);
        cardService.deleteCard(accountId, cardId);
        return ResponseEntity.ok("Tarjeta eliminada correctamente.");
    }

    @ExceptionHandler(CardAlreadyExistsException.class)
    public ResponseEntity<String> handleCardAlreadyExistsException(CardAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(CardNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleCardNotFoundException(CardNotFoundException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Card Not Found");
        response.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

}
