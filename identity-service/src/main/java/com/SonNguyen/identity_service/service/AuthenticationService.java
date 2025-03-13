package com.SonNguyen.identity_service.service;

import com.SonNguyen.identity_service.dto.request.AuthenticationRequest;
import com.SonNguyen.identity_service.dto.request.IntrospectRequest;
import com.SonNguyen.identity_service.dto.response.AuthenticationResponse;
import com.SonNguyen.identity_service.dto.response.IntrospectResponse;
import com.SonNguyen.identity_service.entity.User;
import com.SonNguyen.identity_service.exception.AppException;
import com.SonNguyen.identity_service.exception.ErrorCode;
import com.SonNguyen.identity_service.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.StringJoiner;


@Slf4j
@Service
@RequiredArgsConstructor//Dung de autowired cac bean
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class AuthenticationService {
    UserRepository userRepository;

    @NonFinal//Annotation này dùng để tránh việc thuộc tính bị injec vào constructor
    @Value("${jwt.signerKey}")//Dùng để đọc 1 biến từ file.yaml
    protected String SIGNER_KEY;

    //thực hiện việc kiểm tra tính hợp lệ của một JWT
    public IntrospectResponse introspectResponse(IntrospectRequest introspectRequest)
            throws JOSEException, ParseException {

        //Lấy token từ yêu cầu
        String token = introspectRequest.getToken();

        //Đoạn này tạo một đối tượng JWSVerifier để xác thực chữ ký của JWT
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

        //Phân tích và giải mã token
        SignedJWT signedJWT = SignedJWT.parse(token);

        //Lấy thời gian hết hạn của token
        Date expityTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        //Kiểm tra tính hợp lệ của chữ ký JWT thông qua JWSVerifier
        boolean verified = signedJWT.verify(verifier);


        //Token này hợp lệ nếu chữ ký JWT đúng và thời gian chưa hết
        return IntrospectResponse.builder()
                .valid(verified&&expityTime.after(new Date()))
                .build();
    }


    //Khi người dùng đăng nhập thành công sẽ tạo ra 1 token
    private String generateToken(User user){

        //Thuật toán dùng để ký là HS512
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .issuer("SonNguyen.com")//tổ chức phát hành token
                .subject(user.getUserName())//chủ đề của token
                .issueTime(new Date())//thời điểm token được phát hành
                .expirationTime(new Date(// thời điểm token sẽ hết hạn
                        Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()
                ))
                .claim("authorities", new ArrayList<>(user.getRoles()))
                .build();


        //Payload chứa thông tin (claims) mà bạn đã tạo ở trên, được chuyển thành định dạng JSON.
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());


        JWSObject jwsObject = new JWSObject(jwsHeader,payload);//tạo token

        try {
            //Dùng để ký token JWSObject bằng HMAC sử dụng khoá bí mật
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();//chuyển jwsObj thành chuỗi
        } catch (JOSEException e) {
            log.error("Can not generate token",e);
            throw new RuntimeException(e);
        }
    }


//    private String buildScope(User user){
//        StringJoiner stringJoiner = new StringJoiner(" ");
//        if(!CollectionUtils.isEmpty(user.getRoles())){
//            user.getRoles().forEach(stringJoiner::add);
//        }
//        return stringJoiner.toString();
//    }


    /*
     * AuthenticationResponse xác thực khi user đăng nhập vào hệ thống
     * */
    public AuthenticationResponse authentication(AuthenticationRequest authenticationRequest){
        //Kiểm tra tên người dùng có tồn tại ko?
        User user = userRepository.findByUserName(
                        authenticationRequest.getUserName())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        //Kiểm tra mật khẩu
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean user_authencated = passwordEncoder.matches(authenticationRequest.getPassWord(),user.getPassWord());
        if(!user_authencated){
            throw new AppException(ErrorCode.UNAUTHENCATED);
        }
        var user_token = generateToken(user);
        return AuthenticationResponse.builder()
                .token(user_token)
                .authenticated(user_authencated)
                .build();
    }

}