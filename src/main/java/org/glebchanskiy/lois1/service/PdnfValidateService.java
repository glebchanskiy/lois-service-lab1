package org.glebchanskiy.lois1.service;

import org.glebchanskiy.lois1.model.Message;

public interface PcnfValidateService {
    public Message validate(String expression);
}
