package org.atricore.idbus.kernel.main.provisioning.spi.policies;

import org.atricore.idbus.kernel.main.authn.PolicyEnforcementStatement;
import org.atricore.idbus.kernel.main.provisioning.spi.PasswordPolicy;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sgonzalez.
 */
public class RegExpPasswordPolicy extends AbstractPasswordPolicy {

    private String stmtName;

    private String expression;

    private Pattern pattern;

    private Matcher matcher;


    public String getStmtName() {
        return stmtName;
    }

    public void setStmtName(String stmtName) {
        this.stmtName = stmtName;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    @Override
    public void init() {
        pattern = Pattern.compile(expression);
    }


    @Override
    public List<PolicyEnforcementStatement> validate(String password) {
        matcher = pattern.matcher(password);
        if (!matcher.matches()) {
            addStatement(new IllegalPasswordStatement(stmtName));
        }

        return getAllStatements();

    }
}
