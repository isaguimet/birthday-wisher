import {createSlice} from "@reduxjs/toolkit";

const initialState = {
    id: null,
    firstName: null,
    lastName: null,
    email: null,
    birthdate: null,
    profilePic: null,
};

export const authSlice = createSlice({
    name: "auth",
    initialState,
    reducers: {
        setLogin: (state, action) => {
            return {...action.payload.user};
        },
        setLogout: (state) => {
            return {...initialState};
        },
    }
});

export const {setLogin, setLogout} = authSlice.actions;
export default authSlice.reducer;