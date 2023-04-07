package org.glebchanskiy.lois1.controller;

import lombok.AllArgsConstructor;
import org.glebchanskiy.lois1.model.Message;
import org.glebchanskiy.lois1.service.PcnfValidateService;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/validate")
public class PcnfValidateController {

    private final PcnfValidateService pcnfValidateService;

    @PostMapping
    public Message validate(@RequestBody Message message) {
        return pcnfValidateService.validate(message.getText());
    }
}
