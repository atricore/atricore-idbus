package org.atricore.idbus.capabilities.sso.main.select.internal;

import org.atricore.idbus.kernel.main.mediation.claim.ClaimSet;
import org.atricore.idbus.common.sso._1_0.protocol.SelectEntityRequestType;

/**
 *
 */
public class EntitySelectionState implements java.io.Serializable {

    private ClaimSet userClaims;
    private SelectEntityRequestType request;

    private int nextSelectorIdx;
    private int nextSelectorEndpointIdx;

    private String selectedCotMember;

    private String previousCotMember;

    public ClaimSet getUserClaims() {
        return userClaims;
    }

    public void setUserClaims(ClaimSet userClaims) {
        this.userClaims = userClaims;
    }

    public SelectEntityRequestType getRequest() {
        return request;
    }

    public void setRequest(SelectEntityRequestType request) {
        this.request = request;
    }

    public String getSelectedCotMember() {
        return selectedCotMember;
    }

    public void setSelectedCotMember(String selectedCotMember) {
        this.selectedCotMember = selectedCotMember;
    }

    public int getNextSelectorIdx() {
        return nextSelectorIdx;
    }

    public void setNextSelectorIdx(int nextSelectorIdx) {
        this.nextSelectorIdx = nextSelectorIdx;
    }

    public int getNextSelectorEndpointIdx() {
        return nextSelectorEndpointIdx;
    }

    public void setNextSelectorEndpointIdx(int nextSelectorEndpointIdx) {
        this.nextSelectorEndpointIdx = nextSelectorEndpointIdx;
    }

    public String getPreviousCotMember() {
        return previousCotMember;
    }

    public void setPreviousCotMember(String previousCotMember) {
        this.previousCotMember = previousCotMember;
    }
}
