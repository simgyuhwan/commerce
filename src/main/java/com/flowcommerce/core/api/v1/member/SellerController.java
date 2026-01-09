package com.flowcommerce.core.api.v1.member;

import com.flowcommerce.core.api.v1.member.request.SellerApplyRequest;
import com.flowcommerce.core.domain.User;
import com.flowcommerce.core.domain.member.service.SellerService;
import com.flowcommerce.core.support.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/api/v1/sellers")
@RestController
@RequiredArgsConstructor
public class SellerController {
    private final SellerService sellerService;

    @PostMapping(value = "/apply", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<Void> applySeller(
            User user,
            @RequestPart(value = "data") @Valid SellerApplyRequest request,
            @RequestPart("businessLicense") MultipartFile businessLicense,
            @RequestPart("bankbookCopy") MultipartFile bankbookCopy,
            @RequestPart("onlineSalesLicense") MultipartFile onlineSalesLicense
            ) {
      sellerService.apply(user, request.toSellerApply(), businessLicense, bankbookCopy, onlineSalesLicense);
      return ApiResponse.success();
    }
}
