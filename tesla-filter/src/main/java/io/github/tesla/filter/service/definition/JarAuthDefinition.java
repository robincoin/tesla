package io.github.tesla.filter.service.definition;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;

import io.github.tesla.common.dto.ServiceDTO;
import io.github.tesla.filter.utils.JsonUtils;
import io.github.tesla.filter.utils.SnowflakeIdWorker;

public class JarAuthDefinition extends PluginDefinition {

    private String fileId;

    private String className;

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    @Override
    public String validate(String paramJson, ServiceDTO serviceDTO) {
        JarAuthDefinition definition = JsonUtils.fromJson(paramJson, JarAuthDefinition.class);
        Preconditions.checkArgument(StringUtils.isNotBlank(definition.getFileId()), "jar包执行插件-jar文件不可为空");
        if (definition.getFileId().contains(FILETABSERVICE)) {
            String jarFileId = SnowflakeIdWorker.nextId(PluginDefinition.FILEPREFIX);
            Preconditions.checkNotNull(PluginDefinition.UPLOADFILEMAP.get().get(definition.getFileId()),
                "jar包执行插件-jar文件不可为空");
            PluginDefinition.UPLOADFILEMAP.get().put(jarFileId,
                PluginDefinition.UPLOADFILEMAP.get().get(definition.getFileId()));
            PluginDefinition.UPLOADFILEMAP.get().remove(definition.getFileId());
            definition.setFileId(jarFileId);
        }
        return JsonUtils.serializeToJson(definition);
    }

}
