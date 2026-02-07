package com.flowcommerce.core.storage.member

import com.flowcommerce.core.domain.member.DocumentType
import com.flowcommerce.core.storage.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "seller_documents")
class SellerDocumentEntity(
    var sellerId: Long?,

    @Enumerated(EnumType.STRING)
    var documentType: DocumentType?,

    var originalFileName: String?,
    var storedFileName: String?,
    var filePath: String?,
    var fileSize: Long?
) : BaseEntity() {
    protected constructor() : this(null, null, null, null, null, null)
}
