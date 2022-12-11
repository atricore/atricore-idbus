import React, { useReducer } from 'react';

import {IAMTFconfiguration} from './configuration';

import logo from './logo.svg';
import { BrowserRouter, NavLink, Route, Routes } from 'react-router-dom';
import './App.css';
import { Home } from './Home';


import { render } from "react-dom";
import { OidcProvider } from "@axa-fr/react-oidc";


const getRandomInt = (max: number) => {
  return Math.floor(Math.random() * max);
};


function reducer(state: any, action: any) {
  switch (action.type) {
    case 'event':
      {
        const id = getRandomInt(9999999999999).toString();
        return [{ ...action.data, id, date: Date.now() }, ...state];
      }
    default:
      throw new Error();
  }
}

function App() {

  const [show, setShow] = React.useState(false);
  const [events, dispatch] = useReducer(reducer, []);

  const onEvent = (configurationName: any, eventName: any, data: any) => {
     console.log(`oidc:${configurationName}:${eventName}`, data);
     dispatch({ type: 'event', data: { name: `oidc:${configurationName}:${eventName}`, data } });
   };

  return (

    <OidcProvider configuration={IAMTFconfiguration} onEvent={onEvent}>
    <BrowserRouter>
      <nav className="navbar navbar-expand-lg navbar-dark bg-primary">
        <a className="navbar-brand" href="/">@axa-fr/react-oidc</a>
        <button className="navbar-toggler" type="button" onClick={() => setShow(!show)} data-toggle="collapse" data-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
          <span className="navbar-toggler-icon"/>
        </button>
        <div style={show ? { display: 'block' } : { display: 'none' }} className="collapse navbar-collapse" id="navbarNav">
          <ul className="navbar-nav">
            <li className="nav-item">
              <NavLink className="nav-link" to="/">Home</NavLink>
            </li>
            <li className="nav-item">
              <NavLink className="nav-link" to="/profile">Profile</NavLink>
            </li>
            <li className="nav-item">
              <NavLink className="nav-link" to="/profile-secure-component">Secure Profile Component</NavLink>
            </li>
            <li className="nav-item">
              <NavLink className="nav-link" to="/profile-secure-hoc">Secure Profile Hoc</NavLink>
            </li>
            <li className="nav-item">
              <NavLink className="nav-link" to="/user-fetch-secure-hoc">Secure User Fetch Hoc</NavLink>
            </li>
            <li className="nav-item">
              <NavLink className="nav-link" to="/user-fetch-secure-hook">Secure User Fetch Hook</NavLink>
            </li>
            <li className="nav-item">
              <NavLink className="nav-link" to="/multi-auth">Multi Auth</NavLink>
            </li>
          </ul>
        </div>
      </nav>

      <div>
        <Routes>
          <Route path="/" element={<Home></Home>} />
        </Routes>
      </div>

    </BrowserRouter>
  </OidcProvider>
    /*
    <div className="App">
      <header className="App-header">
        <img src={logo} className="App-logo" alt="logo" />
        <p>
          Edit <code>src/App.tsx</code> and save to reload.
        </p>
        <a
          className="App-link"
          href="https://reactjs.org"
          target="_blank"
          rel="noopener noreferrer"
        >
          Learn React
        </a>
      </header>
    </div>
    */
  );
}

export default App;
