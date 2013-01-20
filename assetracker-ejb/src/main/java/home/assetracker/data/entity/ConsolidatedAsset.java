/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package home.assetracker.data.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Should map to the consolidatedAssetView view in DB
 *
 * @author kenmin
 */
@Entity
@Table(name = "ConsolidatedAssetView")
public class ConsolidatedAsset extends RelatableEntity {

    @Column
    private String dType;

    public String getdType() {
        return dType;
    }

    public void setdType(String dType) {
        this.dType = dType;
    }

    @Override
    public int hashCode() {
        int hash = 41;
        hash += (this.getId() != null ? this.getId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ConsolidatedAsset other = (ConsolidatedAsset) obj;
        return true;
    }

    @Override
    public String toString() {
        return "ConsolidatedAssetView[ id=" + this.getId() + " ]";
    }
}
