package com.flowcommerce.core.domain.member;

import com.flowcommerce.core.domain.HistoryType;
import com.flowcommerce.core.domain.User;
import com.flowcommerce.core.domain.member.service.SellerDocumentService;
import com.flowcommerce.core.storage.member.SellerEntity;
import com.flowcommerce.core.storage.member.SellerHistoryEntity;
import com.flowcommerce.core.storage.member.SellerHistoryRepository;
import com.flowcommerce.core.storage.member.SellerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class SellerWriter {
    private final SellerRepository sellerRepository;
    private final SellerHistoryRepository sellerHistoryRepository;
    private final SellerDocumentService sellerDocumentService;

    @Transactional
    public Long save(User user, SellerApply sellerApply, MultipartFile businessLicense,
                     MultipartFile bankBook, MultipartFile onlineSalesReport) {
        SellerEntity sellerEntity = sellerRepository.save(
                new SellerEntity(
                        user.id(),
                        sellerApply.getSellerName(),
                        SellerStatus.PENDING,
                        sellerApply.getCompanyName(),
                        sellerApply.getBusinessNumber(),
                        sellerApply.getOnlineSalesNumber(),
                        sellerApply.getBusinessType(),
                        sellerApply.getCompanyPhone(),
                        sellerApply.getAddress(),
                        sellerApply.getContactName(),
                        sellerApply.getContactPhone(),
                        sellerApply.getContactEmail(),
                        sellerApply.getAccountHolder(),
                        sellerApply.getBankName(),
                        sellerApply.getAccountNumber(),
                        sellerApply.getCommissionRate(),
                        sellerApply.getSettlementBasis(),
                        null,
                        null
                )
        );

        sellerHistoryRepository.save(
                new SellerHistoryEntity(
                        sellerEntity.getId(),
                        HistoryType.CREATED,
                        null,
                        SellerStatus.PENDING,
                        null,
                        null,
                        user.id()
                )
        );

        sellerDocumentService.saveSellerDocuments(sellerEntity.getId(), businessLicense, bankBook, onlineSalesReport);
        return sellerEntity.getId();
    }
}
