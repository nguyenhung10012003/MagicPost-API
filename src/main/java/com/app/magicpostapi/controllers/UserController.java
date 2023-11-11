package com.app.magicpostapi.controllers;

import com.app.magicpostapi.components.Role;
import com.app.magicpostapi.models.ResponseObject;
import com.app.magicpostapi.models.User;
import com.app.magicpostapi.repositories.UserRepository;
import com.app.magicpostapi.services.GatheringPointService;
import com.app.magicpostapi.services.TransactionPointService;
import com.app.magicpostapi.services.UserService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${spring.rest.path}/user")
public class UserController {
    @Autowired
    UserRepository userRepository;
    @Autowired
    TransactionPointService transactionPointService;
    @Autowired
    GatheringPointService gatheringPointService;
    @Autowired
    UserService userService;


    // Get all account (only highest manager can access here)
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("")
    ResponseEntity<ResponseObject> getAllUser() {
        List<User> accountsFound = userRepository.findAll();
        return new ResponseEntity<>(new ResponseObject("200", "Find Successful", accountsFound), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('TRANSACTION_POINT_MANAGER', 'ADMIN', 'GATHERING_POINT_MANAGER')")
    @GetMapping("/{idOffice}")
    ResponseEntity<ResponseObject> getAllOfficePointUser(@PathVariable @NonNull String idOffice) {
        if (idOffice.startsWith("TSP")) {
            if (!transactionPointService.checkOfficeExist(idOffice))
                return new ResponseEntity<>(
                        new ResponseObject("400",
                                "Workplace with id: " + idOffice + " does not exist!"
                        ), HttpStatus.BAD_REQUEST);
        } else if (idOffice.startsWith("GRP")) {
            if (!gatheringPointService.checkOfficeExist(idOffice))
                return new ResponseEntity<>(
                        new ResponseObject("400",
                                "Workplace with id: " + idOffice + " does not exist!"
                        ), HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>(
                    new ResponseObject("400",
                            "Unknown workplace with id: " + idOffice
                    ), HttpStatus.BAD_REQUEST);
        }
        List<User> accountsFound = userService.getAllUserByIdBranch(idOffice);
        return new ResponseEntity<>(new ResponseObject("200", "Find Successful", accountsFound), HttpStatus.OK);
    }

    /**
     * Create new user
     *
     * @param reqBody: <ul>
     *                 <li>role: role of user</li>
     *                 <li>idBranch: id of user work's place </li>
     *                 </ul>
     * @return http response: new user
     * @author milo
     * @apiNote only account with role ADMIN, GATHERING_POINT_MANAGER, TRANSACTION_POINT_MANAGER can access this endpoint
     */
    @PreAuthorize("hasAnyAuthority('ADMIN', 'GATHERING_POINT_MANAGER', 'TRANSACTION_POINT_MANAGER')")
    @PostMapping("")
    ResponseEntity<ResponseObject> createUser(@RequestBody Map<String, String> reqBody) {
        return new ResponseEntity<>(new ResponseObject(
                "200",
                "Create successful",
                userService.genaratedUser(reqBody.get("username"),
                        reqBody.get("password"),
                        Role.valueOf(reqBody.get("role")),
                        reqBody.get("idBranch")
                )
        ), HttpStatus.OK);
    }


    @PutMapping("/{id}")
    ResponseEntity<ResponseObject> editUser(@RequestBody Map<String, String> reqBody, @PathVariable Long id) {
        return new ResponseEntity<>(new ResponseObject(
                "ok",
                "Update successful",
                userService.editUser(id, reqBody)
        ), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'GATHERING_POINT_MANAGER', 'TRANSACTION_POINT_MANAGER')")
    @DeleteMapping("/{id}")
    ResponseEntity<ResponseObject> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(new ResponseObject("200", "Delete success"), HttpStatus.OK);
    }

}
