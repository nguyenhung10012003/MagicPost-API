package com.app.magicpostapi.controllers;

import com.app.magicpostapi.components.Role;
import com.app.magicpostapi.models.GatheringPoint;
import com.app.magicpostapi.models.ResponseObject;
import com.app.magicpostapi.models.User;
import com.app.magicpostapi.services.GatheringPointService;
import com.app.magicpostapi.services.UserService;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${spring.rest.path}/gathering-point")
public class GatheringPointController {
    @Autowired
    GatheringPointService gatheringPointService;
    @Autowired
    UserService userService;

    @GetMapping("")
    ResponseEntity<ResponseObject> getAllGatheringPoint() {
        return new ResponseEntity<>(new ResponseObject(
                "200",
                "Find successful",
                gatheringPointService.findAllGatheringPoint()
        ), HttpStatus.OK);
    }

    @PostMapping("")
    ResponseEntity<ResponseObject> newGatheringPoint(@RequestBody Map<String, String> data) {
        class GatheringPointWithAccount {
            @JsonProperty("Gathering Point")
            final GatheringPoint gatheringPoint;
            @JsonProperty("User")
            final User user;

            public GatheringPointWithAccount(GatheringPoint gatheringPoint, User user) {
                this.gatheringPoint = gatheringPoint;
                this.user = user;
            }
        }

        GatheringPoint newGatheringPoint = gatheringPointService.newGatheringPoint(data.get("name"), data.get("city"), data.get("address"));
        User newUser = gatheringPointService.newAccountWithGatheringId(Role.GATHERING_POINT_MANAGER, newGatheringPoint.getId());
        return new ResponseEntity<>(new ResponseObject(
                "200",
                "Create successful",
                new GatheringPointWithAccount(newGatheringPoint, newUser)
        ), HttpStatus.OK);

    }

    @GetMapping("/{id}")
    ResponseEntity<ResponseObject> getTransactionInfo(@PathVariable @NonNull String id) {

        class GatheringInfo {
            @JsonProperty("gatheringPoint")
            private final GatheringPoint gatheringPoint;
            @JsonProperty("users")
            private final List<User> users;

            public GatheringInfo(GatheringPoint gatheringPoint, List<User> users) {
                this.gatheringPoint = gatheringPoint;
                this.users = users;
            }
        }

        if (gatheringPointService.checkOfficeExist(id)) {
            return new ResponseEntity<>(new ResponseObject(
                    "200",
                    "Find successful",
                    new GatheringInfo(gatheringPointService.findById(id), userService.getAllUserByIdBranch(id))
            ), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ResponseObject(
                    "400",
                    "No data found"
            ), HttpStatus.BAD_REQUEST);
        }
    }
}
