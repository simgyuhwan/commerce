package com.flowcommerce.core.domain.member.service

import com.flowcommerce.core.domain.User
import com.flowcommerce.core.domain.member.SellerApply
import com.flowcommerce.core.domain.member.SellerApplyValidator
import com.flowcommerce.core.domain.member.SellerWriter
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class SellerService(
    private val sellerApplyValidator: SellerApplyValidator,
    private val sellerWriter: SellerWriter
) {
    fun apply(
        user: User,
        sellerApply: SellerApply,
        businessLicense: MultipartFile,
        bankBook: MultipartFile,
        onlineSalesReport: MultipartFile
    ): Long {
        sellerApplyValidator.validateApply(user, sellerApply)
        return sellerWriter.save(user, sellerApply, businessLicense, bankBook, onlineSalesReport)
    }
}
