import {createSlice, createAsyncThunk} from "@reduxjs/toolkit";
import axios from 'axios';

const initialState = {
    loading: false,
    data: null,
    error: null,
};

// dispatches the lifecycle methods of a promise as actions (pending, fulfilled, rejected)
// TODO: once we have user ID stored in redux after logging in, take out the default userId value here
export const getBoards = createAsyncThunk("board/getBoards", (userId = "63f300a9aa937b2f68a15e23") => {
    return axios
        .get(`http://localhost:8080/boards/byUserId/${userId}`)
        .then((response) => response.data)
});

export const setBoardPrivate = createAsyncThunk("board/setPrivate", (boardId) => {
    return axios
        .patch(`http://localhost:8080/boards/setPrivate/${boardId}`)
        .then((response) => response.data)
});

export const setBoardPublic = createAsyncThunk("board/setPublic", (boardId) => {
    return axios
        .patch(`http://localhost:8080/boards/setPublic/${boardId}`)
        .then((response) => response.data)
});

export const setBoardClosed = createAsyncThunk("board/setClosed", (boardId) => {
    return axios
        .patch(`http://localhost:8080/boards/setClosed/${boardId}`)
        .then((response) => response.data)
});

export const setBoardOpen = createAsyncThunk("board/setOpen", (boardId) => {
    return axios
        .patch(`http://localhost:8080/boards/setOpen/${boardId}`)
        .then((response) => response.data)
});

export const deleteBoard = createAsyncThunk("board/deleteBoard", (boardId) => {
    return axios
        .delete(`http://localhost:8080/boards/${boardId}`)
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
        builder.addCase(setBoardPrivate.pending, (state) => {
            state.loading = true;
        })
        builder.addCase(setBoardPrivate.fulfilled, (state, action) => {
            state.loading = false;
            state.data = action.payload;
            state.error = null;
        })
        builder.addCase(setBoardPrivate.rejected, (state, action) => {
            state.loading = false;
            state.data = null;
            state.error = action.error.message;
        })
        builder.addCase(setBoardPublic.pending, (state) => {
            state.loading = true;
        })
        builder.addCase(setBoardPublic.fulfilled, (state, action) => {
            state.loading = false;
            state.data = action.payload;
            state.error = null;
        })
        builder.addCase(setBoardPublic.rejected, (state, action) => {
            state.loading = false;
            state.data = null;
            state.error = action.error.message;
        })
        builder.addCase(setBoardClosed.pending, (state) => {
            state.loading = true;
        })
        builder.addCase(setBoardClosed.fulfilled, (state, action) => {
            state.loading = false;
            state.data = action.payload;
            state.error = null;
        })
        builder.addCase(setBoardClosed.rejected, (state, action) => {
            state.loading = false;
            state.data = null;
            state.error = action.error.message;
        })
        builder.addCase(setBoardOpen.pending, (state) => {
            state.loading = true;
        })
        builder.addCase(setBoardOpen.fulfilled, (state, action) => {
            state.loading = false;
            state.data = action.payload;
            state.error = null;
        })
        builder.addCase(setBoardOpen.rejected, (state, action) => {
            state.loading = false;
            state.data = null;
            state.error = action.error.message;
        })
        builder.addCase(deleteBoard.pending, (state) => {
            state.loading = true;
        })
        builder.addCase(deleteBoard.fulfilled, (state, action) => {
            state.loading = false;
            state.data = action.payload;
            state.error = null;
        })
        builder.addCase(deleteBoard.rejected, (state, action) => {
            state.loading = false;
            state.data = null;
            state.error = action.error.message;
        })
    }
});
export default boardSlice.reducer;