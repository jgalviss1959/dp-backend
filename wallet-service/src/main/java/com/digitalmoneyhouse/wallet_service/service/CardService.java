package com.digitalmoneyhouse.wallet_service.service;

import com.digitalmoneyhouse.wallet_service.dto.CardDTO;
import com.digitalmoneyhouse.wallet_service.entity.Card;
import com.digitalmoneyhouse.wallet_service.exception.CardAlreadyExistsException;
import com.digitalmoneyhouse.wallet_service.exception.CardNotFoundException;
import com.digitalmoneyhouse.wallet_service.repository.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CardService {

    @Autowired
    private CardRepository cardRepository;

    public List<CardDTO> getCardsByAccountId(Long accountId) {
        List<Card> cards = cardRepository.findByAccountId(accountId);
        return cards.stream()
                .map(card -> new CardDTO(card.getId(), card.getCardNumber(), card.getCardHolder(), card.getExpirationDate(), card.getCardType()))
                .collect(Collectors.toList());
    }

    public CardDTO getCardById(Long accountId, Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("No se encontró la tarjeta con ID " + cardId));

        if (!card.getAccountId().equals(accountId)) {
            throw new CardNotFoundException("La tarjeta no pertenece a esta cuenta.");
        }

        return new CardDTO(card.getId(), card.getCardNumber(), card.getCardHolder(), card.getExpirationDate(), card.getCardType());
    }

    public CardDTO addCardToAccount(Long accountId, CardDTO cardDTO) {
        Optional<Card> existingCard = cardRepository.findByCardNumber(cardDTO.getCardNumber());
        if (existingCard.isPresent()) {
            throw new CardAlreadyExistsException("La tarjeta ya está asociada a otra cuenta.");
        }

        Card card = new Card();
        card.setAccountId(accountId);
        card.setCardNumber(cardDTO.getCardNumber());
        card.setCardHolder(cardDTO.getCardHolder());
        card.setExpirationDate(cardDTO.getExpirationDate());
        card.setCardType(cardDTO.getCardType());

        Card savedCard = cardRepository.save(card);
        return new CardDTO(savedCard.getId(), savedCard.getCardNumber(), savedCard.getCardHolder(), savedCard.getExpirationDate(), savedCard.getCardType());
    }

    public void deleteCard(Long accountId, Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("No se encontró la tarjeta con ID " + cardId));

        if (!card.getAccountId().equals(accountId)) {
            throw new CardNotFoundException("La tarjeta no pertenece a esta cuenta.");
        }

        cardRepository.delete(card);
    }
}
