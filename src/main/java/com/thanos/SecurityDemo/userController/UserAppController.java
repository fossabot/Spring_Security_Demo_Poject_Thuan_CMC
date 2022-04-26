package com.thanos.SecurityDemo.userController;

import com.thanos.SecurityDemo.Entity.UserApp;
import com.thanos.SecurityDemo.userService.UserAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/")
public class UserAppController {

    @Autowired
    private UserAppService userAppService;

    @GetMapping("/allUser")
    @PreAuthorize("hasAuthority('admin:read')")
    public ResponseEntity<List<UserApp>> getUserAppTable(){
        return new ResponseEntity<>(userAppService.getAllUserApp(), HttpStatus.OK);
    }
}