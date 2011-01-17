/*
 * Atricore IDBus
 *
 *   Copyright 2009, Atricore Inc.
 *
 *   This is free software; you can redistribute it and/or modify it
 *   under the terms of the GNU Lesser General Public License as
 *   published by the Free Software Foundation; either version 2.1 of
 *   the License, or (at your option) any later version.
 *
 *   This software is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *   Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this software; if not, write to the Free
 *   Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *   02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package com.atricore.idbus.console.services.dto;

import com.atricore.liveservices.liveupdate._1_0.md.UpdateNatureType;
import sun.util.calendar.LocalGregorianCalendar;

import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author: Dusan Fisic
 * @email: dfisic@atricore.org
 * Date: 1/17/11 - 3:00 PM
 */
public class UpdateDescriptorTypeDTO {
    private String id;
    private String group;
    private String name;
    private String version;
    private String description;
    private String updateNature;
    private Date issueInstant;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUpdateNature() {
        return updateNature;
    }

    public void setUpdateNature(String updateNature) {
        this.updateNature = updateNature;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getIssueInstant() {
        return issueInstant;
    }

    public void setIssueInstant(Date issueInstant) {
        this.issueInstant = issueInstant;
    }
}
