import {createSlice} from "@reduxjs/toolkit";

const initialState = {
    id: null,
};

export const authSlice = createSlice({
    name: "auth",
    initialState,
    reducers: {
        setLogin: (state, action) => {
            state.id = action.payload.id;
        },
        setLogout: (state) => {
            state.id = null;
        },
    }
});

export const {setLogin, setLogout} = authSlice.actions;
export default authSlice.reducer;