#!/bin/bash

mvn org.heneveld.maven:license-audit-maven-plugin:report \
    -Dformat=csv \
    -DlistDependencyIdOnly=true \
    -DsuppressExcludedDependencies=true \
    -DlicensesPreferred=ASL2,ASL,EPL1,BSD-2-Clause,BSD-3-Clause \
    -DoutputFile=dependencies-licenses.csv
