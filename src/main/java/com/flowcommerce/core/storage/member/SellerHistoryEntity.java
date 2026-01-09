package com.flowcommerce.core.storage.member;

import com.flowcommerce.core.domain.HistoryType;
import com.flowcommerce.core.domain.member.RejectionReason;
import com.flowcommerce.core.domain.member.SellerStatus;
import com.flowcommerce.core.storage.BaseEntity;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class SellerHistoryEntity extends BaseEntity {
    private Long sellerId;
    private HistoryType type;
    private SellerStatus previousStatus;
    private SellerStatus newStatus;
    private RejectionReason rejectionReason;
    private String rejectionDetail;
    private Long processedBy;
}
