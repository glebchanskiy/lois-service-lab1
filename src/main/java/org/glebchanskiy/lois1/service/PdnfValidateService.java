package org.glebchanskiy.lois1.service;

import org.glebchanskiy.lois1.model.Message;

public interface PdnfValidateService {
    public Message validate(String expression);
}
