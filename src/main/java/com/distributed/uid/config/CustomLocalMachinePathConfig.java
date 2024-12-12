package com.distributed.uid.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Properties;

@Slf4j
public class CustomLocalMachinePathConfig {

    @Value("${spring.application.name:}")
    private String applicationName;

    private static final String SEQ_SUFFIX = "_seq";


    private static final String DEFAULT_CONFIG_PATH = System.getProperty("user.home") + "/custom_seq.conf";

    private Long SEQ_VAL = null;


    @PostConstruct
    private void init() {
        Properties p = new Properties();
        InputStream inputStream = null;
        try {
            String props = applicationName.replaceAll("-","_") + SEQ_SUFFIX;
            inputStream = new BufferedInputStream(Files.newInputStream(Paths.get(DEFAULT_CONFIG_PATH)));
            p.load(inputStream);

            p.forEach((k,v) -> {
                if(Objects.equals(props,(String)k)) {
                    SEQ_VAL = Long.valueOf((String) v);
                }
            });
            log.info("CustomLocalMachinePathConfig.init load mch key config success ...");
        } catch (Exception e) {
            log.error("CustomLocalMachinePathConfig.init load mch key config failed",e);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    /**
     * 获取自定义序列
     *
     * @return
     */
    public Long getSeq() {
        return SEQ_VAL;
    }
}
