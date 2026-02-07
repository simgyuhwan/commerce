package com.flowcommerce.core.domain.member.service

import com.flowcommerce.core.domain.member.DocumentType
import com.flowcommerce.core.storage.member.SellerDocumentEntity
import com.flowcommerce.core.storage.member.SellerDocumentRepository
import com.flowcommerce.core.support.error.CoreException
import com.flowcommerce.core.support.error.ErrorType
import com.flowcommerce.core.support.utils.DocumentService
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.IOException

@Service
class SellerDocumentService(
    private val documentService: DocumentService,
    private val sellerDocumentRepository: SellerDocumentRepository
) {
    fun saveSellerDocuments(
        sellerId: Long,
        businessLicense: MultipartFile,
        bankBook: MultipartFile,
        onlineSalesReport: MultipartFile
    ) {
        saveDocument(sellerId, DocumentType.BUSINESS_LICENSE, businessLicense)
        saveDocument(sellerId, DocumentType.BANKBOOK_COPY, bankBook)
        saveDocument(sellerId, DocumentType.ONLINE_SALES_LICENSE, onlineSalesReport)
    }

    private fun saveDocument(sellerId: Long, documentType: DocumentType, file: MultipartFile) {
        try {
            val directory = "seller/$sellerId/${documentType.name}"
            val fileInfo = documentService.save(directory, file)

            sellerDocumentRepository.save(
                SellerDocumentEntity(
                    sellerId,
                    documentType,
                    file.originalFilename,
                    fileInfo.storedFileName,
                    fileInfo.filePath,
                    fileInfo.fileSize
                )
            )
        } catch (e: IOException) {
            throw CoreException(ErrorType.FILE_SAVE_FAILED)
        }
    }
}
