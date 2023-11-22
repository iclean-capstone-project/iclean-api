package iclean.code.function.common.service;

import iclean.code.data.enumjava.SendMailOptionEnum;

public interface EmailSenderService {

    void sendEmailTemplate(SendMailOptionEnum option, Object mail);

}
