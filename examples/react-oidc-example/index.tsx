import * as React from "react";
import * as ReactDOM from "react-dom";

import { AuthProvider, useAuth } from "react-oidc-context";

const oidcConfig = {
    authority: "http://localhost:8081/IDBUS/MYIAM-03/PARTNERAPP1-OP/OIDC/MD",
    client_id: "m03-cli01",
    redirect_uri: "http://localhost:1234",
};

function App() {
    const auth = useAuth();

    if (auth.isLoading) {
        return <div>Loading...</div>;
    }

    if (auth.error) {
        return <div>Oops... {auth.error.message}</div>;
    }

    if (auth.isAuthenticated) {
        return (
            <div>
                Hello {auth.user?.profile.sub}{" "}
                <button onClick={() => void auth.removeUser()}>
                    Log out
                </button>
            </div>
        );
    }

    return <button onClick={() => void auth.signinRedirect()}>Log in</button>;
}

ReactDOM.render(
    <AuthProvider {...oidcConfig}>
        <App />
    </AuthProvider>,
    document.getElementById("root"),
);
