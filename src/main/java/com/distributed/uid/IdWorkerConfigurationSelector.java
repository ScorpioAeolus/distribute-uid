package com.distributed.uid;

import com.distributed.uid.annotation.EnableIdWorker;
import com.distributed.uid.config.DBIdWorkerConfig;
import com.distributed.uid.config.LocalMachineIdWorkerConfig;
import com.distributed.uid.config.RedisIdWorkerConfiguration;
import com.distributed.uid.config.ZkIdWorkerConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;

/**
 * 发号器选择器
 *
 * @author typhoon
 * @since 1.0.0
 **/
@Slf4j
public class IdWorkerConfigurationSelector implements ImportSelector {

    public static final String DEFAULT_ADVICE_MODE_ATTRIBUTE_NAME = "mode";


    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        Class<?> annType = EnableIdWorker.class;
        Assert.state(annType != null, "Unresolvable type argument for IdWorkerConfigurationSelector");
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(annType.getName(), false));
        if (attributes == null) {
            throw new IllegalArgumentException(String.format(
                    "@%s is not present on importing class '%s' as expected",
                    annType.getSimpleName(), importingClassMetadata.getClassName()));
        }
        CenterModel centerModel = attributes.getEnum(getModeAttributeName());
        switch (centerModel) {
            case DB:
                log.info("IdWorkerConfigurationSelector.selectImports use DBIdWorker........");
                return new String[]{DBIdWorkerConfig.class.getName()};
            case REDIS:
                log.info("IdWorkerConfigurationSelector.selectImports use RedisIdWorker........");
                return new String[]{RedisIdWorkerConfiguration.class.getName()};
            case ZOOKEEPER:
                log.info("IdWorkerConfigurationSelector.selectImports use ZookeeperIdWorker........");
                return new String[]{ZkIdWorkerConfiguration.class.getName()};
            case LOCAL_MACHINE:
                log.info("IdWorkerConfigurationSelector.selectImports use LocalMachineIdWorker........");
                return new String[]{LocalMachineIdWorkerConfig.class.getName()};
            default:
                return null;
        }
    }

    protected String getModeAttributeName() {
        return DEFAULT_ADVICE_MODE_ATTRIBUTE_NAME;
    }
}
