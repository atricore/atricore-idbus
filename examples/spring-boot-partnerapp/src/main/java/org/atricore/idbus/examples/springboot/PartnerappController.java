package org.atricore.idbus.examples.springboot;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PartnerappController {

    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @RequestMapping("/protected")
    public String protectedPage() {
        return "protected/index";
    }

    @RequestMapping("/protected-josso")
    public String protectedJossoPage() {
        return "protected-josso/index";
    }
}
