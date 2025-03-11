package com.SonNguyen.identity_service.Controller;

import com.SonNguyen.identity_service.dto.request.ApiResponse;
import com.SonNguyen.identity_service.dto.request.AuthenticationRequest;
import com.SonNguyen.identity_service.dto.request.IntrospectRequest;
import com.SonNguyen.identity_service.dto.response.AuthenticationResponse;
import com.SonNguyen.identity_service.dto.response.IntrospectResponse;
import com.SonNguyen.identity_service.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor//Dung de autowired cac bean
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Builder
public class AuthenticationController {
    AuthenticationService authenticationService;

    @PostMapping("/token")
    ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest authenticationRequest){
        AuthenticationResponse res = authenticationService.authentication(authenticationRequest);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(res)
                .build();
    }

    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> authenticate(@RequestBody IntrospectRequest introspectRequest)
            throws ParseException, JOSEException {
        var res = authenticationService.introspectResponse(introspectRequest);
        return ApiResponse.<IntrospectResponse>builder()
                .result(res)
                .build();
    }
}
