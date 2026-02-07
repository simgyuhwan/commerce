package com.flowcommerce.core.domain.member

import com.flowcommerce.core.domain.HistoryType
import com.flowcommerce.core.domain.User
import com.flowcommerce.core.domain.member.service.SellerDocumentService
import com.flowcommerce.core.storage.member.SellerEntity
import com.flowcommerce.core.storage.member.SellerHistoryEntity
import com.flowcommerce.core.storage.member.SellerHistoryRepository
import com.flowcommerce.core.storage.member.SellerRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile

@Component
class SellerWriter(
    private val sellerRepository: SellerRepository,
    private val sellerHistoryRepository: SellerHistoryRepository,
    private val sellerDocumentService: SellerDocumentService
) {
    @Transactional
    fun save(
        user: User,
        sellerApply: SellerApply,
        businessLicense: MultipartFile,
        bankBook: MultipartFile,
        onlineSalesReport: MultipartFile
    ): Long {
        val sellerEntity = sellerRepository.save(
            SellerEntity(
                user.id,
                sellerApply.sellerName,
                SellerStatus.PENDING,
                sellerApply.companyName,
                sellerApply.businessNumber,
                sellerApply.onlineSalesNumber,
                sellerApply.businessType,
                sellerApply.companyPhone,
                sellerApply.address,
                sellerApply.contactName,
                sellerApply.contactPhone,
                sellerApply.contactEmail,
                sellerApply.accountHolder,
                sellerApply.bankName,
                sellerApply.accountNumber,
                sellerApply.commissionRate,
                sellerApply.settlementBasis,
                null,
                null
            )
        )

        sellerHistoryRepository.save(
            SellerHistoryEntity(
                sellerEntity.id,
                HistoryType.CREATED,
                null,
                SellerStatus.PENDING,
                null,
                null,
                user.id
            )
        )

        sellerDocumentService.saveSellerDocuments(
            sellerEntity.id,
            businessLicense,
            bankBook,
            onlineSalesReport
        )
        return sellerEntity.id
    }
}
