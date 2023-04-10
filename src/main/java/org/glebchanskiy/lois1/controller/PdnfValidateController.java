package org.glebchanskiy.lois1.controller;

import lombok.AllArgsConstructor;
import org.glebchanskiy.lois1.model.Message;
import org.glebchanskiy.lois1.service.PdnfValidateService;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/validate")
public class PdnfValidateController {

    private final PdnfValidateService pdnfValidateService;

    @CrossOrigin
    @PostMapping
    public Message validate(@RequestBody Message message) {
        return pdnfValidateService.validate(message.getText());
    }
}
