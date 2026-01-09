package com.flowcommerce.core.domain.member.service;

import com.flowcommerce.core.domain.member.DocumentType;
import com.flowcommerce.core.storage.member.SellerDocumentEntity;
import com.flowcommerce.core.storage.member.SellerDocumentRepository;
import com.flowcommerce.core.support.error.CoreException;
import com.flowcommerce.core.support.error.ErrorType;
import com.flowcommerce.core.support.utils.DocumentService;
import com.flowcommerce.core.support.utils.FileInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class SellerDocumentService {
    private final DocumentService documentService;
    private final SellerDocumentRepository sellerDocumentRepository;

    public void saveSellerDocuments(Long sellerId, MultipartFile businessLicense, MultipartFile bankBook,
                                    MultipartFile onlineSalesReport) {
        saveDocument(sellerId, DocumentType.BUSINESS_LICENSE, businessLicense);
        saveDocument(sellerId, DocumentType.BANKBOOK_COPY, bankBook);
        saveDocument(sellerId, DocumentType.ONLINE_SALES_LICENSE, onlineSalesReport);
    }

    private void saveDocument(Long sellerId, DocumentType documentType, MultipartFile file) {
        try {
            String directory = String.format("seller/%d/%s", sellerId, documentType.name());
            FileInfo fileInfo = documentService.save(directory, file);

            sellerDocumentRepository.save(
                    new SellerDocumentEntity(
                            sellerId,
                            documentType,
                            file.getOriginalFilename(),
                            fileInfo.storedFileName(),
                            fileInfo.filePath(),
                            fileInfo.fileSize()
                    )
            );
        } catch (IOException e) {
            throw new CoreException(ErrorType.FILE_SAVE_FAILED);
        }
    }
}
