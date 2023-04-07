package org.glebchanskiy.lois1.service.impl;

import org.glebchanskiy.lois1.model.Message;
import org.glebchanskiy.lois1.service.PdnfValidateService;
import org.springframework.stereotype.Service;

@Service
public class PdnfValidateServiceImpl implements PdnfValidateService {
    @Override
    public Message validate(String expression) {
        int answer = CheckPdnfFormula.checkFormula(expression);

        if (answer == 0) {
            return new Message("It's PDNF", "pdnf-validator", "view");
        }
        if (answer == 1) {
            return new Message("It's no PDNF", "pdnf-validator", "view");
        } else {
            return new Message("Not correct syntax", "pdnf-validator", "view");
        }
    }
}
