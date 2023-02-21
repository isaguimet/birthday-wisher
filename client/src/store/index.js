import storage from "redux-persist/lib/storage"
import {configureStore} from "@reduxjs/toolkit";
import {combineReducers} from "redux";
import {FLUSH, PAUSE, PERSIST, persistReducer, PURGE, REGISTER, REHYDRATE} from "redux-persist";
import user from "./user";
import board from "./board";

const persistConfig = {key: "root", storage, version: 1};

const rootReducer = combineReducers({
    user,
    board,
});

const persistedReducer = persistReducer(persistConfig, rootReducer);

const store = configureStore({
    reducer: persistedReducer,
    middleware: (getDefaultMiddleware) =>
        getDefaultMiddleware({
            serializableCheck: {
                ignoredActions: [FLUSH, REHYDRATE, PAUSE, PERSIST, PURGE, REGISTER],
            },
        }),
});

export default store;