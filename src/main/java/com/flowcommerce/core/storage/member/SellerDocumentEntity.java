package com.flowcommerce.core.storage.member;

import com.flowcommerce.core.domain.member.DocumentType;
import com.flowcommerce.core.storage.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "seller_documents")
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class SellerDocumentEntity extends BaseEntity {
    private Long sellerId;
    @Enumerated(EnumType.STRING)
    private DocumentType documentType;
    private String originalFileName;
    private String storedFileName;
    private String filePath;
    private Long fileSize;
}
