/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
package com.atricore.idbus.console.main {
public class AppSectionViewFactory {

    private var _viewName:String;

    public function AppSectionViewFactory(viewName:String) {
        this._viewName = viewName;
    }

    public function get viewName():String {
        return _viewName;
    }

    public function set viewName(value:String):void {
        _viewName = value;
    }

    public function createView():AppSectionView {
        return null;
    }
}
}
