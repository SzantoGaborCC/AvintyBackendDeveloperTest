package com.jwt_test.app.security.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.jwt_test.app.dto.response.ServerMessageDto;
import com.jwt_test.app.security.dto.request.JwtRefreshRequestDto;
import com.jwt_test.app.security.dto.request.LoginRequestDto;
import com.jwt_test.app.security.dto.request.LogoutRequestDto;
import com.jwt_test.app.security.dto.response.JwtLoginResponseDto;
import com.jwt_test.app.security.dto.response.JwtRefreshResponseDto;
import com.jwt_test.app.security.entity.RefreshToken;
import com.jwt_test.app.security.exception.TokenRefreshException;
import com.jwt_test.app.repository.EmployeeRepository;
import com.jwt_test.app.security.EmployeeDetails;
import com.jwt_test.app.security.JwtUtils;
import com.jwt_test.app.security.service.RefreshTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import static com.jwt_test.app.utility.Utility.createBadRequestMessageResponseEntity;

@CrossOrigin(origins = "http://localhost:5000")
@RestController
@RequestMapping("/security")
public class AuthController {

    @Autowired
    AuthenticationConfiguration authenticationConfiguration;

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    RefreshTokenService refreshTokenService;


    @RequestMapping(value =  "/login", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Request the JWT here, with email and password")
    @ApiResponse(
            responseCode = "200",
            description = "Login successful",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = JwtLoginResponseDto.class)
            ))
    @ApiResponse(
            responseCode = "400",
            description = "Login failure",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ServerMessageDto.class)
            ))
    public ResponseEntity<?> authenticateUser(
            @Valid @RequestBody LoginRequestDto loginRequestDto,
            BindingResult result) throws Exception {
        if (result.hasErrors()) {
            return createBadRequestMessageResponseEntity(result);
        }

        Authentication authentication = authenticationConfiguration.getAuthenticationManager()
                .authenticate(new UsernamePasswordAuthenticationToken(
                        loginRequestDto.getEmail(), loginRequestDto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication); //explicit authentication

        EmployeeDetails employeeDetails = (EmployeeDetails) authentication.getPrincipal();

        String jwt = jwtUtils.generateJwt(employeeDetails);

        List<String> roles = employeeDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(employeeDetails.getId());

        return ResponseEntity.ok(new JwtLoginResponseDto(jwt, refreshToken.getToken(), employeeDetails.getId(),
                employeeDetails.getUsername(), roles));
    }

    @RequestMapping(value =  "/refreshtoken", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Request your new JWT here, if the old expired, with refresh token")
    @ApiResponse(
            responseCode = "200",
            description = "Returns the new JWT, along with the refresh token used",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = JwtRefreshResponseDto.class)
            ))
    @ApiResponse(
            responseCode = "400",
            description = "Refresh token was invalid",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ServerMessageDto.class)
            ))
    public ResponseEntity<?> refreshtoken(
            @Valid @RequestBody JwtRefreshRequestDto request,
            BindingResult result) {
        if (result.hasErrors()) {
            return createBadRequestMessageResponseEntity(result);
        }

        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getEmployee)
                .map(employee -> {
                    String token = jwtUtils.generateTokenFromUsername(employee.getEmail());
                    return ResponseEntity.ok(new JwtRefreshResponseDto(token, requestRefreshToken));
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken,
                        "Refresh token is not in database!"));
    }

    @RequestMapping(value =  "/logout", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Logout by invalidating your refresh token, using user id, then you can only get new JWT by logging in again")
    public ResponseEntity<ServerMessageDto> logoutUser(@Valid @RequestBody LogoutRequestDto logoutRequestDto) {
        refreshTokenService.deleteByEmployeeId(logoutRequestDto.getEmployeeId());
        return ResponseEntity.ok(new ServerMessageDto("Logout successful!"));
    }
}
