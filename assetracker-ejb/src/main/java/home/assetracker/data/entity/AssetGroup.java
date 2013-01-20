/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package home.assetracker.data.entity;

import javax.persistence.Entity;

/**
 *
 * @author kenmin
 */
@Entity
public class AssetGroup extends RelatableEntity {

    @Override
    public int hashCode() {
        int hash = 53;
        hash += (this.getId() != null ? this.getId().hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "AssetGroup[ id=" + this.getId() + " ]";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AssetGroup other = (AssetGroup) obj;
        return true;
    }
}
