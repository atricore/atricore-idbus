@echo off
rem
rem
rem    Licensed to the Apache Software Foundation (ASF) under one or more
rem    contributor license agreements.  See the NOTICE file distributed with
rem    this work for additional information regarding copyright ownership.
rem    The ASF licenses this file to You under the Apache License, Version 2.0
rem    (the "License"); you may not use this file except in compliance with
rem    the License.  You may obtain a copy of the License at
rem
rem       http://www.apache.org/licenses/LICENSE-2.0
rem
rem    Unless required by applicable law or agreed to in writing, software
rem    distributed under the License is distributed on an "AS IS" BASIS,
rem    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
rem    See the License for the specific language governing permissions and
rem    limitations under the License.
rem
rem
rem $Id: karaf.bat 979 2005-11-30 22:50:55Z bsnyder $
rem

if not "%ECHO%" == "" echo %ECHO%

setlocal
set DIRNAME=%~dp0%
set PROGNAME=%~nx0%
set ARGS=%*

title Atricore

goto BEGIN

:warn
    echo %PROGNAME%: %*
goto :EOF

:BEGIN

rem # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # #

if not "%KARAF_HOME%" == "" (
    call :warn Ignoring predefined value for KARAF_HOME
)
set KARAF_HOME=%DIRNAME%..
if not exist "%KARAF_HOME%" (
    call :warn KARAF_HOME is not valid: %KARAF_HOME%
    goto END
)

if not "%KARAF_BASE%" == "" (
    if not exist "%KARAF_BASE%" (
       call :warn KARAF_BASE is not valid: %KARAF_BASE%
       goto END
    )
)
if "%KARAF_BASE%" == "" (
  set KARAF_BASE=%KARAF_HOME%
)

:EXECUTE
    start "Atricore" /MIN "%KARAF_HOME%\bin\atricore.bat" server

rem # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # #

:END

endlocal

if not "%PAUSE%" == "" pause

:END_NO_PAUSE