import React from 'react'
import { login } from '../redux/authSlice'
import { useDispatch } from 'react-redux'
const Home = () => {
    const dispatch = useDispatch();
    return (
        <div>Home</div>
    )
}

export default Home