package io.github.toberocat.core.extensions;

import io.github.toberocat.core.utility.version.Version;
import org.bukkit.Material;

import java.net.URL;
import java.util.Map;

public class ExtensionObject {
    private String fileName;
    private String displayName;
    private String registryName;
    private String[] description;
    private Material guiIcon;
    private Map<Version, URL> downloadLinks;
    private Version newestVersion;
    private String author;
    private String sha256;

    public ExtensionObject() {
    }

    public ExtensionObject(String fileName, String displayName, String[] description, Map<Version,
            URL> downloadLinks, Version newestVersion, String author, Material guiIcon, String sha256) {
        this.fileName = fileName;
        this.displayName = displayName;
        this.description = description;
        this.downloadLinks = downloadLinks;
        this.newestVersion = newestVersion;
        this.author = author;
        this.guiIcon = guiIcon;
        this.sha256 = sha256;
    }

    public String getSha256() {
        return sha256;
    }

    public void setSha256(String sha256) {
        this.sha256 = sha256;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String[] getDescription() {
        return description;
    }

    public void setDescription(String[] description) {
        this.description = description;
    }

    public Map<Version, URL> getDownloadLinks() {
        return downloadLinks;
    }

    public void setDownloadLinks(Map<Version, URL> downloadLinks) {
        this.downloadLinks = downloadLinks;
    }

    public Version getNewestVersion() {
        return newestVersion;
    }

    public void setNewestVersion(Version newestVersion) {
        this.newestVersion = newestVersion;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Material getGuiIcon() {
        return guiIcon;
    }

    public void setGuiIcon(Material guiIcon) {
        this.guiIcon = guiIcon;
    }

    public String getRegistryName() {
        return registryName;
    }

    public void setRegistryName(String registryName) {
        this.registryName = registryName;
    }
}
