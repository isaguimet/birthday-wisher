import {createSlice, createAsyncThunk} from "@reduxjs/toolkit";
import { combineReducers } from 'redux';
import axios from 'axios';

const initialAuthState = {
    user: null,
}

const initialUserState = {
    loading: false,
    data: null,
    error: null,
};

export const authSlice = createSlice({
    name: "auth",
    initialState:initialAuthState,
    reducers: {
        setLogin: (state, action) => {
            state.user = action.payload.user;
        },
        setLogout: (state) => {
            state.user = null;
        },
    }
});

export const getFriends = createAsyncThunk("users/friendList", (userId="63e947477d6de33dfab29aac") => {
    return axios
        .get(`http://localhost:8080/users/friendList/${userId}`)
        .then((response) => response.data)

});

export const userSlice = createSlice({
    name: "user",
    initialState:initialUserState,
    extraReducers: (builder) => {
        builder.addCase(getFriends.pending, (state) => {
            state.loading = true;
        })
        builder.addCase(getFriends.fulfilled, (state, action) => {
            state.loading = false;
            state.data = action.payload;
            state.error = null;
        })
        builder.addCase(getFriends.rejected, (state, action) => {
            state.loading = false;
            state.data = null;
            state.error = action.error.message;
        })
    }
});

const userReducer = combineReducers({
    auth: authSlice.reducer,
    user: userSlice.reducer,
});

export const {setLogin, setLogout} = authSlice.actions;

export default userReducer;
