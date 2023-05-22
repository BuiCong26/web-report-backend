package com.sm.base.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class ResourceBundle {

    MessageSourceAccessor messageSourceAccessor;
    private static final Logger logger = LogManager.getLogger(ResourceBundle.class);
    

    String locate;

    public ResourceBundle(String locate) {
        List<Locale> LOCALES = Arrays.asList(
                new Locale("en"),
                new Locale("la"),
                new Locale("vi"),
                new Locale("zh"));
        try {
            if (DataUtil.isNullOrEmpty(locate)) {
                this.messageSourceAccessor = new MessageSourceAccessor(messageSource(), Locale.lookup(Locale.LanguageRange.parse("la"), LOCALES));
            } else {
                this.messageSourceAccessor = new MessageSourceAccessor(messageSource(), Locale.lookup(Locale.LanguageRange.parse(locate.toLowerCase()), LOCALES));
            }
        } catch (Exception ex) {
            ex.printStackTrace();;
            logger.error(ex);
        }
    }

    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("static/config");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setUseCodeAsDefaultMessage(true);
        return messageSource;
    }

    public String getResourceMessage(String key) {

        try {
            return messageSourceAccessor.getMessage(key);
        } catch (Exception ex) {
            ex.printStackTrace();;
            logger.error(ex);
            return key;
        }
    }

    public String getResourceMessage(String key, Object... args) {

        try {
            return messageSourceAccessor.getMessage(key,args);
        } catch (Exception ex) {
            ex.printStackTrace();;
            logger.error(ex);
            return key;
        }
    }

   
}
