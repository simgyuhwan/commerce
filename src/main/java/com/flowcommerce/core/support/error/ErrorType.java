package com.flowcommerce.core.support.error;

import lombok.Getter;
import org.springframework.boot.logging.LogLevel;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorType {
    DEFAULT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.E500, "알 수 없는 오류가 발생했습니다. 잠시 후 다시 시도해주세요.", LogLevel.ERROR),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, ErrorCode.E400, "요청이 올바르지 않습니다.", LogLevel.INFO),
    NOT_FOUND_DATA(HttpStatus.BAD_REQUEST, ErrorCode.E400, "해당 데이터를 찾을 수 없습니다.", LogLevel.ERROR),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, ErrorCode.E401, "인증되지 않은 사용자입니다.", LogLevel.WARN),

    // member
    DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST, ErrorCode.E401, "이미 사용 중인 이메일입니다.", LogLevel.INFO),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, ErrorCode.E401, "비밀번호가 유효하지 않습니다", LogLevel.INFO),
    INVALID_PASSWORD_POLICY(HttpStatus.BAD_REQUEST, ErrorCode.E401, "비밀번호는 8자 이상, 대소문자, 특수문자를 포함해야 합니다", LogLevel.INFO),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, ErrorCode.E401, "잘못된 입력 값입니다.", LogLevel.INFO),

    COMMISSION_REQUIRED(HttpStatus.BAD_REQUEST, ErrorCode.COM001,"수수료는 필수입니다", LogLevel.WARN),
    COMMISSION_NEGATIVE(HttpStatus.BAD_REQUEST, ErrorCode.COM002, "수수료는 0 이상이어야 합니다", LogLevel.WARN),
    COMMISSION_EXCEEDED(HttpStatus.BAD_REQUEST, ErrorCode.COM003,"수수료는 100%를 초과할 수 없습니다", LogLevel.WARN),

    // Company (업체 정보)
    COMPANY_NAME_REQUIRED(HttpStatus.BAD_REQUEST, ErrorCode.CMP001, "업체명은 필수입니다", LogLevel.WARN),
    BUSINESS_NUMBER_REQUIRED(HttpStatus.BAD_REQUEST, ErrorCode.CMP002, "사업자등록번호는 필수입니다", LogLevel.WARN),
    BUSINESS_NUMBER_INVALID(HttpStatus.BAD_REQUEST, ErrorCode.CMP003, "올바른 사업자등록번호 형식이 아닙니다", LogLevel.WARN),
    ONLINE_SALES_NUMBER_REQUIRED(HttpStatus.BAD_REQUEST, ErrorCode.CMP004, "통신판매업신고번호는 필수입니다", LogLevel.WARN),

    // Contact (담당자 정보)
    CONTACT_NAME_REQUIRED(HttpStatus.BAD_REQUEST, ErrorCode.CNT001, "담당자명은 필수입니다", LogLevel.WARN),
    CONTACT_PHONE_REQUIRED(HttpStatus.BAD_REQUEST, ErrorCode.CNT002, "담당자 연락처는 필수입니다", LogLevel.WARN),
    CONTACT_PHONE_INVALID(HttpStatus.BAD_REQUEST, ErrorCode.CNT003, "올바른 연락처 형식이 아닙니다", LogLevel.WARN),
    CONTACT_EMAIL_REQUIRED(HttpStatus.BAD_REQUEST, ErrorCode.CNT004, "이메일은 필수입니다", LogLevel.WARN),
    CONTACT_EMAIL_INVALID(HttpStatus.BAD_REQUEST, ErrorCode.CNT005, "올바른 이메일 형식이 아닙니다", LogLevel.WARN),

    // Payment (결제 정보)
    ACCOUNT_HOLDER_REQUIRED(HttpStatus.BAD_REQUEST, ErrorCode.PAY001, "계좌 명의는 필수입니다", LogLevel.WARN),
    BANK_NAME_REQUIRED(HttpStatus.BAD_REQUEST, ErrorCode.PAY002, "은행명은 필수입니다", LogLevel.WARN),
    ACCOUNT_NUMBER_REQUIRED(HttpStatus.BAD_REQUEST, ErrorCode.PAY003, "계좌번호는 필수입니다", LogLevel.WARN),
    ACCOUNT_VERIFICATION_FAILED(HttpStatus.BAD_REQUEST, ErrorCode.PAY004, "계좌 실명 인증에 실패했습니다", LogLevel.WARN),

    // Seller (셀러)
    SELLER_NAME_REQUIRED(HttpStatus.BAD_REQUEST, ErrorCode.SEL001, "셀러명은 필수입니다", LogLevel.WARN),
    SELLER_NAME_DUPLICATE(HttpStatus.CONFLICT, ErrorCode.SEL002, "이미 사용 중인 셀러명입니다", LogLevel.WARN),
    SELLER_NOT_FOUND(HttpStatus.NOT_FOUND, ErrorCode.SEL003, "셀러를 찾을 수 없습니다", LogLevel.INFO),
    SELLER_ALREADY_EXISTS(HttpStatus.CONFLICT, ErrorCode.SEL004, "이미 셀러로 등록되어 있습니다", LogLevel.WARN),
    SELLER_APPLICATION_PENDING(HttpStatus.CONFLICT, ErrorCode.SEL005, "이미 승인 대기 중인 신청이 있습니다", LogLevel.INFO),
    BUSINESS_NUMBER_DUPLICATE(HttpStatus.CONFLICT, ErrorCode.SEL006, "이미 등록된 사업자 번호 입니다.", LogLevel.INFO),

    // Document (서류)
    DOCUMENT_REQUIRED(HttpStatus.BAD_REQUEST, ErrorCode.DOC001, "필수 서류가 누락되었습니다", LogLevel.WARN),
    DOCUMENT_INVALID_TYPE(HttpStatus.BAD_REQUEST, ErrorCode.DOC002, "지원하지 않는 파일 형식입니다", LogLevel.WARN),
    DOCUMENT_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST, ErrorCode.DOC003, "파일 크기가 제한을 초과했습니다", LogLevel.WARN),

    // file
    FILE_SAVE_FAILED(HttpStatus.BAD_REQUEST, ErrorCode.FIL001, "파일 저장에 실패했습니다", LogLevel.ERROR)

    ;

    private final HttpStatus status;
    private final ErrorCode code;
    private final String message;
    private final LogLevel logLevel;

    ErrorType(HttpStatus status, ErrorCode code, String message, LogLevel logLevel) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.logLevel = logLevel;
    }
}

