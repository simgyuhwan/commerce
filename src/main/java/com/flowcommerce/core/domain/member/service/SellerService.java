package com.flowcommerce.core.domain.member.service;

import com.flowcommerce.core.domain.User;
import com.flowcommerce.core.domain.member.SellerApply;
import com.flowcommerce.core.domain.member.SellerApplyValidator;
import com.flowcommerce.core.domain.member.SellerWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class SellerService {
    private final SellerApplyValidator sellerApplyValidator;
    private final SellerWriter sellerWriter;

    public Long apply(User user, SellerApply sellerApply, MultipartFile businessLicense, MultipartFile bankBook,
                      MultipartFile onlineSalesReport) {
        sellerApplyValidator.validateApply(user, sellerApply);
        return sellerWriter.save(user, sellerApply, businessLicense, bankBook, onlineSalesReport);
    }
}
