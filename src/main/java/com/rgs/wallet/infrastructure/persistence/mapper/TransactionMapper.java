package com.rgs.wallet.infrastructure.persistence.mapper;

import com.rgs.wallet.domain.model.Transaction;
import com.rgs.wallet.infrastructure.persistence.entity.TransactionEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionMapper {

    private final WalletMapper walletMapper;

    public Transaction toDomain(TransactionEntity entity) {
        return Transaction.builder()
                .id(entity.getId())
                .wallet(walletMapper.toDomain(entity.getWallet()))
                .amount(entity.getAmount())
                .type(entity.getType())
                .createdAt(entity.getCreatedAt())
                .relatedTransactionId(entity.getRelatedTransactionId())
                .build();
    }

    public TransactionEntity toEntity(Transaction transaction) {
        return TransactionEntity.builder()
                .id(transaction.getId())
                .wallet(walletMapper.toEntity(transaction.getWallet()))
                .amount(transaction.getSignedAmount())
                .type(transaction.getType())
                .relatedTransactionId(transaction.getRelatedTransactionId())
                .createdAt(transaction.getCreatedAt())
                .build();
    }
}
