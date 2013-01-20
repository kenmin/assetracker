/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package home.assetracker.data.entity;

import home.assetracker.data.common.AppConfig;
import java.io.File;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicMatch;

/**
 *
 * @author kenmin
 */
@Entity
public class UploadedFile implements Serializable, ModifiableEntity {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Version
    private Integer version;
    private String fileName;
    private String remarks;
    @ManyToOne
    private Asset asset;
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedDateTime;
    private String lastModifiedUsername;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        String path = AppConfig.getInstance().getPropertyAsString("uploadedFile.uploadPath");
        if (getFileName() != null && path != null) {
            File file = new File(path + File.separator + getFileName());
            try {
                MagicMatch magicMatch = Magic.getMagicMatch(file, true);
                return magicMatch.getDescription() + " (" + magicMatch.getMimeType() + ")";
            } catch (Exception e) {
                // Do nothing
            }
        }
        return "";
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Asset getAsset() {
        return asset;
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    @Override
    public int hashCode() {
        int hash = 17;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UploadedFile)) {
            return false;
        }
        UploadedFile other = (UploadedFile) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "UploadedFile[ id=" + id + " ]";
    }

    @Override
    public Date getLastModifiedDateTime() {
        return lastModifiedDateTime;
    }

    @Override
    public void setLastModifiedDateTime(Date lastModifiedDateTime) {
        this.lastModifiedDateTime = lastModifiedDateTime;
    }

    @Override
    public String getLastModifiedUsername() {
        return lastModifiedUsername;
    }

    @Override
    public void setLastModifiedUsername(String lastModifiedUsername) {
        this.lastModifiedUsername = lastModifiedUsername;
    }
}
