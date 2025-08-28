package com.safetynetalerts.safetynet_alerts.controller;

import com.safetynetalerts.safetynet_alerts.dto.PersonInfoDTO;
import com.safetynetalerts.safetynet_alerts.service.PersonInfoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/personInfo", produces = "application/json")
public class PersonInfoController {

    private final PersonInfoService personInfoService;

    public PersonInfoController(PersonInfoService personInfoService) {
        this.personInfoService = personInfoService;
    }

    @GetMapping
    public List<PersonInfoDTO> getPersonInfo(@RequestParam String lastName) {
        return personInfoService.getPersonsInfoByLastName(lastName);
    }
}

