import React from 'react';
import ReactDOM from 'react-dom';

const ResetPasswordMainPage = () => {
    return (<div>It Works!!!</div>)
}

if (document.getElementById('app')) {
    let features = document.getElementById('features').innerHTML
    ReactDOM.render(<ResetPasswordMainPage rawFeatures={features}/>, document.getElementById('app'));
}