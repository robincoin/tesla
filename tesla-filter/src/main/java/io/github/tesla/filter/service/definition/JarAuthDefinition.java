package io.github.tesla.filter.service.definition;

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

}
