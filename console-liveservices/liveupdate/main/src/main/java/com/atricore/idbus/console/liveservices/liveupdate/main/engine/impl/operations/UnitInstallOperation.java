package com.atricore.idbus.console.liveservices.liveupdate.main.engine.impl.operations;

import com.atricore.idbus.console.liveservices.liveupdate.main.LiveUpdateException;
import com.atricore.idbus.console.liveservices.liveupdate.main.engine.UpdateContext;
import com.atricore.idbus.console.liveservices.liveupdate.main.engine.OperationStatus;
import com.atricore.liveservices.liveupdate._1_0.md.InstallableUnitType;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public abstract class UnitInstallOperation extends AbstractInstallOperation {

    private String iuGroup;

    private String iuName;

    private String iuVersion;

    public String getIuGroup() {
        return iuGroup;
    }

    public void setIuGroup(String iuGroup) {
        this.iuGroup = iuGroup;
    }

    public String getIuName() {
        return iuName;
    }

    public void setIuName(String iuName) {
        this.iuName = iuName;
    }

    public String getIuVersion() {
        return iuVersion;
    }

    public void setIuVersion(String iuVersion) {
        this.iuVersion = iuVersion;
    }

    public OperationStatus preInstall(UpdateContext ctx) throws LiveUpdateException {
        for (InstallableUnitType iu : ctx.getIUs()) {
            if (iu.getGroup().equals(iuGroup) &&
                    iu.getName().equals(iuName) &&
                    iu.getVersion().equals(iuVersion)) {
                preInstall(ctx, iu);
            }
        }
        return OperationStatus.NEXT;
    }

    public OperationStatus postInstall(UpdateContext ctx) throws LiveUpdateException {
        for (InstallableUnitType iu : ctx.getIUs()) {
            if (iu.getGroup().equals(iuGroup) &&
                    iu.getName().equals(iuName) &&
                    iu.getVersion().equals(iuVersion)) {
                postInstall(ctx, iu);
            }
        }
        return OperationStatus.NEXT;
    }

    public abstract OperationStatus preInstall(UpdateContext ctx, InstallableUnitType iu) throws LiveUpdateException;

    public abstract OperationStatus postInstall(UpdateContext ctx, InstallableUnitType iu) throws LiveUpdateException;
}
