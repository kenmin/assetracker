/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package home.assetracker.data.entity;

import java.util.Date;

/**
 *
 * @author kenmin
 */
public interface ModifiableEntity {

    Date getLastModifiedDateTime();

    void setLastModifiedDateTime(Date dateTime);

    String getLastModifiedUsername();

    void setLastModifiedUsername(String username);
}