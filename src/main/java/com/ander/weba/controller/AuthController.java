package com.ander.weba.controller;

import com.ander.weba.entity.AuthAccount;
import com.ander.weba.repository.AuthAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @Date 2018/12/25 9:50.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthAccountRepository authAccountRepository;

    @PostMapping("/account")
    public ResponseEntity saveAccount(@RequestBody AuthAccount authAccount) {
        authAccount.setCrTime(new Date());
        authAccount.setModTime(new Date());
        authAccountRepository.save(authAccount);
        return ResponseEntity.ok().build();
    }
}