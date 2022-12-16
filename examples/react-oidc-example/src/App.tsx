import { AuthProvider } from "react-oidc-context";
import './App.css';
import Dashboard from './Dashboard';

const a: string = (process.env.REACT_APP_IDP as string);
const cid: string = (process.env.REACT_APP_CLIENT_ID as string);
const ruri: string = (process.env.REACT_APP_REDIRECT_URI as string);

const oidcConfig = {
  authority: a,
  client_id: cid,
  redirect_uri: ruri,
};


const App = () => {


  return <AuthProvider {...oidcConfig}>
    <Dashboard></Dashboard>
  </AuthProvider>;
};

export default App;

