/*
 * Atricore IDBus
 *
 * Copyright 2009, Atricore Inc.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.atricore.idbus.console.lifecycle.main.util;

public class ApplianceUnitBundleDescriptor {

	private String fileName;
	private String inputFolder;
	private String outputFolder;
	private String idauFolder;
	private static String SEPARATOR = "/";
	
	public String getIdauFolder() {
		return idauFolder;
	}

	public void setIdauFolder(String idauFolder) {
		this.idauFolder = (idauFolder.endsWith(SEPARATOR) ? idauFolder : idauFolder + SEPARATOR);
	}

	public String getFileName() {
		return fileName;
	}
	
	public void setFileName(String fileName) {
		this.fileName = (fileName.endsWith(SEPARATOR) ? fileName : fileName + SEPARATOR);
	}
	
	public String getInputFolder() {
		return inputFolder;
	}
	
	public void setInputFolder(String inputFolder) {
		this.inputFolder = (inputFolder.endsWith(SEPARATOR) ? inputFolder : inputFolder + SEPARATOR);
	}
	
	public String getOutputFolder() {
		return outputFolder;
	}
	
	public void setOutputFolder(String outputFolder) {
		this.outputFolder = (outputFolder.endsWith(SEPARATOR) ? outputFolder : outputFolder + SEPARATOR);
	}
	
	
}
