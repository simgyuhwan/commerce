package com.flowcommerce.core.domain.member;

public enum RejectionReason {
    INVALID_BUSINESS_NUMBER, // 사업자등록번호 오류
    INVALID_DOCUMENT, // 서류 미비 또는 위조
    DUPLICATE_SELLER, //중복 신청
    EXCESSIVE_COMMISSION, //수수료 과다 요청
    POLICY_VIOLATION, //
    OTHER // 기타
}
