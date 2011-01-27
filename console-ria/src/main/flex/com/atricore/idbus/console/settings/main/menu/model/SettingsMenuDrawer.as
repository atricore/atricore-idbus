/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
package com.atricore.idbus.console.settings.main.menu.model {

public class SettingsMenuDrawer extends SettingsMenuContainer {

    public static const INITIAL_STATE_OPEN:int = 0;
    public static const INITIAL_STATE_CLOSED:int = 1;
    public static const INITIAL_STATE_PINNED_OPEN:int = 2;

    private var _initialState:int;
    private var _showDefaultIcon:Boolean;

    public function SettingsMenuDrawer() {
        super();
    }

    public function get initialState() : int {
        return _initialState;
    }

    public function isInitiallyOpen():Boolean {
        return (initialState == INITIAL_STATE_OPEN || initialState == INITIAL_STATE_PINNED_OPEN);
    }

    public function isInitiallyPinned() : Boolean {
        return initialState == INITIAL_STATE_PINNED_OPEN;
    }

    public function isShowDefaultIcon():Boolean {
        return _showDefaultIcon;
    }

    public function setShowDefaultIcon(showDefaultIcon: Boolean):void {
        _showDefaultIcon = showDefaultIcon;
    }
}
}
