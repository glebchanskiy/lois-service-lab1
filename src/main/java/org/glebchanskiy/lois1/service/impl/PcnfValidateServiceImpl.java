package org.glebchanskiy.lois1.service.impl;

import org.glebchanskiy.lois1.model.Message;
import org.glebchanskiy.lois1.service.PcnfValidateService;
import org.springframework.stereotype.Service;

@Service
public class PcnfValidateServiceImpl implements PcnfValidateService {
    @Override
    public Message validate(String expression) {
        return new Message("Is pcnf", "pcnf-validator", "view");
    }
}
