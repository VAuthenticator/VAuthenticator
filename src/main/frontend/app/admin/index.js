import React from 'react';
import ReactDOM from 'react-dom';
import {withStyles} from "@material-ui/core";

import vauthenticatorStyles from "../component/styles";
import {HashRouter} from "react-router-dom";
import {Route, Switch} from "react-router";
import ClientAppListPage from "./clientapp/ClientAppListPage";
import ClientAppManagementPage from "./clientapp/ClientAppManagementPage";

const VAuthenticatorAdminApp = withStyles(vauthenticatorStyles)((props) => {
    console.log(props)
    return (
        <HashRouter>
            <Switch>
                <Route exact={true} path="/"
                       render={(props) => <ClientAppListPage {...props} />}/>

                <Route exact={true} path="/client-application"
                       render={(props) => <ClientAppListPage {...props} />}/>

                <Route exact={true} path="/client-application/:clientAppId"
                       render={(props) => <ClientAppManagementPage {...props} />}/>
            </Switch>
        </HashRouter>)

})

if (document.getElementById('app')) {
    ReactDOM.render(<VAuthenticatorAdminApp/>, document.getElementById('app'));
}