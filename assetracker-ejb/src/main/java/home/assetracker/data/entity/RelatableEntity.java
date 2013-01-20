/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package home.assetracker.data.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

/**
 *
 * @author kenmin
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class RelatableEntity implements Serializable, ModifiableEntity {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Version
    private Integer version;
    @NotNull
    private String name;
    private String description;
    private String imagePath;
    private String remarks;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @Override
    public int hashCode() {
        int hash = 23;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RelatableEntity)) {
            return false;
        }
        RelatableEntity other = (RelatableEntity) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "RelatableEntity[ id=" + id + " ]";
    }

    @Override
    public Date getLastModifiedDateTime() {
        return lastModifiedDateTime;
    }

    @Override
    public void setLastModifiedDateTime(Date dateTime) {
        this.lastModifiedDateTime = dateTime;
    }

    @Override
    public String getLastModifiedUsername() {
        return lastModifiedUsername;
    }

    @Override
    public void setLastModifiedUsername(String username) {
        this.lastModifiedUsername = username;
    }
}
