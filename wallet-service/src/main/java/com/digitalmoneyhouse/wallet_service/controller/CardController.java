package com.digitalmoneyhouse.wallet_service.controller;

import com.digitalmoneyhouse.wallet_service.dto.CardDTO;
import com.digitalmoneyhouse.wallet_service.exception.CardAlreadyExistsException;
import com.digitalmoneyhouse.wallet_service.exception.CardNotFoundException;
import com.digitalmoneyhouse.wallet_service.service.CardService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts/{accountId}/cards")
public class CardController {

    @Autowired
    private CardService cardService;

    @GetMapping
    public ResponseEntity<List<CardDTO>> getCards(@PathVariable Long accountId) {
        List<CardDTO> cards = cardService.getCardsByAccountId(accountId);
        return ResponseEntity.ok(cards);
    }

    @GetMapping("/{cardId}")
    public ResponseEntity<CardDTO> getCard(@PathVariable Long accountId, @PathVariable Long cardId) {
        CardDTO card = cardService.getCardById(accountId, cardId);
        return ResponseEntity.ok(card);
    }

    @PostMapping
    public ResponseEntity<CardDTO> addCard(@PathVariable Long accountId, @Valid @RequestBody CardDTO cardDTO) {
        CardDTO newCard = cardService.addCardToAccount(accountId, cardDTO);
        return ResponseEntity.status(201).body(newCard);
    }

    @DeleteMapping("/{cardId}")
    public ResponseEntity<String> deleteCard(@PathVariable Long accountId, @PathVariable Long cardId) {
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
