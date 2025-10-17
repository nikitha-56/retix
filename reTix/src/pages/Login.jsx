import React, { useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { login } from '../redux/authSlice';
import { useNavigate } from 'react-router-dom';
import { GoogleLogin } from '@react-oauth/google';

const Login = () => {
    const dispatch = useDispatch();
    const isLoggedIn = useSelector((state) => state.auth.isLoggedIn);
    const navigate = useNavigate();

    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');

    const handleLogin = (e) => {
        e.preventDefault();
        dispatch(login({ username, password }));
        navigate('/dashboard');
    };

    const handleGoogleLogin = (response) => {
        if (response.credential) {
            const googleData = response.credential;
            dispatch(login({ user: googleData }));
            navigate('/dashboard');
        }
    };

    if (isLoggedIn) {
        return <p>You are already logged in.</p>;
    }

    return (
        <div>
            <h1>Login Page</h1>
            <form onSubmit={handleLogin}>
                <input
                    type="text"
                    placeholder="Username"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                />
                <br />
                <input
                    type="password"
                    placeholder="Password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                />
                <br />
                <button type="submit">Login</button>
            </form>

            <GoogleLogin
                onSuccess={handleGoogleLogin}
                onError={(error) => console.log("Login Failed:", error)}
            />

            <p>
                Don't have an account? <a href="/register">Register here</a>
            </p>
            <p>
                Forgot your password? <a href="/reset-password">Reset it here</a>
            </p>
        </div>
    );
};

export default Login;
