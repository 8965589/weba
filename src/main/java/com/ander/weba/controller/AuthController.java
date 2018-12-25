package com.ander.weba.controller;

import com.ander.weba.entity.AuthAccount;
import com.ander.weba.repository.AuthAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Optional;

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

    @PostMapping("/account/delete/{id}")
    public void deleteAccount(@PathVariable("id") String id) {
        authAccountRepository.deleteById(id);
    }

    @PostMapping("/account/update/{id}")
    public AuthAccount updateUser(@PathVariable("id") String id, @RequestBody AuthAccount authAccount) {
        authAccount.setId(id);
        authAccount.setModTime(new Date());
        //创建时间 没了
        return authAccountRepository.saveAndFlush(authAccount);

    }

    @GetMapping("/account/get/{id}")
    public AuthAccount getUserInfo(@PathVariable("id") String id) {
        Optional<AuthAccount> optional = authAccountRepository.findById(id);
        return optional.orElseGet(AuthAccount::new);
    }


    @GetMapping("/account/list")
    public Page<AuthAccount> pageQuery(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                       @RequestParam(value = "pageSize", defaultValue = "1") Integer pageSize) {
        return authAccountRepository.findAll(PageRequest.of(pageNum - 1, pageSize));
    }


}