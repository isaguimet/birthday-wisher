import {createSlice, createAsyncThunk} from "@reduxjs/toolkit";
import axios from 'axios';

const initialState = {
    loading: false,
    data: null,
    error: null,
};

// dispatches the lifecycle methods of a promise as actions (pending, fulfilled, rejected)
// TODO: once we have user ID stored in redux after logging in, take out the hard-coded userId value here
export const getBoards = createAsyncThunk("board/getBoards", () => {
        return axios
            .get(`http://localhost:8080/boards/byUserId/63f12b1424e25937d0545ac1`)
            .then((response) => response.data)
    });

export const boardSlice = createSlice({
    name: "board",
    initialState,
    extraReducers: (builder) => {
        builder.addCase(getBoards.pending, (state) => {
            state.loading = true;
        })
        builder.addCase(getBoards.fulfilled, (state, action) => {
            state.loading = false;
            state.data = action.payload;
            state.error = null;
        })
        builder.addCase(getBoards.rejected, (state, action) => {
            state.loading = false;
            state.data = null;
            state.error = action.error.message;
        })
    }
});
export default boardSlice.reducer;