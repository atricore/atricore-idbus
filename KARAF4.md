feature:repo-add camel cxf spring-legacy
feature:install camel camel-cxf camel-http cxf spring spring-dm


TEMP FILE FOR NOW
feature:repo-add file:///home/sgonzalez/wa/git/atricore-1.5/distributions/atricore-idbus/src/main/filtered-resources/idbus-features-k4.xml


CAMEL

feature:repo-add camel
Feature camel 2.22.0
Feature depends on:
  camel-core 2.22.0
  camel-blueprint 2.22.0

CXF

feature:repo-add cxf
Feature cxf 3.2.5
Feature depends on:
  cxf-core 3.2.5
  cxf-jaxws 3.2.5
  cxf-jaxrs 3.2.5
  cxf-databinding-jaxb 3.2.5
  cxf-databinding-aegis 3.2.5
  cxf-bindings-corba 3.2.5
  cxf-bindings-coloc 3.2.5
  cxf-http-provider 3.2.5
  cxf-transports-local 3.2.5
  cxf-transports-jms 3.2.5
  cxf-transports-udp 3.2.5
  cxf-xjc-runtime 3.2.5
  cxf-ws-security 3.2.5
  cxf-ws-rm 3.2.5
  cxf-ws-mex 3.2.5
  cxf-javascript 3.2.5
  cxf-frontend-javascript 3.2.5
  cxf-features-clustering 3.2.5
  cxf-features-metrics 3.2.5
  cxf-features-throttling 3.2.5
  cxf-features-logging 3.2.5
  
TODOS

* UPDATE STAX
* IDBUS BOOT (kernel/common)
* WEB-CONSOLE BRANDING (kernel/common)
* Review bundle start-level
